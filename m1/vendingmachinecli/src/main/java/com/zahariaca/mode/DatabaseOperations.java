package com.zahariaca.mode;

import com.zahariaca.cli.PrimaryCli;
import com.zahariaca.dao.Dao;
import com.zahariaca.dao.DaoFactory;
import com.zahariaca.pojo.Product;
import com.zahariaca.pojo.ProductTransaction;
import com.zahariaca.pojo.users.User;
import com.zahariaca.threads.CliRunnable;
import com.zahariaca.threads.VendingMachineRunnable;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import com.zahariaca.threads.events.TransactionWriterOperationType;
import com.zahariaca.vendingmachine.OperatorInteractions;
import com.zahariaca.vendingmachine.VendingMachineInteractions;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 18.12.2018
 */
public class DatabaseOperations implements Operations {
    private final Logger logger = LogManager.getLogger(DatabaseOperations.class);
    private final BlockingQueue<OperationsEvent<OperationType, String[]>> commandQueue = new LinkedBlockingQueue<>(1);
    private final BlockingQueue<OperationsEvent<ResultOperationType, String>> resultQueue = new LinkedBlockingQueue<>(1);
    private PrimaryCli primaryCli;
    private OperatorInteractions<Product, String[]> vendingMachine;
    private Dao<User, String> usersDao;
    private Dao<Product, Integer> vendingMachineDao;
    private Dao<ProductTransaction, String> transactionsDao;

    @Override
    public void startUp() {
        DaoFactory<Dao<Product, Integer>, Dao<User, String>, Dao<ProductTransaction, String>> daoFactory = DaoFactory.makeMySqlDaoFactory();

        vendingMachineDao = daoFactory.getVendingMachineDao();
        transactionsDao = daoFactory.getTransactionsDao();
        vendingMachine = new VendingMachineInteractions(vendingMachineDao, transactionsDao);

        usersDao = daoFactory.getUserDao();

        primaryCli = new PrimaryCli(commandQueue, resultQueue);

        logger.log(Level.INFO, ">O: Prerequisites created...");
        ExecutorService taskExecutor = Executors.newFixedThreadPool(2);

        taskExecutor.execute(VendingMachineRunnable.makeDatabaseVendingMachineRunnable(commandQueue, resultQueue, vendingMachine, usersDao));
        logger.log(Level.INFO, ">O: VendingMachine thread started.");

        taskExecutor.execute(new CliRunnable(primaryCli));
        logger.log(Level.INFO, ">O: CLI thread started.");

        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            logger.log(Level.ERROR, "One of the threads was interrupted! {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
