package com.zahariaca.cli;

import com.zahariaca.exceptions.UserInUnsafeStateException;
import com.zahariaca.pojo.users.User;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import com.zahariaca.utils.UserInputUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.constraints.NotNull;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class CustomerCli implements Cli<BlockingQueue<OperationsEvent<OperationType, String[]>>, BlockingQueue<OperationsEvent<ResultOperationType, String>>> {
    private Logger logger = LogManager.getLogger(CustomerCli.class);
    private BlockingQueue<OperationsEvent<OperationType, String[]>> commandQueue = null;
    private BlockingQueue<OperationsEvent<ResultOperationType, String>> resultQueue = null;
    private Scanner scanner;
    private User user;

    public CustomerCli(User customer) {
        user = customer;
    }

    @Override
    public void setCommandQueue(@NotNull BlockingQueue<OperationsEvent<OperationType, String[]>> commandQueue) {
        this.commandQueue = commandQueue;
    }

    @Override
    public void setResultQueue(@NotNull BlockingQueue<OperationsEvent<ResultOperationType, String>> resultQueue) {
        this.resultQueue = resultQueue;
    }

    @Override
    public void promptUserOptions() {
        // offer customer specific option and handle appropriately
        logger.log(Level.DEBUG, ">O: Handling customer");

        checkQueues();

        boolean continueCondition = true;

        scanner = new Scanner(System.in);

        while (continueCondition) {
            System.out.println(
                    String.format(
                            UserInputUtils.INSTANCE.constructPromptMessage(
                                    "%nSelect an operation:%n",
                                    "   [1] See product list. %n",
                                    "   [2] Buy product. %n",
                                    "   [q/quit] to end process. %n")));

            String userInput = scanner.nextLine();
            if (UserInputUtils.INSTANCE.checkQuitCondition(userInput)) {
                continueCondition = false;
                continue;
            }

            if (!UserInputUtils.INSTANCE.checkIsInteger(userInput)) {
                System.out.println("Incorrect input. Try again.");
                continue;
            }

            continueCondition = handleUserInput(userInput);
            logger.log(Level.INFO, "Handling user input.");
        }
    }

    private void checkQueues() {
        if (commandQueue == null || resultQueue == null) {
            String message = "COMMAND QUEUE / RESULT QUEUE IS NULL. Something very bad has happened...cannot operate";
            logger.log(Level.ERROR, message);
            throw new UserInUnsafeStateException(message);
        }
    }

    private boolean handleUserInput(String userInput) {
        try {
            if (Integer.valueOf(userInput) == 1) {
                sendDisplayEvent();
            } else if (Integer.valueOf(userInput) == 2) {
                handleBuyOption();
            }
        } catch (InterruptedException ie) {
            logger.log(Level.ERROR, ie.getMessage());
            Thread.currentThread().interrupt();
        }

        return true;
    }

    private void sendDisplayEvent() throws InterruptedException {
        addEventToCommandQueue(OperationType.DISPLAY, new String[]{""});
        logger.log(Level.DEBUG, ">E: firing: {}", OperationType.DISPLAY);
        resultQueue.take();
    }

    private void handleBuyOption() throws InterruptedException {
        String userOrder = promptForOrder();
        addEventToCommandQueue(OperationType.BUY, new String[]{userOrder});
        logger.log(Level.DEBUG, ">E: firing: {}", OperationType.BUY);

        handleResponse();
    }

    private String promptForOrder() {
        System.out.println("What would you like to order (name of product): ");
        return scanner.nextLine();
    }

    private void handleResponse() throws InterruptedException {
        OperationsEvent<ResultOperationType, String> returnedResult = resultQueue.take();

        if (returnedResult.getType().equals(ResultOperationType.RETURN_PRODUCT)) {
            System.out.println(String.format("Client has received: >>> %n%s%n", returnedResult.getPayload()));
        } else if (returnedResult.getType().equals(ResultOperationType.PRODUCT_NOT_FOUND)) {
            System.out.println("The product you chose does not exist. Please try again.");
        }
    }

    private void addEventToCommandQueue(OperationType commandOperation, String[] userOrder) throws InterruptedException {
        commandQueue.put(new OperationsEvent<>() {
            @Override
            public OperationType getType() {
                return commandOperation;
            }

            @Override
            public String[] getPayload() {
                return userOrder;
            }
        });
    }
}
