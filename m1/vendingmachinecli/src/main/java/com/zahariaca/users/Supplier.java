package com.zahariaca.users;

import com.zahariaca.utils.UserInputUtils;
import com.zahariaca.vendingmachine.events.VendingMachineEvent;
import com.zahariaca.vendingmachine.events.VendingMachineOperations;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class Supplier implements User {

    @Override
    public String promptUserOptions(BlockingQueue<VendingMachineEvent<VendingMachineOperations, String>> commandQueue) {
        // offer customer specific option and handle appropriately
        System.out.println("++ handling customer");

        boolean continueCondition = true;
        Scanner scanner = new Scanner(System.in);

        while (continueCondition) {
            // TODO show only products related to this supplier, not all (m1 or m2)
            System.out.println(
                    String.format(
                            UserInputUtils.constructPromptMessage(
                                    "%nSelect an operation:%n",
                                    "   [1] See product list. %n",
                                    "   [2] Add product. %n",
                                    "   [3] Delete product. %n",
                                    "   [4] Change price of product. %n",
                                    "   [5] Change name of product. %n",
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

        if (Integer.valueOf(userInput) == 1) {
            //TODO: display products of Vending Machine
            System.out.println("Display products!");
        } else if (Integer.valueOf(userInput) == 2) {
            //TODO: handle add process
            System.out.println("Add product");
        } else if (Integer.valueOf(userInput) == 3) {
            //TODO: handle delete process
            System.out.println("Delete product");
        } else if (Integer.valueOf(userInput) == 4) {
            //TODO: handle change price process
            System.out.println("Change product price");
        } else if (Integer.valueOf(userInput) == 5) {
            //TODO: handle change name process
            System.out.println("Change product name");
        }

        return true;
    }
}
