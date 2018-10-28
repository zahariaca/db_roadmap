package com.zahariaca;

import com.zahariaca.exceptions.UnknownUserException;
import com.zahariaca.users.LoginHandler;
import com.zahariaca.users.TypeOfUser;
import com.zahariaca.users.UserFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class Main {
    private static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        System.out.println(String.format("%s%n%s%n%s",
                "+++++++++++++++++++++++++++++",
                "+    VENDING MACHINE CLI    +",
                "+++++++++++++++++++++++++++++"));

        promptForUserInput();

        System.out.println("Goodbye!");
    }

    private static void promptForUserInput() {
        boolean continueCondition = true;
        Scanner scanner = new Scanner(System.in);

        while (continueCondition) {
            System.out.println(String.format("%nSelect an operation:%n" +
                    "   [1] Login as Customer. %n" +
                    "   [2] Login as Supplier. %n" +
                    "   [q/quit] to end process. %n"));
            String userInput = scanner.next();

            continueCondition = handleUserInput(userInput);
        }
    }

    private static boolean handleUserInput(String userInput) {
        if ("quit".equalsIgnoreCase(userInput) || "q".equalsIgnoreCase(userInput)) {
            return false;
        }

        try {
            if (Integer.valueOf(userInput) == 1) {
                // no login required for customers, just handle input from them
                UserFactory.getUser(TypeOfUser.CUSTOMER).handleUserInput();
            }

            if (Integer.valueOf(userInput) == 2 && LoginHandler.INSTANCE.checkUserCredentials(TypeOfUser.SUPPLIER)) {
                // check username and password for supplier, then handle input from them
                UserFactory.getUser(TypeOfUser.SUPPLIER).handleUserInput();
            } else {
                logger.log(Level.ERROR, "Incorrect credentials, try again!");
            }

        } catch (UnknownUserException uex) {
            logger.log(Level.ERROR, uex.getMessage());
        }

        return true;
    }
}
