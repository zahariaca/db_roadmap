package com.zahariaca.users;

import com.zahariaca.pojo.Product;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 14.11.2018
 */
class SupplierTest {
    private InputStream stdin;
    private User<BlockingQueue<OperationsEvent<OperationType, String[]>>, BlockingQueue<OperationsEvent<ResultOperationType, String>>> supplier;
    private Product sodaProduct;
    private BlockingQueue<OperationsEvent<OperationType, String[]>> commandQueue;
    private BlockingQueue<OperationsEvent<ResultOperationType, String>> resultQueue;

    @BeforeEach
    void init() {
        Product.setIdGenerator(new AtomicInteger(1000));
        stdin = System.in;
        supplier = new Supplier();
        String supplierOneUUID = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918";
        sodaProduct = new Product("Soda", "Sugary refreshing beverage", 5.6f, supplierOneUUID);
        commandQueue = new LinkedBlockingQueue<>(1);
        BlockingQueue<OperationsEvent<ResultOperationType, String>> realResultQueue = new LinkedBlockingQueue<>(1);
        resultQueue = spy(realResultQueue);
//        BlockingQueue mock = mock(LinkedBlockingQueue.class);
        supplier.setCommandQueue(commandQueue);
        supplier.setResultQueue(realResultQueue);


    }

    @Test
    void testEmptyQueue() {
        supplier = new Supplier();
        assertThrows(RuntimeException.class, () -> supplier.promptUserOptions(), "Empty Queues results in RuntimeException");
    }

    @Test
    void testAddOption() throws InterruptedException {
        Thread t = new Thread(getAddRunnable());
        t.start();
        // TODO: shouldn't use sleep... FIXME when time permits...
        Thread.sleep(1000);
        OperationsEvent<OperationType, String[]> commandEvent = commandQueue.take();
        assertEquals(commandEvent.getType(), OperationType.ADD);
        assertEquals(commandEvent.getPayload(), "{\"name\":\"NewProduct\",\"description\":\"New product description\",\"price\":5.67,\"uniqueId\":1002,\"supplierId\":\"a3af93f2-0fff-42e0-b84c-6e507ece0264\"}");
        verify(resultQueue, times(1)).take();

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
            supplier.promptUserOptions();

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
                public String getPayload() {
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
