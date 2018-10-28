package com.zahariaca.users;

import com.zahariaca.utils.UserInputUtils;

import java.util.Scanner;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class Customer implements User {

    @Override
    public void promptUserOptions() {
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

            continueCondition = handleUserInput(scanner.next());
        }
    }


    @Override
    public boolean handleUserInput(String userInput) {
        if (UserInputUtils.checkQuitCondition(userInput)) {
            return false;
        }

        if (Integer.valueOf(userInput) == 1) {
            //TODO: display products of Vending Machine
            System.out.println("Display products!");
        } else if (Integer.valueOf(userInput) == 2) {
            //TODO: handle buy process
            System.out.println("Buy product");
        }

        return true;
    }

}
