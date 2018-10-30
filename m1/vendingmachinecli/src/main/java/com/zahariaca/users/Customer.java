package com.zahariaca.users;

import com.zahariaca.utils.UserInputUtils;
import com.zahariaca.vendingmachine.events.VendingMachineEvent;
import com.zahariaca.vendingmachine.events.VendingMachineOperations;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class Customer implements User {
    @Override
    public String promptUserOptions(BlockingQueue<VendingMachineEvent<VendingMachineOperations, String>> commandQueue) {
        // offer customer specific option and handle appropriately
        System.out.println("++ handling customer");

        boolean continueCondition = true;
        Scanner scanner = new Scanner(System.in);

        while (continueCondition) {
            System.out.println(
                    String.format(
                            UserInputUtils.constructPromptMessage(
                                    "%nSelect an operation:%n",
                                    "   [1] See product list. %n",
                                    "   [2] Buy product. %n",
                                    "   [q/quit] to end process. %n")));
            continueCondition = handleUserInput(scanner.next(), commandQueue);
        }

        return "";
    }


    @Override
    public boolean handleUserInput(String userInput, BlockingQueue<VendingMachineEvent<VendingMachineOperations, String>> commandQueue) {
        if (UserInputUtils.checkQuitCondition(userInput)) {
            return false;
        }

        try {
            if (Integer.valueOf(userInput) == 1) {
                commandQueue.put(new VendingMachineEvent<>() {
                    @Override
                    public VendingMachineOperations getType() {
                        return VendingMachineOperations.DISPLAY;
                    }

                    @Override
                    public String getPayload() {
                        return "DISPLAY!";
                    }
                });
                System.out.println(Thread.currentThread() + " ++ Display products");
            } else if (Integer.valueOf(userInput) == 2) {
                //TODO: handle buy process
                commandQueue.put(new VendingMachineEvent<>() {
                    @Override
                    public VendingMachineOperations getType() {
                        return VendingMachineOperations.BUY;
                    }

                    @Override
                    public String getPayload() {
                        return "BUY!";
                    }
                });
                System.out.println(Thread.currentThread() + " ++ Buy product");
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        return true;
    }


}
