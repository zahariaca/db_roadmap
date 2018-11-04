package com.zahariaca.users;

import com.zahariaca.pojo.Product;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;

import java.util.concurrent.BlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public interface User<T, K> {
    String promptUserOptions();
    boolean handleUserInput(String input) throws InterruptedException;
    void setCommandQueue(T commandQueue);
    void setResultQueue(K resultQueue);
}
