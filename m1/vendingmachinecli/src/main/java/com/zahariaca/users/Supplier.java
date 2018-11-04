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
public class Supplier implements User<BlockingQueue<OperationsEvent<OperationType, String>>, BlockingQueue<OperationsEvent<ResultOperationType, Product>>> {
    private final Logger logger = LogManager.getLogger(Supplier.class);
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
        logger.log(Level.DEBUG, ">O: Handling supplier");

        checkQueues();

        boolean continueCondition = true;
        Scanner scanner = new Scanner(System.in);

        while (continueCondition) {
            // TODO show only products related to this supplier, not all (m1 or m2)
            System.out.println(
                    String.format(
                            UserInputUtils.INSTANCE.constructPromptMessage(
                                    "%nSelect an operation:%n",
                                    "   [1] See product list. %n",
                                    "   [2] Add product. %n",
                                    "   [3] Delete product. %n",
                                    "   [4] Change price of product. %n",
                                    "   [5] Change name of product. %n",
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
                //TODO: handle add process
                handleAddOption();
            } else if (Integer.valueOf(userInput) == 3) {
                //TODO: handle delete process
                handleDeleteProduct();
            } else if (Integer.valueOf(userInput) == 4) {
                //TODO: handle change price process
                handleChangePrice();
            } else if (Integer.valueOf(userInput) == 5) {
                //TODO: handle change name process
                handleChangeName();
            }
        } catch (InterruptedException ie) {
            logger.log(Level.ERROR, ie.getMessage());
            Thread.currentThread().interrupt();
        }


        return true;
    }

    private void sendDisplayEvent() throws InterruptedException {
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
    }

    private void handleAddOption() {
        System.out.println("Add product");
    }


    private void handleDeleteProduct() {
        System.out.println("Delete product");
    }

    private void handleChangePrice() {
        System.out.println("Change product price");
    }

    private void handleChangeName() {
        System.out.println("Change product name");
    }

}
