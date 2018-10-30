package com.zahariaca.threads;

import com.zahariaca.vendingmachine.VendingMachine;
import com.zahariaca.vendingmachine.events.VendingMachineEvent;
import com.zahariaca.vendingmachine.events.VendingMachineOperations;

import java.util.concurrent.BlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 30.10.2018
 */
public class VendingMachineRunnable implements Runnable {
    private final VendingMachine vendingMachine;
    private final BlockingQueue<VendingMachineEvent<VendingMachineOperations, String>> commandQueue;
    private boolean exitCondition = true;

    public VendingMachineRunnable(BlockingQueue<VendingMachineEvent<VendingMachineOperations, String>> commandQueue, VendingMachine vendingMachine) {
        this.commandQueue = commandQueue;
        this.vendingMachine = vendingMachine;
    }

    @Override
    public void run() {
        try {
            while (exitCondition) {
                VendingMachineEvent<VendingMachineOperations, String> test = commandQueue.take();
                if (test.getType().equals(VendingMachineOperations.DISPLAY)) {
                    System.out.println(Thread.currentThread() + " >> DISPLAY >> " + test.getPayload());
                } else if (test.getType().equals(VendingMachineOperations.BUY)) {
                    System.out.println(Thread.currentThread() + " >> BUY >> " + test.getPayload());
                } else if (test.getType().equals(VendingMachineOperations.DELETE)) {
                    System.out.println(Thread.currentThread() + " >> DELETE >> " + test.getPayload());
                } else if (test.getType().equals(VendingMachineOperations.QUIT)) {
                    System.out.println(Thread.currentThread() + " >> QUIT >> " + test.getPayload());
                    System.out.println("Goodbye!");
                    exitCondition = false;
                }

                System.out.println("Outside if: " + test.getType() + test.getPayload());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
