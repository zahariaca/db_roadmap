package com.zahariaca.threads;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zahariaca.pojo.Product;
import com.zahariaca.pojo.ProductTransaction;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 03.11.2018
 */
public class TransactionsWritterRunnable implements Runnable {
    private final Logger logger = LogManager.getLogger(TransactionsWritterRunnable.class);
    private final Product transactionProduct;
    private boolean exitCondition = true;

    public TransactionsWritterRunnable(Product transactionProduct) {
        this.transactionProduct = transactionProduct;
    }

    @Override
    public void run() {

        registerTransaction();
    }

    private void registerTransaction() {
        //TODO: write transaction to transactions file
        ProductTransaction productTransaction = new ProductTransaction(transactionProduct.getUniqueId(), transactionProduct.getPrice());

        File file = new File("persistence/transactions.json");

        try {
            if (file.createNewFile()) {
                logger.log(Level.INFO, "File: {} does not exist. Creating.", file.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String transactionString = gson.toJson(productTransaction);

        Path path = Paths.get(file.toURI());
        try {
            Files.write(path, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
            Files.write(path, transactionString.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
