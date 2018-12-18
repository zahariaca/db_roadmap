package com.zahariaca.mode;

import com.google.gson.reflect.TypeToken;
import com.zahariaca.cli.PrimaryCli;
import com.zahariaca.dao.Dao;
import com.zahariaca.dao.UserDao;
import com.zahariaca.dao.VendingMachineDao;
import com.zahariaca.filehandlers.PersistenceFileLoader;
import com.zahariaca.filehandlers.PersistenceFileWriter;
import com.zahariaca.pojo.Product;
import com.zahariaca.pojo.users.User;
import com.zahariaca.threads.CliRunnable;
import com.zahariaca.threads.TransactionsWriterRunnable;
import com.zahariaca.threads.VendingMachineRunnable;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import com.zahariaca.threads.events.TransactionWriterOperationType;
import com.zahariaca.utils.FileUtils;
import com.zahariaca.vendingmachine.OperatorInteractions;
import com.zahariaca.vendingmachine.VendingMachineInteractions;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 21.11.2018
 */
public class FileOperations implements Operations {
    private final Logger logger = LogManager.getLogger(FileOperations.class);
    private final BlockingQueue<OperationsEvent<OperationType, String[]>> commandQueue = new LinkedBlockingQueue<>(1);
    private final BlockingQueue<OperationsEvent<ResultOperationType, String>> resultQueue = new LinkedBlockingQueue<>(1);
    private final BlockingQueue<OperationsEvent<TransactionWriterOperationType, Product>> transactionsQueue = new LinkedBlockingQueue<>(10);
    private PrimaryCli primaryCli;
    private OperatorInteractions<Product, String[]> vendingMachine;
    private Dao<User, String> usersDao;
    private String productsPath;
    private String usersPath;
    private String transactionsPath;

    public FileOperations(String productsPath, String usersPath, String transactionsPath) {
        this.productsPath = productsPath;
        this.usersPath = usersPath;
        this.transactionsPath = transactionsPath;
    }

    @Override
    public void startUp() {
        File productsFile = FileUtils.INSTANCE.getFile(productsPath);
        Set<Product> loadedProducts = PersistenceFileLoader.INSTANCE.loadProductsFromFile(productsFile);
        OptionalInt largestIdOptional = loadedProducts.stream().mapToInt(Product::getUniqueId).max();

        Product.setIdGenerator(new AtomicInteger(largestIdOptional.orElse(1000)));
        Dao<Product, Integer> vendingMachineDao = new VendingMachineDao(loadedProducts);
        vendingMachine = new VendingMachineInteractions(vendingMachineDao);

        File usersFile = FileUtils.INSTANCE.getFile(usersPath);
        Set<User> loadedUsers = PersistenceFileLoader.INSTANCE.loadFromFile(usersFile, new TypeToken<TreeSet<User>>() {
        });

        usersDao = new UserDao(loadedUsers);

        primaryCli = new PrimaryCli(commandQueue, resultQueue);

        File transactionsFile = FileUtils.INSTANCE.getFile(transactionsPath);
        logger.log(Level.INFO, ">O: Prerequisites created...");

        ExecutorService taskExecutor = Executors.newFixedThreadPool(3);

        // TODO: Builder?
        taskExecutor.execute(VendingMachineRunnable.makeFileVendingMachineRunnable(commandQueue, resultQueue, transactionsQueue, vendingMachine, usersDao));
        logger.log(Level.INFO, ">O: VendingMachine thread started.");

        taskExecutor.execute(new CliRunnable(primaryCli));
        logger.log(Level.INFO, ">O: CLI thread started.");

        taskExecutor.execute(new TransactionsWriterRunnable(transactionsQueue, transactionsFile));
        logger.log(Level.INFO, ">O: Transactions writer thread started.");

        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            handleFinalOperation();
        } catch (InterruptedException e) {
            logger.log(Level.ERROR, "One of the threads was interrupted! {}", e.getMessage());
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.log(Level.ERROR, ">E: Could not write persistence file on shutdown. Message: {} ", e.getMessage());
            System.out.println("Application terminating without saving products! Error occurred!");
            System.out.println("Goodbye!");
        }
    }

    private void handleFinalOperation() throws IOException {
        File productsFile = FileUtils.INSTANCE.getFile(productsPath);
        PersistenceFileWriter.INSTANCE.handleFileWrite(productsFile, vendingMachine.getProductsSet());
        File usersFile = FileUtils.INSTANCE.getFile(usersPath);
        PersistenceFileWriter.INSTANCE.handleFileWrite(usersFile, usersDao.getAll());
        System.out.println("Application terminating gracefully!");
        System.out.println("Goodbye!");
    }

}
