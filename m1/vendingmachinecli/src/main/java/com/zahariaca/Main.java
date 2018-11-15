package com.zahariaca;

import com.zahariaca.dao.Dao;
import com.zahariaca.loader.ProductFileLoader;
import com.zahariaca.pojo.Product;
import com.zahariaca.threads.CLIRunnable;
import com.zahariaca.threads.TransactionsWriterRunnable;
import com.zahariaca.threads.VendingMachineRunnable;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import com.zahariaca.threads.events.TransactionWriterOperationType;
import com.zahariaca.utils.FileUtils;
import com.zahariaca.vendingmachine.VendingMachineDao;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final BlockingQueue<OperationsEvent<OperationType, String>> commandQueue = new LinkedBlockingQueue<>(1);
    private static final BlockingQueue<OperationsEvent<ResultOperationType, Product>> resultQueue = new LinkedBlockingQueue<>(1);
    private static final BlockingQueue<OperationsEvent<TransactionWriterOperationType, Product>> transactionsQueue = new LinkedBlockingQueue<>(10);

    public static void main(String[] args) {
        logger.log(Level.INFO, ">O: Application startup...");
        // TODO: Maybe do some startup option, option1: cli-file, option2: cli-db, file save locations, etc
        System.out.println(String.format("%s%n%s%n%s",
                "+++++++++++++++++++++++++++++",
                "+    VENDING MACHINE CLI    +",
                "+++++++++++++++++++++++++++++"));

        System.out.println("Starting up...");

        File file = FileUtils.INSTANCE.getFile("persistence/products.json");
        Set<Product> loadedProducts = ProductFileLoader.INSTANCE.loadFromFile(file);
        OptionalInt largestIdOptional = loadedProducts.stream().mapToInt(Product::getUniqueId).max();

        Product.setIdGenerator(new AtomicInteger(largestIdOptional.orElse(1000)));
        Dao<Product, String> vendingMachine = new VendingMachineDao(loadedProducts);

        logger.log(Level.INFO, ">O: Prerequisites created...");
        new Thread(new VendingMachineRunnable(commandQueue, resultQueue, transactionsQueue, vendingMachine)).start();
        logger.log(Level.INFO, ">O: VendingMachine thread started.");
        new Thread(new CLIRunnable(commandQueue, resultQueue)).start();
        logger.log(Level.INFO, ">O: CLI thread started.");
        new Thread(new TransactionsWriterRunnable(transactionsQueue)).start();
        logger.log(Level.INFO, ">O: Transactions writer thread started.");
    }


}
