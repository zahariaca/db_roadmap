package com.zahariaca.users;

import com.zahariaca.utils.UserInputUtils;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class Customer implements User<BlockingQueue<OperationsEvent<OperationType, String>>> {

    private Logger logger = LogManager.getLogger(Customer.class);
    private BlockingQueue<OperationsEvent<OperationType, String>> commandQueue = null;

    @Override
    public void setCommandQueue(BlockingQueue<OperationsEvent<OperationType, String>> commandQueue) {
        this.commandQueue = commandQueue;
    }

    @Override
    public String promptUserOptions() {
        // offer customer specific option and handle appropriately
        System.out.println("++ handling customer");
        logger.log(Level.DEBUG, "Handling customer");

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
            if (commandQueue != null) {
                continueCondition = handleUserInput(scanner.next());
                logger.log(Level.INFO, "Handling user input.");
            } else {
                continueCondition = false;
                logger.log(Level.ERROR, "COMMAND QUEUE IS NULL. Something very bad has happened...cannot operate");
                System.exit(-1);
            }
        }

        return "";
    }


    @Override
    public boolean handleUserInput(String userInput) {
        if (UserInputUtils.checkQuitCondition(userInput)) {
            return false;
        }

        try {
            if (Integer.valueOf(userInput) == 1) {
                commandQueue.put(new OperationsEvent<OperationType, String>() {
                    @Override
                    public OperationType getType() {
                        return OperationType.DISPLAY;
                    }

                    @Override
                    public String getPayload() {
                        return "DISPLAY!";
                    }
                });
                System.out.println(Thread.currentThread() + " ++ Display products");
                logger.log(Level.DEBUG, ">E: firing: {}", OperationType.DISPLAY);
            } else if (Integer.valueOf(userInput) == 2) {
                String userOrder = promptForOrder();
                //TODO: handle buy process
                commandQueue.put(new OperationsEvent<OperationType, String>() {
                    @Override
                    public OperationType getType() {
                        return OperationType.BUY;
                    }

                    @Override
                    public String getPayload() {
                        return userOrder;
                    }
                });
                System.out.println(Thread.currentThread() + " ++ Buy product");
                logger.log(Level.DEBUG, ">E: firing: {}", OperationType.BUY);
            }

            // make this thread wait to get the result from vending machine thread:
//            OperationsEvent<OperationType, String> result = commandQueue.take();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            logger.log(Level.ERROR, ie.getMessage());
            Thread.currentThread().interrupt();
        }

        return true;
    }

    private String promptForOrder() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What would you like to order (name of product): ");
        return scanner.next();
    }


}
