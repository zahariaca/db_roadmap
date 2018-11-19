package com.zahariaca.users;

import com.zahariaca.pojo.Product;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
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
    private String supplierOneUUID;

    @BeforeEach
    void init() {
        Product.setIdGenerator(new AtomicInteger(1000));
        stdin = System.in;
        supplier = new Supplier("admin", "admin", true);
        supplierOneUUID = ((Supplier) supplier).getUserId();
        sodaProduct = new Product("Soda", "Sugary refreshing beverage", 5.6f, supplierOneUUID);
        commandQueue = new LinkedBlockingQueue<>(1);
        BlockingQueue<OperationsEvent<ResultOperationType, String>> realResultQueue = new LinkedBlockingQueue<>(1);
        resultQueue = spy(realResultQueue);
        supplier.setCommandQueue(commandQueue);
        supplier.setResultQueue(resultQueue);


    }

    @Test
    void testCompareToReturnsNonZeroCode() {
        assertFalse(0 ==((Supplier) supplier).compareTo(new Supplier("test", "test", false)));
    }

    @Test
    void testCompareToReturnsZeroCode() {
        assertTrue(0 ==((Supplier) supplier).compareTo(new Supplier("admin", "admin", true)));
    }

    @Test
    void testIdIsHashOfUsername() {
        assertEquals(((Supplier) supplier).getUserId(),(DigestUtils.sha256Hex(((Supplier) supplier).getUsername())));
    }

    @Test
    void testEmptyQueue() {
        supplier = new Supplier("admin", "admin", true);
        assertThrows(RuntimeException.class, () -> supplier.promptUserOptions(), "Empty Queues results in RuntimeException");
    }

    @Test
    void testAddOption() throws InterruptedException {
        Thread t = new Thread(getAddRunnable());
        t.start();
        // TODO: shouldn't use sleep... FIXME when time permits...
        OperationsEvent<OperationType, String[]> commandEvent = commandQueue.take();
        assertEquals(commandEvent.getType(), OperationType.ADD);
        assertEquals(commandEvent.getPayload()[0], "NewProduct");
        assertEquals(commandEvent.getPayload()[1], "New product description");
        assertEquals(commandEvent.getPayload()[2], "5.67");
        assertEquals(commandEvent.getPayload()[3], supplierOneUUID);
        Thread.sleep(1000);
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
