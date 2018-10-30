package com.zahariaca;

import com.zahariaca.exceptions.UnknownUserException;
import com.zahariaca.loader.FileLoader;
import com.zahariaca.threads.CLIRunnable;
import com.zahariaca.threads.VendingMachineRunnable;
import com.zahariaca.users.LoginHandler;
import com.zahariaca.users.TypeOfUser;
import com.zahariaca.users.UserFactory;
import com.zahariaca.utils.UserInputUtils;
import com.zahariaca.vendingmachine.VendingMachine;
import com.zahariaca.vendingmachine.events.VendingMachineEvent;
import com.zahariaca.vendingmachine.events.VendingMachineOperations;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class Main {
    private static Logger logger = LogManager.getLogger(Main.class);
    private static BlockingQueue<VendingMachineEvent<VendingMachineOperations, String>> commandQueue = new LinkedBlockingQueue<>(10);

    public static void main(String[] args) {
        System.out.println(String.format("%s%n%s%n%s",
                "+++++++++++++++++++++++++++++",
                "+    VENDING MACHINE CLI    +",
                "+++++++++++++++++++++++++++++"));

        System.out.println("Starting up...");

        ClassLoader classLoader = Main.class.getClassLoader();
        URL fileUrl = classLoader.getResource("products.json");
        File file = null;
        if (fileUrl != null) {
            file = new File(fileUrl.getFile());
        }

        VendingMachine vendingMachine = new VendingMachine(FileLoader.INSTANCE.loadFromFile(file));

        new Thread(new VendingMachineRunnable(commandQueue, vendingMachine)).start();

        new Thread(new CLIRunnable(commandQueue)).start();
    }


}
