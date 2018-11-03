package com.zahariaca;

import com.zahariaca.dao.Dao;
import com.zahariaca.loader.FileLoader;
import com.zahariaca.pojo.Product;
import com.zahariaca.threads.CLIRunnable;
import com.zahariaca.threads.VendingMachineRunnable;
import com.zahariaca.vendingmachine.VendingMachineDao;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class Main {
    private static Logger logger = LogManager.getLogger(Main.class);
    private static BlockingQueue<OperationsEvent<OperationType, String>> commandQueue = new LinkedBlockingQueue<>(10);

    public static void main(String[] args) {
        logger.log(Level.INFO, ">O: Application startup...");
        System.out.println(String.format("%s%n%s%n%s",
                "+++++++++++++++++++++++++++++",
                "+    VENDING MACHINE CLI    +",
                "+++++++++++++++++++++++++++++"));

        System.out.println("Starting up...");

        ClassLoader classLoader = Main.class.getClassLoader();
        URL fileUrl = classLoader.getResource("products.json");
        File file = null;
        if (fileUrl != null) {
            file = new File(fileUrl.getFile());
        }

        //TODO: Handle what happenes if there is no file or no products in file.
        Dao<Product, String> vendingMachine = new VendingMachineDao(FileLoader.INSTANCE.loadFromFile(file));

        logger.log(Level.INFO, ">O: Prerequisites created...");
        new Thread(new VendingMachineRunnable(commandQueue, vendingMachine)).start();
        logger.log(Level.INFO, ">O: VendingMachine thread started.");
        new Thread(new CLIRunnable(commandQueue)).start();
        logger.log(Level.INFO, ">O: CLI thread started.");
    }


}
