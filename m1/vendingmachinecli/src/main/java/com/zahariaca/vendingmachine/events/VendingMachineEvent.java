package com.zahariaca.vendingmachine.events;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 30.10.2018
 */
public interface VendingMachineEvent<T,K> {
    T getType();
    K getPayload();
}
