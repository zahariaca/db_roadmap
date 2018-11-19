package com.zahariaca.threads;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zahariaca.pojo.Product;
import com.zahariaca.pojo.ProductTransaction;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.TransactionWriterOperationType;
import com.zahariaca.utils.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 03.11.2018
 */
//TODO: think of a way to not have this thread always running. seems wasteful.
// thread should handle the operation then exit. Then a new thread starts when
// when an operation is needed. Needs to be thread safe
public class TransactionsWriterRunnable implements Runnable {
    private final Logger logger = LogManager.getLogger(TransactionsWriterRunnable.class);
    private final BlockingQueue<OperationsEvent<TransactionWriterOperationType, Product>> transactionsQueue;
    private volatile boolean continueCondition = true;

    public TransactionsWriterRunnable(BlockingQueue<OperationsEvent<TransactionWriterOperationType, Product>> transactionsQueue) {
        this.transactionsQueue = transactionsQueue;
    }

    @Override
    public void run() {
        try {
            while (continueCondition) {
                registerTransaction();
            }
        } catch (InterruptedException ex) {
            logger.log(Level.DEBUG, ">E: Thread is interrupted. terminating");
            Thread.currentThread().interrupt();
        }
    }

    private void registerTransaction() throws InterruptedException {
        OperationsEvent<TransactionWriterOperationType, Product> transactionWriterOperation = transactionsQueue.take();
        if (transactionWriterOperation.getType().equals(TransactionWriterOperationType.QUIT)) {
            continueCondition = false;
            return;
        }

        Product product = transactionWriterOperation.getPayload();
        ProductTransaction productTransaction = new ProductTransaction(
                product.getUniqueId(),
                product.getPrice(),
                Date.from(Instant.now()).getTime());

        String transactionString = transformToJsonString(productTransaction);

        handleFileWrite(transactionString);
    }

    private String transformToJsonString(ProductTransaction productTransaction) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(productTransaction);
    }

    private void handleFileWrite(String transactionString) {
        File file = FileUtils.INSTANCE.getFile("persistence/transactions.json");

        Path path = Paths.get(file.toURI());
        try {
            String transactionLine = transactionString + System.lineSeparator();
            Files.write(path, transactionLine.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.log(Level.ERROR, e.getMessage());
            logger.log(Level.DEBUG, "Could not write: {}", transactionString);
        }
    }


}
