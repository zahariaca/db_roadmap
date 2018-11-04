package com.zahariaca.threads;

import com.zahariaca.exceptions.UnknownUserTypeException;
import com.zahariaca.pojo.Product;
import com.zahariaca.threads.events.ResultOperationType;
import com.zahariaca.users.LoginHandler;
import com.zahariaca.users.TypeOfUser;
import com.zahariaca.users.User;
import com.zahariaca.users.UserFactory;
import com.zahariaca.utils.UserInputUtils;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 30.10.2018
 */
public class CLIRunnable implements Runnable {
    private final Logger logger = LogManager.getLogger(CLIRunnable.class);
    private final BlockingQueue<OperationsEvent<OperationType, String>> commandQueue;
    private final BlockingQueue<OperationsEvent<ResultOperationType, Product>> resultQueue;

    public CLIRunnable(BlockingQueue<OperationsEvent<OperationType, String>> commandQueue, BlockingQueue<OperationsEvent<ResultOperationType, Product>> resultQueue) {
        this.commandQueue = commandQueue;
        this.resultQueue = resultQueue;
        logger.log(Level.INFO, ">O: instantiated");
    }


    @Override
    public void run() {
        promptForUserIdentification();
    }

    private void promptForUserIdentification() {
        boolean continueCondition = true;
        Scanner scanner = new Scanner(System.in);

        try {
            while (continueCondition) {
                logger.log(Level.DEBUG, ">O: infinite loop enter.");
                System.out.println(
                        String.format(
                                UserInputUtils.INSTANCE.constructPromptMessage(
                                        "%nSelect an operation:%n",
                                        "   [1] Login as Customer. %n",
                                        "   [2] Login as Supplier. %n",
                                        "   [q/quit] to end process. %n")));

                String userInput = scanner.next();

                if(!UserInputUtils.INSTANCE.checkIsNumericCharacter(userInput)) {
                    System.out.println("Incorrect input. Try again.");
                    continue;
                }
                continueCondition = handleUserInput(userInput);
            }
            logger.log(Level.DEBUG, ">O: infinite loop exit.");
        } catch (InterruptedException e) {
            logger.log(Level.ERROR, ">O: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private boolean handleUserInput(String userInput) throws InterruptedException {
        if (UserInputUtils.INSTANCE.checkQuitCondition(userInput)) {
            commandQueue.put(new OperationsEvent<OperationType, String>() {
                @Override
                public OperationType getType() {
                    return OperationType.QUIT;
                }

                @Override
                public String getPayload() {
                    return "";
                }
            });
            logger.log(Level.INFO, ">O: quit command caught. Initiating application shutdown");
            return false;
        }

        try {
            if (Integer.valueOf(userInput) == 1) {
                // no login required for customers, just handle input from them
                User user = UserFactory.getUser(TypeOfUser.CUSTOMER);
                user.setCommandQueue(commandQueue);
                user.setResultQueue(resultQueue);
                user.promptUserOptions();
                return true;
            }

            if (Integer.valueOf(userInput) == 2 && LoginHandler.INSTANCE.checkUserCredentials(TypeOfUser.SUPPLIER)) {
                // check username and password for supplier, then handle input from them
                User user = UserFactory.getUser(TypeOfUser.SUPPLIER);
                user.setCommandQueue(commandQueue);
                user.setResultQueue(resultQueue);
                user.promptUserOptions();
            } else {
                logger.log(Level.ERROR, ">O: Incorrect credentials, try again!");
            }

        } catch (UnknownUserTypeException e) {
            logger.log(Level.ERROR, ">O: {}", e.getMessage());
        }

        return true;
    }
}
