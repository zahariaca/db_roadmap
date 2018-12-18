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
public class SupplierCli implements Cli<BlockingQueue<OperationsEvent<OperationType, String[]>>, BlockingQueue<OperationsEvent<ResultOperationType, String>>> {
    private final Logger logger = LogManager.getLogger(SupplierCli.class);
    private BlockingQueue<OperationsEvent<OperationType, String[]>> commandQueue = null;
    private BlockingQueue<OperationsEvent<ResultOperationType, String>> resultQueue = null;
    private Scanner scanner;
    private volatile boolean continueCondition = true;
    private User user;

    public SupplierCli(User user) {
        this.user = user;
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
        logger.log(Level.DEBUG, ">O: Handling supplier");

        checkQueues();

        scanner = new Scanner(System.in);

        while (continueCondition) {
            System.out.println(
                    String.format(
                            UserInputUtils.INSTANCE.constructPromptMessage(
                                    "%nSelect an operation:%n",
                                    "   [1] See product list. %n",
                                    "   [2] Add product. %n",
                                    "   [3] Delete product. %n",
                                    "   [4] Change product. %n",
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
                handleAddOption();
            } else if (Integer.valueOf(userInput) == 3) {
                handleDeleteProduct();
            } else if (Integer.valueOf(userInput) == 4) {
                handleChangeProduct();
            }
        } catch (InterruptedException ie) {
            logger.log(Level.ERROR, ie.getMessage());
            Thread.currentThread().interrupt();
        }


        return true;
    }

    private void sendDisplayEvent() throws InterruptedException {
        addEventToCommandQueue(OperationType.DISPLAY, new String[]{user.getUserId()});
        logger.log(Level.DEBUG, ">E: firing: {}", OperationType.DISPLAY);
        resultQueue.take();
    }


    private void handleAddOption() throws InterruptedException {
        String[] newProduct = promptForAdd();

        addEventToCommandQueue(OperationType.ADD, newProduct);

        System.out.println("Adding product: " + newProduct[0]);

        OperationsEvent<ResultOperationType, String> result = resultQueue.take();
        if (result.getType().equals(ResultOperationType.SUCCESS)) {
            System.out.println("Successfully added product to VendingMachine!");
        } else if (result.getType().equals(ResultOperationType.ADD_ERROR)) {
            System.out.println("Could not add product. Try again...");
        }
    }

    private String[] promptForAdd() {
        String productName;
        String productDescription;
        String productPrice;

        System.out.println("Name of new product: ");
        productName = scanner.nextLine();
        System.out.println("Description of new product: ");
        productDescription = scanner.nextLine();
        System.out.println("Price of new product: ");
        productPrice = scanner.nextLine();

        while (!UserInputUtils.INSTANCE.checkIsFloat(productPrice)) {
            System.out.println("Incorrect price. Please input a float. (e.g: 5.67)");
            productPrice = scanner.nextLine();
        }

        return new String[]{productName, productDescription, productPrice, user.getUserId()};
    }

    private void handleDeleteProduct() throws InterruptedException {
        String[] productToBeDeleted = promptForDelete();

        addEventToCommandQueue(OperationType.DELETE, productToBeDeleted);

        System.out.println("Deleting product with ID: " + productToBeDeleted);


        OperationsEvent<ResultOperationType, String> result = resultQueue.take();
        if (result.getType().equals(ResultOperationType.SUCCESS)) {
            System.out.println("Successfully deleted product from VendingMachine!");
        } else if (result.getType().equals(ResultOperationType.DELETE_ERROR)) {
            System.out.println("Could not delete product. Try again...");
        }
    }

    private String[] promptForDelete() {
        String productUniqueId;

        System.out.println("Unique ID of the product that should be deleted: ");
        productUniqueId = scanner.nextLine();

        return new String[]{productUniqueId, user.getUserId()};

    }

    private void handleChangeProduct() throws InterruptedException {
        String[] newProduct = promptForChange();

        addEventToCommandQueue(OperationType.CHANGE_PRODUCT, newProduct);

        System.out.println("Changing product: " + newProduct);

        OperationsEvent<ResultOperationType, String> result = resultQueue.take();
        if (result.getType().equals(ResultOperationType.SUCCESS)) {
            System.out.println("Successfully changed product to VendingMachine!");
        } else if (result.equals(ResultOperationType.CHANGE_ERROR)) {
            System.out.println("Could not change product. Try again...");
        }
    }

    private String[] promptForChange() {
        String currentScan;
        String productUniqueId;
        String productName;
        String productDescription;
        String productPrice;
        String userId;

        System.out.println("Unique ID of the product that should be changed: ");
        currentScan = scanner.nextLine();
        productUniqueId = currentScan.isEmpty() ? null : currentScan;

        while (productUniqueId == null || !UserInputUtils.INSTANCE.checkIsInteger(productUniqueId)) {
            System.out.println("Incorrect unique id. Please checked the displayed products and input a integer. (e.g: 1000)");
            currentScan = scanner.nextLine();
            productUniqueId = currentScan.isEmpty() ? null : currentScan;
        }

        System.out.println("Name of new product (to keep old value, hit ENTER): ");
        currentScan = scanner.nextLine();
        productName = currentScan.isEmpty() ? null : currentScan;
        System.out.println("Description of new product (to keep old value, hit ENTER): ");
        currentScan = scanner.nextLine();
        productDescription = currentScan.isEmpty() ? null : currentScan;
        System.out.println("Price of new product (to keep old value, hit ENTER): ");
        currentScan = scanner.nextLine();
        productPrice = currentScan.isEmpty() ? null : currentScan;

        if (productPrice != null) {
            while (productPrice != null && !UserInputUtils.INSTANCE.checkIsFloat(productPrice)) {
                System.out.println("Incorrect price. Please input a float. (e.g: 5.67)");
                currentScan = scanner.nextLine();
                productPrice = currentScan.isEmpty() ? null : currentScan;
            }
        }

        userId = user.getUserId().isEmpty() ? null : user.getUserId();

        return new String[]{productName, productDescription, productPrice, productUniqueId, userId};
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
