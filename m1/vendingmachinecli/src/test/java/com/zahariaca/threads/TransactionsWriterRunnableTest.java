package com.zahariaca.threads;

import com.zahariaca.pojo.Product;
import com.zahariaca.pojo.users.User;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.TransactionWriterOperationType;
import com.zahariaca.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 20.11.2018
 */
//TODO: fix sleep issue
class TransactionsWriterRunnableTest {
    private BlockingQueue<OperationsEvent<TransactionWriterOperationType, Product>> transactionsQueue;
    private TransactionsWriterRunnable transactionsWriterRunnable;

    @BeforeEach
    void init() {
        File transactionsFile = FileUtils.INSTANCE.getFile("src\\test\\resources\\test-transactions.json");
        transactionsQueue = spy(new LinkedBlockingQueue<>(10));
        transactionsWriterRunnable = new TransactionsWriterRunnable(transactionsQueue, transactionsFile);
    }

    @Test
    void testQuit() throws InterruptedException {
        Thread t = new Thread(transactionsWriterRunnable);
        t.start();
        transactionsQueue.put(new OperationsEvent<>() {
            @Override
            public TransactionWriterOperationType getType() {
                return TransactionWriterOperationType.QUIT;
            }

            @Override
            public Product getPayload() {
                return null;
            }
        });

        verify(transactionsQueue, timeout(1)).take();

        Thread.sleep(1000);
        assertFalse(t.isAlive());
    }
}
