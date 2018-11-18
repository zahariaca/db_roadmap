package com.zahariaca.users;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import com.zahariaca.exceptions.UserInUnsafeStateException;
import com.zahariaca.pojo.Product;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 14.11.2018
 */
class CustomerTest {
    private InputStream stdin;
    private User<BlockingQueue<OperationsEvent<OperationType, String[]>>, BlockingQueue<OperationsEvent<ResultOperationType, String>>> customer;
    private Product sodaProduct;
    private BlockingQueue<OperationsEvent<OperationType, String[]>> commandQueue;
    private BlockingQueue<OperationsEvent<ResultOperationType, String>> resultQueue;

    @BeforeEach
    void init() {
        stdin = System.in;
        customer = new Customer();
        String supplierOneUUID = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918";
        sodaProduct = new Product("Soda", "Sugary refreshing beverage", 5.6f, supplierOneUUID);
        commandQueue = new LinkedBlockingQueue<>(1);
        BlockingQueue<OperationsEvent<ResultOperationType, String>> realResultQueue = new LinkedBlockingQueue<>(1);
        resultQueue = spy(realResultQueue);
        customer.setCommandQueue(commandQueue);
        customer.setResultQueue(resultQueue);
    }

    @Test
    void testEmptyQueue() {
        customer = new Customer();
        assertThrows(UserInUnsafeStateException.class, () -> customer.promptUserOptions(), "Empty Queues results in RuntimeException");
    }

    @Test
    void testDisplayOption() throws InterruptedException {
        Thread t = new Thread(getDisplayRunnable());
        t.start();
        // TODO: FIXME when time permits...
        Thread.sleep(1000);
        OperationsEvent<OperationType, String[]> commandEvent = commandQueue.take();
        assertEquals(commandEvent.getType(), OperationType.DISPLAY);
        assertEquals(commandEvent.getPayload()[0], "");
        verify(resultQueue, times(1)).take();
    }

    @Test
    void testBuyOption() throws InterruptedException {
        Thread t = new Thread(getBuyRunnable());
        t.start();
        // TODO: FIXME when time permits...
        Thread.sleep(1000);
        OperationsEvent<OperationType, String[]> commandEvent = commandQueue.take();
        assertEquals(commandEvent.getType(), OperationType.BUY);
        assertEquals(commandEvent.getPayload()[0], "Soda");
        verify(resultQueue, times(1)).take();

    }

    private Runnable getDisplayRunnable() {
        return () -> {
            String dummySystemIn = String.format("%s%n%s%n", "1", "q");
            System.setIn(new ByteArrayInputStream(dummySystemIn.getBytes()));
            customer.promptUserOptions();

            try {
                when(resultQueue.take()).thenReturn(new OperationsEvent<>() {
                    @Override
                    public ResultOperationType getType() {
                        return ResultOperationType.SUCCESS;
                    }

                    @Override
                    public String getPayload() {
                        return null;
                    }
                });


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    private Runnable getBuyRunnable() {
        return () -> {
            String dummySystemIn = String.format("%s%n%s%n%s%n", "2", "Soda", "q");
            System.setIn(new ByteArrayInputStream(dummySystemIn.getBytes()));
            customer.promptUserOptions();

            try {
                when(resultQueue.take()).thenReturn(new OperationsEvent<>() {
                    @Override
                    public ResultOperationType getType() {
                        return ResultOperationType.RETURN_PRODUCT;
                    }

                    @Override
                    public String getPayload() {
                        return sodaProduct.toString();
                    }
                });


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    @AfterEach
    void tearDown() {
        System.setIn(stdin);
    }
}
