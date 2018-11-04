package com.zahariaca.threads;

import com.zahariaca.dao.Dao;
import com.zahariaca.exceptions.NoSuchProductException;
import com.zahariaca.pojo.Product;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 30.10.2018
 */
public class VendingMachineRunnable implements Runnable {
    private final Logger logger = LogManager.getLogger(VendingMachineRunnable.class);
    private final Dao<Product, String> vendingMachine;
    private final BlockingQueue<OperationsEvent<OperationType, String>> commandQueue;
    private final BlockingQueue<OperationsEvent<ResultOperationType, Product>> resultQueue;
    private boolean exitCondition = true;

    public VendingMachineRunnable(BlockingQueue<OperationsEvent<OperationType, String>> commandQueue,
                                  BlockingQueue<OperationsEvent<ResultOperationType, Product>> resultQueue,
                                  Dao<Product, String> vendingMachine) {
        this.commandQueue = commandQueue;
        this.vendingMachine = vendingMachine;
        this.resultQueue = resultQueue;
        logger.log(Level.INFO, ">O: instantiated");
    }

    @Override
    public void run() {
        try {
            while (exitCondition) {
                logger.log(Level.DEBUG, ">O: infinite loop enter.");
                OperationsEvent<OperationType, String> receivedEvent = commandQueue.take();
                if (receivedEvent.getType().equals(OperationType.DISPLAY)) {
                    System.out.println(Thread.currentThread() + " >> DISPLAY >> " + receivedEvent.getPayload());
                    vendingMachine.displayProducts();
                } else if (receivedEvent.getType().equals(OperationType.BUY)) {
                    System.out.println(Thread.currentThread() + " >> BUY >> " + receivedEvent.getPayload());

                    try {
                        sendProductToClient(receivedEvent.getPayload());
                    } catch (NoSuchProductException e) {
                        logger.log(Level.WARN, ">E: Product does not exist. " + e.getMessage());
                        handleNoProduct();
                    }

                } else if (receivedEvent.getType().equals(OperationType.QUIT)) {
                    System.out.println(Thread.currentThread() + " >> QUIT >> " + receivedEvent.getPayload());
                    System.out.println("Goodbye!");
                    exitCondition = false;
                }
            }
            logger.log(Level.DEBUG, ">O: infinite loop exit.");
        } catch (InterruptedException e) {
            logger.log(Level.ERROR, ">O: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void handleNoProduct() throws InterruptedException {
        resultQueue.put(new OperationsEvent<ResultOperationType, Product>() {
            @Override
            public ResultOperationType getType() {
                return ResultOperationType.PRODUCT_NOT_FOUND;
            }

            @Override
            public Product getPayload() {
                return null;
            }
        });
    }

    private void sendProductToClient(String payload) throws NoSuchProductException, InterruptedException {
        Product returnedProduct = vendingMachine.buyProduct(payload);
        System.out.println("Delivering your product: " + returnedProduct);
        resultQueue.put(new OperationsEvent<ResultOperationType, Product>() {
            @Override
            public ResultOperationType getType() {
                return ResultOperationType.RETURN_PRODUCT;
            }

            @Override
            public Product getPayload() {
                return returnedProduct;
            }
        });
        new Thread(new TransactionsWriterRunnable(returnedProduct)).start();
    }
}
