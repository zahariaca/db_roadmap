package com.zahariaca.users;

import com.zahariaca.pojo.Product;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 14.11.2018
 */
class SupplierTest {
    private InputStream stdin;
    private User customer;
    private Product sodaProduct;
    private BlockingQueue<OperationsEvent<OperationType, String>> commandQueue;
    private BlockingQueue<OperationsEvent<ResultOperationType, Product>> resultQueue;

    @BeforeEach
    void init() {
        Product.setIdGenerator(new AtomicInteger(1000));
        stdin = System.in;
        customer = new Supplier();
        UUID productOneUUID = UUID.fromString("8f70c754-4257-43d1-91e8-9438838d23cd");
        UUID supplierOneUUID = UUID.fromString("a3af93f2-0fff-42e0-b84c-6e507ece0264");
        sodaProduct = new Product("Soda", "Sugary refreshing beverage", 5.6f, supplierOneUUID);
        commandQueue = new LinkedBlockingQueue<>(1);
        BlockingQueue<OperationsEvent<ResultOperationType, Product>> realResultQueue = new LinkedBlockingQueue(1);
        resultQueue = spy(realResultQueue);
        customer.setCommandQueue(commandQueue);
        customer.setResultQueue(resultQueue);
    }

    @Test
    void testEmptyQueue() {
        customer = new Customer();
        assertThrows(RuntimeException.class, () -> customer.promptUserOptions(), "Empty Queues results in RuntimeException");
    }

    @Test
    void testAddOption() throws InterruptedException {
        Thread t = new Thread(getAddRunnable());
        t.start();
        // TODO: FIXME when time permits...
        Thread.sleep(1000);
        OperationsEvent<OperationType, String> commandEvent = commandQueue.take();
        assertEquals(commandEvent.getType(), OperationType.ADD);
        assertEquals(commandEvent.getPayload(), "{\"name\":\"NewProduct\",\"description\":\"New product description\",\"price\":5.67,\"uniqueId\":1002,\"supplierId\":\"a3af93f2-0fff-42e0-b84c-6e507ece0264\"}");
        verify(resultQueue, times(1)).take();

    }

    @Test
    void testDeleteOption() throws InterruptedException {
        Thread t = new Thread(getDeleteRunnable());
        t.start();
        // TODO: FIXME when time permits...
        Thread.sleep(1000);
        OperationsEvent<OperationType, String> commandEvent = commandQueue.take();
        assertEquals(commandEvent.getType(), OperationType.DELETE);
        assertEquals(commandEvent.getPayload(), "1002");
        verify(resultQueue, times(1)).take();
        fail();
    }

    @Test
    void testChangeOption() throws InterruptedException {
        Thread t = new Thread(getChangeRunnable());
        t.start();
        // TODO: FIXME when time permits...
        Thread.sleep(1000);
        OperationsEvent<OperationType, String> commandEvent = commandQueue.take();
        assertEquals(commandEvent.getType(), OperationType.CHANGE_PRODUCT);
        assertEquals(commandEvent.getPayload(), "{\"name\":\"NewProduct\",\"description\":\"New product description\",\"price\":5.67,\"uniqueId\":1002,\"supplierId\":\"a3af93f2-0fff-42e0-b84c-6e507ece0264\"}");
        verify(resultQueue, times(1)).take();
        fail();

    }



    private Runnable getAddRunnable() {
        return () -> {
            String dummySystemIn = String.format("%s%n%s%n%s%n%s%n%s%n",
                    "2",
                    "NewProduct",
                    "New product description",
                    "5.67",
                    "q");
            System.setIn(new ByteArrayInputStream(dummySystemIn.getBytes()));
            customer.promptUserOptions();

            whenSuccess();
        };
    }

    private Runnable getDeleteRunnable() {
        return () -> {
            String dummySystemIn = String.format("%s%n%s%n%s%n",
                    "3",
                    "1002", //uniqueId of product to delete
                    "q");
            System.setIn(new ByteArrayInputStream(dummySystemIn.getBytes()));
            customer.promptUserOptions();

            whenSuccess();
        };
    }

    private Runnable getChangeRunnable() {
        return () -> {
            String dummySystemIn = String.format("%s%n%s%n%s%n%s%n%s%n%s%n",
                    "4",
                    "1002", //uniqueId of product to change
                    "NewProduct",
                    "New product description",
                    "5.67",
                    "q");
            System.setIn(new ByteArrayInputStream(dummySystemIn.getBytes()));
            customer.promptUserOptions();

            whenSuccess();
        };
    }

    private void whenSuccess() {
        try {
            when(resultQueue.take()).thenReturn(new OperationsEvent<>() {
                @Override
                public ResultOperationType getType() {
                    return ResultOperationType.SUCCESS;
                }

                @Override
                public Product getPayload() {
                    return null;
                }
            });


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @AfterEach
    void tearDown() {
        System.setIn(stdin);
    }
}
