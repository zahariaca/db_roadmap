package com.zahariaca.cli;

import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import org.junit.jupiter.api.AfterEach;
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
public class PrimaryCliTest {
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
    public void testLoginAsCustomer() throws InterruptedException {
        String dummySystemIn = String.format("%s%n",
                "q");
        System.setIn(new ByteArrayInputStream(dummySystemIn.getBytes()));
        primaryCli.promptUserOptions();
        OperationsEvent<OperationType, String[]> commandEvent = commandQueue.take();
        assertEquals(commandEvent.getType(), OperationType.QUIT);
        assertEquals(commandEvent.getPayload()[0], "");
    }


    @AfterEach
    void tearDown() {
        System.setIn(stdin);
    }
}
