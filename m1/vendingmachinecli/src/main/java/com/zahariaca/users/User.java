package com.zahariaca.users;

import com.zahariaca.vendingmachine.events.VendingMachineEvent;
import com.zahariaca.vendingmachine.events.VendingMachineOperations;

import java.util.concurrent.BlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public interface User {
    String promptUserOptions(BlockingQueue<VendingMachineEvent<VendingMachineOperations, String>> commandQueue);

    boolean handleUserInput(String input, BlockingQueue<VendingMachineEvent<VendingMachineOperations, String>> commandQueue) throws InterruptedException;
}
