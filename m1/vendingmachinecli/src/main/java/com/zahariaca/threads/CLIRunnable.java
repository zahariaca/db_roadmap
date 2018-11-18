package com.zahariaca.threads;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import com.zahariaca.users.Customer;
import com.zahariaca.users.Supplier;
import com.zahariaca.users.User;
import com.zahariaca.utils.UserInputUtils;
import org.apache.commons.codec.digest.DigestUtils;
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
    private final BlockingQueue<OperationsEvent<OperationType, String[]>> commandQueue;
    private final BlockingQueue<OperationsEvent<ResultOperationType, String>> resultQueue;
    private User<BlockingQueue<OperationsEvent<OperationType, String[]>>, BlockingQueue<OperationsEvent<ResultOperationType, String>>> user;
    private Scanner scanner;
    private volatile boolean continueCondition = true;

    public CLIRunnable(BlockingQueue<OperationsEvent<OperationType, String[]>> commandQueue, BlockingQueue<OperationsEvent<ResultOperationType, String>> resultQueue) {
        this.commandQueue = commandQueue;
        this.resultQueue = resultQueue;
        logger.log(Level.INFO, ">O: instantiated");
    }


    @Override
    public void run() {
        promptForUserIdentification();
    }

    private void promptForUserIdentification() {
        scanner = new Scanner(System.in);

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

                String userInput = scanner.nextLine();

                if (UserInputUtils.INSTANCE.checkQuitCondition(userInput)) {
                    handleShutdown();
                    continue;
                }

                if (!UserInputUtils.INSTANCE.checkIsInteger(userInput)) {
                    System.out.println("Incorrect input. Try again.");
                    continue;
                }

                handleUserInput(userInput);
            }
            logger.log(Level.DEBUG, ">O: infinite loop exit.");
        } catch (InterruptedException e) {
            logger.log(Level.ERROR, ">O: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void handleShutdown() throws InterruptedException {
        addEventToCommandQueue(OperationType.QUIT,  new String[]{""});
        logger.log(Level.INFO, ">O: quit command caught. Initiating application shutdown");
        continueCondition = false;
    }

    private void handleUserInput(String userInput) throws InterruptedException {
        if (Integer.valueOf(userInput) == 1) {
            // no login required for customers, just handle input from them
            handleCustomer();
        } else if (Integer.valueOf(userInput) == 2) {
            handleSupplier();
        }
    }

    private void handleCustomer() {
        user = new Customer();
        user.setCommandQueue(commandQueue);
        user.setResultQueue(resultQueue);
        user.promptUserOptions();
    }


    private void handleSupplier() throws InterruptedException {
        System.out.println("Input username: ");
        String username = scanner.nextLine();
        System.out.println("Input password: ");
        String password = DigestUtils.sha256Hex(scanner.nextLine());

        sendLoginEvent(new String[]{username, password});
        waitForResultAndHandle();
    }

    private void sendLoginEvent(String[] credentials) throws InterruptedException {
        addEventToCommandQueue(OperationType.USER_LOGIN, credentials);
    }

    private void waitForResultAndHandle() throws InterruptedException {
        OperationsEvent<ResultOperationType, String> userResult = resultQueue.take();

        if (userResult.getType().equals(ResultOperationType.SUCCESS)) {
            user = deserializeJson(userResult.getPayload());
            user.setCommandQueue(commandQueue);
            user.setResultQueue(resultQueue);
            user.promptUserOptions();
        } else if (userResult.getType().equals(ResultOperationType.LOGIN_ERROR)) {
            System.out.println("Incorrect credentials, try again!");
            logger.log(Level.ERROR, ">O: Incorrect credentials, try again!");
        }
    }

    private Supplier deserializeJson(String payload) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(payload, new TypeToken<Supplier>() {
        }.getType());
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
