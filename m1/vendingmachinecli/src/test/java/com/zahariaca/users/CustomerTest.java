package com.zahariaca.users;

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
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 14.11.2018
 */
public class CustomerTest {
    private InputStream stdin;
    private User customer;
    private Product sodaProduct;
    private BlockingQueue<OperationsEvent<OperationType, String>> commandQueue;
    private BlockingQueue<OperationsEvent<ResultOperationType, Product>> resultQueue;

    @BeforeEach
    public void init() {
        stdin = System.in;
        customer = new Customer();
        UUID supplierOneUUID = UUID.fromString("a3af93f2-0fff-42e0-b84c-6e507ece0264");
        sodaProduct = new Product("Soda", "Sugary refreshing beverage", 5.6f, supplierOneUUID);
        commandQueue = new LinkedBlockingQueue<>(1);
        BlockingQueue<OperationsEvent<ResultOperationType, Product>> realResultQueue = new LinkedBlockingQueue(1);
        resultQueue = spy(realResultQueue);
        customer.setCommandQueue(commandQueue);
        customer.setResultQueue(resultQueue);
    }

    @Test
    public void testEmptyQueue() {
        customer = new Customer();
        assertThrows(RuntimeException.class, () -> customer.promptUserOptions(), "Empty Queues results in RuntimeException");
    }

    @Test
    public void testDisplayOption() throws InterruptedException {
        String dummySystemIn = String.format("%s%n%s%n", "1", "q");
        System.setIn(new ByteArrayInputStream(dummySystemIn.getBytes()));
        customer.promptUserOptions();
        OperationsEvent<OperationType, String> commandEvent = commandQueue.take();
        assertEquals(commandEvent.getType(), OperationType.DISPLAY);
        assertEquals(commandEvent.getPayload(), "");
    }

    @Test
    public void testBuyOption() throws InterruptedException {
        Thread t = new Thread(getBuyRunnable());
        t.start();
        // TODO: FIXME when time permits...
        Thread.sleep(1000);
        OperationsEvent<OperationType, String> commandEvent = commandQueue.take();
        assertEquals(commandEvent.getType(), OperationType.BUY);
        assertEquals(commandEvent.getPayload(), "Soda");
        verify(resultQueue, times(1)).take();

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
                    public Product getPayload() {
                        return sodaProduct;
                    }
                });


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }


    @AfterEach
    public void tearDown() {
        System.setIn(stdin);
    }
}
