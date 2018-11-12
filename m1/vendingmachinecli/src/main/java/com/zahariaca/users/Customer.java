package com.zahariaca.users;

import com.zahariaca.pojo.Product;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import com.zahariaca.utils.UserInputUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class Customer implements User<BlockingQueue<OperationsEvent<OperationType, String>>, BlockingQueue<OperationsEvent<ResultOperationType, Product>>> {

    private Logger logger = LogManager.getLogger(Customer.class);
    private BlockingQueue<OperationsEvent<OperationType, String>> commandQueue = null;
    private BlockingQueue<OperationsEvent<ResultOperationType, Product>> resultQueue = null;

    @Override
    public void setCommandQueue(BlockingQueue<OperationsEvent<OperationType, String>> commandQueue) {
        this.commandQueue = commandQueue;
    }

    @Override
    public void setResultQueue(BlockingQueue<OperationsEvent<ResultOperationType, Product>> resultQueue) {
        this.resultQueue = resultQueue;
    }

    @Override
    public void promptUserOptions() {
        // offer customer specific option and handle appropriately
        logger.log(Level.DEBUG, ">O: Handling customer");

        checkQueues();

        boolean continueCondition = true;
        Scanner scanner = new Scanner(System.in);

        while (continueCondition) {
            System.out.println(
                    String.format(
                            UserInputUtils.INSTANCE.constructPromptMessage(
                                    "%nSelect an operation:%n",
                                    "   [1] See product list. %n",
                                    "   [2] Buy product. %n",
                                    "   [q/quit] to end process. %n")));

            String userInput = scanner.next();
            if (UserInputUtils.INSTANCE.checkQuitCondition(userInput)) {
                continueCondition = false;
                continue;
            }

            if (!UserInputUtils.INSTANCE.checkIsNumericCharacter(userInput)) {
                System.out.println("Incorrect input. Try again.");
                continue;
            }

            continueCondition = handleUserInput(userInput);
            logger.log(Level.INFO, "Handling user input.");
        }
    }

    private void checkQueues() {
        if (commandQueue == null || resultQueue == null) {
            logger.log(Level.ERROR, "COMMAND QUEUE / RESULT QUEUE IS NULL. Something very bad has happened...cannot operate");
            System.exit(-1);
        }
    }


    @Override
    public boolean handleUserInput(String userInput) {
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
        addEventToResultQueue("", OperationType.DISPLAY);
        System.out.println(Thread.currentThread() + " ++ Display products");
        logger.log(Level.DEBUG, ">E: firing: {}", OperationType.DISPLAY);
    }

    private void handleBuyOption() throws InterruptedException {
        String userOrder = promptForOrder();
        addEventToResultQueue(userOrder, OperationType.BUY);
        System.out.println(Thread.currentThread() + " ++ Buy product");
        logger.log(Level.DEBUG, ">E: firing: {}", OperationType.BUY);

        handleResponse();
    }

    private String promptForOrder() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What would you like to order (name of product): ");
        return scanner.next();
    }

    private void handleResponse() throws InterruptedException {
        OperationsEvent<ResultOperationType, Product> returnedResult = resultQueue.take();

        if (returnedResult.getType().equals(ResultOperationType.RETURN_PRODUCT)) {
            System.out.println(String.format("CLIENT RECEIVED: >>> %s", returnedResult.getPayload()));
        } else if (returnedResult.getType().equals(ResultOperationType.PRODUCT_NOT_FOUND)) {
            System.out.println("The product you chose does not exist. Please try again.");
        }
    }

    private void addEventToResultQueue(String userOrder, OperationType commandOperation) throws InterruptedException {
        commandQueue.put(new OperationsEvent<OperationType, String>() {
            @Override
            public OperationType getType() {
                return commandOperation;
            }

            @Override
            public String getPayload() {
                return userOrder;
            }
        });
    }
}
