package com.zahariaca;

import com.zahariaca.exceptions.UnknownUserException;
import com.zahariaca.loader.FileLoader;
import com.zahariaca.pojo.Product;
import com.zahariaca.users.LoginHandler;
import com.zahariaca.users.TypeOfUser;
import com.zahariaca.users.UserFactory;
import com.zahariaca.utils.UserInputUtils;
import com.zahariaca.vendingmachine.VendingMachine;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.Scanner;
import java.util.Set;

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

        System.out.println("Starting up...");

        ClassLoader classLoader = Main.class.getClassLoader();
        URL fileUrl = classLoader.getResource("test-products.json");
        File file = null;
        if (fileUrl != null) {
            file = new File(fileUrl.getFile());
        }

        VendingMachine vendingMachine = new VendingMachine(FileLoader.INSTANCE.loadFromFile(file));

        promptForUserIdentification();

        System.out.println("Goodbye!");
    }

    private static void promptForUserIdentification() {
        boolean continueCondition = true;
        Scanner scanner = new Scanner(System.in);

        while (continueCondition) {
            System.out.println(
                    String.format(
                            UserInputUtils.constructPromptMessage(
                                    "%nSelect an operation:%n",
                                    "   [1] Login as Customer. %n",
                                    "   [2] Login as Supplier. %n",
                                    "   [q/quit] to end process. %n")));

            continueCondition = handleUserInput(scanner.next());
        }
    }

    private static boolean handleUserInput(String userInput) {
        if (UserInputUtils.checkQuitCondition(userInput)) {
            return false;
        }

        try {
            if (Integer.valueOf(userInput) == 1) {
                // no login required for customers, just handle input from them
                UserFactory.getUser(TypeOfUser.CUSTOMER).promptUserOptions();
            }

            if (Integer.valueOf(userInput) == 2 && LoginHandler.INSTANCE.checkUserCredentials(TypeOfUser.SUPPLIER)) {
                // check username and password for supplier, then handle input from them
                UserFactory.getUser(TypeOfUser.SUPPLIER).promptUserOptions();
            } else {
                logger.log(Level.ERROR, "Incorrect credentials, try again!");
            }

        } catch (UnknownUserException uex) {
            logger.log(Level.ERROR, uex.getMessage());
        }

        return true;
    }
}
