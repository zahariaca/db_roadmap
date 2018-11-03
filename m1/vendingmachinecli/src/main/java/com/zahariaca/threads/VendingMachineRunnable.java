package com.zahariaca.threads;

import com.zahariaca.dao.Dao;
import com.zahariaca.pojo.Product;
import com.zahariaca.pojo.ProductTransaction;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 30.10.2018
 */
public class VendingMachineRunnable implements Runnable {
    private final Logger logger = LogManager.getLogger(VendingMachineRunnable.class);
    private final Dao<Product, String> vendingMachine;
    private final BlockingQueue<OperationsEvent<OperationType, String>> commandQueue;
    //    private final BlockingQueue<Product> resultQueue;
    private boolean exitCondition = true;

    public VendingMachineRunnable(BlockingQueue<OperationsEvent<OperationType, String>> commandQueue, Dao vendingMachine) {
        this.commandQueue = commandQueue;
        this.vendingMachine = vendingMachine;
        logger.log(Level.INFO, ">O: instantiated");
    }

    @Override
    public void run() {
        try {
            while (exitCondition) {
                logger.log(Level.DEBUG, ">O: infinite loop enter.");
                OperationsEvent<OperationType, String> test = commandQueue.take();
                if (test.getType().equals(OperationType.DISPLAY)) {
                    System.out.println(Thread.currentThread() + " >> DISPLAY >> " + test.getPayload());
                    vendingMachine.displayProducts();
                } else if (test.getType().equals(OperationType.BUY)) {
                    System.out.println(Thread.currentThread() + " >> BUY >> " + test.getPayload());
                    Optional<Product> product = Optional.ofNullable(vendingMachine.buyProduct(test.getPayload()));
                    if (product.isPresent()) {
                        System.out.println("Delivering your product: " + product.get());
//                        resultQueue.put(product.get());
                        new Thread(new TransactionsWritterRunnable(product.get())).start();
                        //TODO: Use some mechanism to get the result back to CLI Thread.1
                    } else {
                        System.out.println("No such product. Please try again!");
                    }

                } else if (test.getType().equals(OperationType.DELETE)) {
                    System.out.println(Thread.currentThread() + " >> DELETE >> " + test.getPayload());
//                    vendingMachine.deleteProduct();
                } else if (test.getType().equals(OperationType.QUIT)) {
                    System.out.println(Thread.currentThread() + " >> QUIT >> " + test.getPayload());
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
}
