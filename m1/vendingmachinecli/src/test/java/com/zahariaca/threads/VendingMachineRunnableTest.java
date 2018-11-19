package com.zahariaca.threads;

import com.zahariaca.dao.Dao;
import com.zahariaca.dao.UserDao;
import com.zahariaca.dao.VendingMachineDao;
import com.zahariaca.pojo.Product;
import com.zahariaca.pojo.users.User;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import com.zahariaca.threads.events.TransactionWriterOperationType;
import com.zahariaca.vendingmachine.OperatorInteractions;
import com.zahariaca.vendingmachine.VendingMachineInteractions;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 20.11.2018
 */
public class VendingMachineRunnableTest {
    private VendingMachineRunnable vendingMachineRunnable;
    private BlockingQueue<OperationsEvent<OperationType, String[]>> commandQueue;
    private BlockingQueue<OperationsEvent<ResultOperationType, String>> resultQueue;
    private BlockingQueue<OperationsEvent<TransactionWriterOperationType, Product>> transactionsQueue;
    private OperatorInteractions<Product, String[]> vendingMachine;
    private Dao<Product, Integer> vendingMachineDao;
    private Dao<User, String> userDao;
    private Set<User> userSet;
    private Set<Product> productSet;

    @BeforeEach
    void init() {
        commandQueue = new LinkedBlockingQueue<>(1);
        resultQueue = spy(new LinkedBlockingQueue<>(1));
        transactionsQueue = spy(new LinkedBlockingQueue<>(10));
        User supplier = new User("admin", "admin", true);
        userSet.add(supplier);
        userDao = new UserDao(userSet);
        productSet.add(new Product("Product name", "Product Description", 6.77f, supplier.getUserId()));
        vendingMachineDao = new VendingMachineDao(productSet);
        vendingMachine = new VendingMachineInteractions(vendingMachineDao);
        vendingMachineRunnable = new VendingMachineRunnable(commandQueue, resultQueue, transactionsQueue, vendingMachine, userDao);
    }

    @Test
    @Ignore
    void testQuit() throws InterruptedException {
        Thread t = new Thread(vendingMachineRunnable);
//        t.start();

        fail();
        commandQueue.put(new OperationsEvent<OperationType, String[]>() {
            @Override
            public OperationType getType() {
                return OperationType.QUIT;
            }

            @Override
            public String[] getPayload() {
                return new String[0];
            }
        });

        doNothing().when(transactionsQueue).put(new OperationsEvent<TransactionWriterOperationType, Product>() {
            @Override
            public TransactionWriterOperationType getType() {
                return null;
            }

            @Override
            public Product getPayload() {
                return null;
            }
        });
    }
}
