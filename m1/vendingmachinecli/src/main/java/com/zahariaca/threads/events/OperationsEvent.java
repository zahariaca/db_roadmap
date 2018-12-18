package com.zahariaca.threads.events;

/**
 * @param <T> type of event that is fired
 * @param <K> type of payload that accompanies event
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 30.10.2018
 */
public interface OperationsEvent<T, K> {
    T getType();

    K getPayload();
}
