package com.zahariaca.cli;

import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 19.11.2018
 */
//TODO: fix sleep issue
class PrimaryCliTest {
    private InputStream stdin;
    private BlockingQueue<OperationsEvent<OperationType, String[]>> commandQueue;
    private BlockingQueue<OperationsEvent<ResultOperationType, String>> resultQueue;
    private Cli<BlockingQueue<OperationsEvent<OperationType, String[]>>,
            BlockingQueue<OperationsEvent<ResultOperationType, String>>> primaryCli;

    @BeforeEach
    void init() {
        stdin = System.in;
        commandQueue = new LinkedBlockingQueue<>(1);
        BlockingQueue<OperationsEvent<ResultOperationType, String>> realResultQueue = new LinkedBlockingQueue<>(1);
        resultQueue = spy(realResultQueue);
        Cli<BlockingQueue<OperationsEvent<OperationType, String[]>>,
                BlockingQueue<OperationsEvent<ResultOperationType, String>>> realPrimaryCli = new PrimaryCli(commandQueue, resultQueue);
        primaryCli = spy(realPrimaryCli);
    }

    @Test
    void testQuit() throws InterruptedException {
        String dummySystemIn = String.format("%s%n",
                "q");
        System.setIn(new ByteArrayInputStream(dummySystemIn.getBytes()));
        primaryCli.promptUserOptions();
        OperationsEvent<OperationType, String[]> commandEvent = commandQueue.take();
        assertEquals(OperationType.QUIT, commandEvent.getType());
        assertEquals("", commandEvent.getPayload()[0]);
    }

    @Test
    void testSuccessfulLoginAsCustomer() throws InterruptedException {
        Thread t = new Thread(getRunnable(String.format("%s%n%s%n%s%n",
                "1",
                "q",
                "q")));
        t.start();
    }

    @Test
    void testSuccessfulLoginAsSupplier() throws InterruptedException {
        Thread t = new Thread(getRunnable(String.format("%s%n%s%n%s%n%s%n%s%n",
                "2",
                "admin",
                "admin",
                "q",
                "q")));
        t.start();
        OperationsEvent<OperationType, String[]> commandEvent = commandQueue.take();
        Assertions.assertEquals(commandEvent.getType(), OperationType.USER_LOGIN);
        Assertions.assertEquals(commandEvent.getPayload()[0], "admin");
        Assertions.assertEquals(commandEvent.getPayload()[1], DigestUtils.sha256Hex("admin"));
        Thread.sleep(1000);
        verify(resultQueue, times(1)).take();
    }

    @Test
    void testUnSuccessfulLoginAsSupplier() throws InterruptedException {
        Thread t = new Thread(getRunnable(String.format("%s%n%s%n%s%n%s%n%s%n",
                "2",
                "admin",
                "password",
                "q",
                "q")));
        t.start();
        OperationsEvent<OperationType, String[]> commandEvent = commandQueue.take();
        Assertions.assertEquals(commandEvent.getType(), OperationType.USER_LOGIN);
        Assertions.assertEquals(commandEvent.getPayload()[0], "admin");
        Assertions.assertEquals(commandEvent.getPayload()[1], DigestUtils.sha256Hex("password"));
        Thread.sleep(1000);
        verify(resultQueue, times(1)).take();
    }


    private Runnable getRunnable(String dummySystemIn) {
        return () -> {
            System.setIn(new ByteArrayInputStream(dummySystemIn.getBytes()));
            primaryCli.promptUserOptions();

            expectedResult(ResultOperationType.SUCCESS);
        };
    }

    private void expectedResult(ResultOperationType resultOperationType) {
        try {
            when(resultQueue.take()).thenReturn(new OperationsEvent<>() {
                @Override
                public ResultOperationType getType() {
                    return resultOperationType;
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
