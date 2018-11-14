package com.zahariaca.threads;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zahariaca.dao.Dao;
import com.zahariaca.exceptions.NoSuchProductException;
import com.zahariaca.exceptions.ProductAlreadyExistsException;
import com.zahariaca.loader.ProductFileWriter;
import com.zahariaca.pojo.Product;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import com.zahariaca.threads.events.TransactionWriterOperationType;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 30.10.2018
 */
public class VendingMachineRunnable implements Runnable {
    private final Logger logger = LogManager.getLogger(VendingMachineRunnable.class);
    private final Dao<Product, String> vendingMachine;
    private final BlockingQueue<OperationsEvent<OperationType, String>> commandQueue;
    private final BlockingQueue<OperationsEvent<ResultOperationType, Product>> resultQueue;
    private final BlockingQueue<OperationsEvent<TransactionWriterOperationType, Product>> transactionsQueue;
    private volatile boolean continueCondition = true;

    public VendingMachineRunnable(BlockingQueue<OperationsEvent<OperationType, String>> commandQueue,
                                  BlockingQueue<OperationsEvent<ResultOperationType, Product>> resultQueue,
                                  BlockingQueue<OperationsEvent<TransactionWriterOperationType, Product>> transactionsQueue,
                                  Dao<Product, String> vendingMachine) {
        this.commandQueue = commandQueue;
        this.transactionsQueue = transactionsQueue;
        this.vendingMachine = vendingMachine;
        this.resultQueue = resultQueue;
        logger.log(Level.INFO, ">O: instantiated");
    }

    @Override
    public void run() {
        try {
            while (continueCondition) {
                logger.log(Level.DEBUG, ">O: infinite loop enter.");
                OperationsEvent<OperationType, String> receivedEvent = commandQueue.take();
                if (receivedEvent.getType().equals(OperationType.DISPLAY)) {
                    handleDisplayProcess(receivedEvent);
                } else if (receivedEvent.getType().equals(OperationType.BUY)) {
                    handleBuyProcess(receivedEvent);
                } else if (receivedEvent.getType().equals(OperationType.ADD)) {
                    handleAddEvent(receivedEvent.getPayload());
                } else if (receivedEvent.getType().equals(OperationType.QUIT)) {
                    handleShutdown();
                }
            }
            logger.log(Level.DEBUG, ">O: infinite loop exit.");
        } catch (InterruptedException e) {
            logger.log(Level.ERROR, ">O: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void handleDisplayProcess(OperationsEvent<OperationType, String> receivedEvent) {
        System.out.println(Thread.currentThread() + " >> DISPLAY >> " + receivedEvent.getPayload());
        vendingMachine.displayProducts();
    }

    private void handleBuyProcess(OperationsEvent<OperationType, String> receivedEvent) throws InterruptedException {
        System.out.println(Thread.currentThread() + " >> BUY >> " + receivedEvent.getPayload());
        try {
            sendProductToClient(receivedEvent.getPayload());
        } catch (NoSuchProductException e) {
            logger.log(Level.WARN, ">E: Product does not exist. " + e.getMessage());
            handleNoProduct();
        }
    }


    private void handleAddEvent(String payload) {
        try {
            vendingMachine.addProduct(deserializeJsonProduct(payload));
        } catch (ProductAlreadyExistsException e) {
            logger.log(Level.ERROR, ">E: Message: ", e.getMessage());
            //TODO: add event to result queue, to let supplier know of the problem, delete syso
            System.out.println("The product already exists");
        }
    }

    private void handleShutdown() throws InterruptedException {
        handleTransactionQueueShutdown();
        handleFinalOperation();
    }



    private void handleTransactionQueueShutdown() throws InterruptedException {
        addEventToTransactionsQueue(null, TransactionWriterOperationType.QUIT);
    }

    private void handleFinalOperation() {
        try {
            ProductFileWriter.INSTANCE.handleFileWrite(vendingMachine.getProductsSet());
            System.out.println("Application terminating gracefully!");
            System.out.println("Goodbye!");
            continueCondition = false;
        } catch (IOException e) {
            logger.log(Level.ERROR, ">E: Could not write persistence file on shutdown. Message: {} ", e.getMessage());
            System.out.println("Application terminating without saving products! Error occurred!");
            System.out.println("Goodbye!");
            continueCondition = false;
        }
    }


    private void sendProductToClient(String payload) throws NoSuchProductException, InterruptedException {
        Product returnedProduct = vendingMachine.buyProduct(payload);
        System.out.println("Delivering your product: " + returnedProduct);
        addEventToResultQueue(returnedProduct, ResultOperationType.RETURN_PRODUCT);
        addEventToTransactionsQueue(returnedProduct, TransactionWriterOperationType.WRITE);
    }

    private void handleNoProduct() throws InterruptedException {
        addEventToResultQueue(null, ResultOperationType.PRODUCT_NOT_FOUND);
    }


    private Product deserializeJsonProduct(String payload) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(payload, new TypeToken<Product>(){}.getType());
    }

    private void addEventToResultQueue(Product returnedProduct, ResultOperationType resultOperationType) throws InterruptedException {
        resultQueue.put(new OperationsEvent<ResultOperationType, Product>() {
            @Override
            public ResultOperationType getType() {
                return resultOperationType;
            }
            @Override
            public Product getPayload() {
                return returnedProduct;
            }
        });
    }

    private void addEventToTransactionsQueue(Product returnedProduct, TransactionWriterOperationType writeOperation) throws InterruptedException {
        transactionsQueue.put(new OperationsEvent<TransactionWriterOperationType, Product>() {
            @Override
            public TransactionWriterOperationType getType() {
                return writeOperation;
            }

            @Override
            public Product getPayload() {
                return returnedProduct;
            }
        });
    }
}
