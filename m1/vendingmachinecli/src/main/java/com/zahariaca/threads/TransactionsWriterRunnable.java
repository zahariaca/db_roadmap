package com.zahariaca.threads;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zahariaca.pojo.Product;
import com.zahariaca.pojo.ProductTransaction;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 03.11.2018
 */
//TODO: make it thread safe
public class TransactionsWriterRunnable implements Runnable {
    private final Logger logger = LogManager.getLogger(TransactionsWriterRunnable.class);
    private final Product transactionProduct;

    TransactionsWriterRunnable(Product transactionProduct) {
        this.transactionProduct = transactionProduct;
    }

    @Override
    public void run() {

        registerTransaction();
    }

    private void registerTransaction() {
        ProductTransaction productTransaction = new ProductTransaction(transactionProduct.getUniqueId(), transactionProduct.getPrice());

        File file = new File("persistence/transactions.json");

        boolean firstWrite = false;

        try {
            firstWrite = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (firstWrite) {
            logger.log(Level.INFO, "File: {} does not exist. Creating.", file.getName());
        }

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String transactionString = gson.toJson(productTransaction);

        Path path = Paths.get(file.toURI());
        try {
            String transactionLine = transactionString + System.lineSeparator();
            Files.write(path, transactionLine.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
