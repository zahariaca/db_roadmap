package com.zahariaca.users;

import com.google.gson.annotations.Expose;
import com.zahariaca.exceptions.UserInUnsafeStateException;
import com.zahariaca.pojo.Product;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import com.zahariaca.utils.UserInputUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class Supplier implements User<BlockingQueue<OperationsEvent<OperationType, String[]>>, BlockingQueue<OperationsEvent<ResultOperationType, String>>>, Comparable<Supplier> {
    @Expose(serialize = false)
    private final Logger logger = LogManager.getLogger(Supplier.class);

    //TODO: username assumed unique, no mechanism to add users, if added unique username must be guarded for
    @Expose
    private String username;
    @Expose
    private String userPassword;
    @Expose
    private String userId;
    // isSupplier has no usage for now.
    @Expose
    private boolean isSupplier;

    @Expose(serialize = false)
    private BlockingQueue<OperationsEvent<OperationType, String[]>> commandQueue = null;
    @Expose(serialize = false)
    private BlockingQueue<OperationsEvent<ResultOperationType, String>> resultQueue = null;
    @Expose(serialize = false)
    private Scanner scanner;
    @Expose(serialize = false)
    private volatile boolean continueCondition = true;

    public Supplier(String username, String userPassword, boolean isSupplier) {
        this.username = username;
        this.userPassword = DigestUtils.sha256Hex(userPassword);
        this.userId = DigestUtils.sha256Hex(username);
        this.isSupplier = isSupplier;
    }

    @Override
    public void setCommandQueue(BlockingQueue<OperationsEvent<OperationType, String[]>> commandQueue) {
        this.commandQueue = commandQueue;
    }

    @Override
    public void setResultQueue(BlockingQueue<OperationsEvent<ResultOperationType, String>> resultQueue) {
        this.resultQueue = resultQueue;
    }

    @Override
    public void promptUserOptions() {
        // offer customer specific option and handle appropriately
        logger.log(Level.DEBUG, ">O: Handling supplier");

        checkQueues();

        scanner = new Scanner(System.in);

        while (continueCondition) {
            // TODO show only products related to this supplier, not all (m1 or m2)
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


    @Override
    public boolean handleUserInput(String userInput) {
        try {
            if (Integer.valueOf(userInput) == 1) {
                sendDisplayEvent();
            } else if (Integer.valueOf(userInput) == 2) {
                handleAddOption();
            } else if (Integer.valueOf(userInput) == 3) {
                sendDisplayEvent();
                handleDeleteProduct();
            } else if (Integer.valueOf(userInput) == 4) {
                sendDisplayEvent();
                handleChangeProduct();
            }
        } catch (InterruptedException ie) {
            logger.log(Level.ERROR, ie.getMessage());
            Thread.currentThread().interrupt();
        }


        return true;
    }

    private void sendDisplayEvent() throws InterruptedException {
        addEventToCommandQueue(OperationType.DISPLAY, new String[]{getUserId()});
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

        return new String[]{productName, productDescription, productPrice, getUserId()};
    }

    private void handleDeleteProduct() throws InterruptedException {
        String[] productToBeDeleted = promptForDelete();

        addEventToCommandQueue(OperationType.DELETE, productToBeDeleted);

        System.out.println("Deleting product with ID: " + productToBeDeleted);

        if (resultQueue.take().getType().equals(ResultOperationType.SUCCESS)) {
            System.out.println("Successfully deleted product from VendingMachine!");
        } else if (resultQueue.take().getType().equals(ResultOperationType.ADD_ERROR)) {
            System.out.println("Could not delete product. Try again...");
        }
    }

    private String[] promptForDelete() {
        String productUniqueId;

        System.out.println("Unique ID of the product that should be deleted: ");
        productUniqueId = scanner.nextLine();

        return new String[]{productUniqueId};

    }

    private void handleChangeProduct() throws InterruptedException {
        String[] newProduct = promptForChange();

        addEventToCommandQueue(OperationType.CHANGE_PRODUCT, newProduct);

        System.out.println("Adding product: " + newProduct);

        if (resultQueue.take().getType().equals(ResultOperationType.SUCCESS)) {
            System.out.println("Successfully added product to VendingMachine!");
        } else if (resultQueue.take().getType().equals(ResultOperationType.ADD_ERROR)) {
            System.out.println("Could not add product. Try again...");
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

        if (productPrice == null || !productPrice.isEmpty()) {
            while (!UserInputUtils.INSTANCE.checkIsFloat(productPrice)) {
                System.out.println("Incorrect price. Please input a float. (e.g: 5.67)");
                currentScan = scanner.nextLine();
                productPrice = currentScan.isEmpty() ? null : currentScan;
            }
        }

        userId = getUserId().isEmpty() ? null : getUserId();

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

    public String getUsername() {
        return username;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isSupplier() {
        return isSupplier;
    }

    @Override
    public int compareTo(Supplier o) {
        return this.getUserId().compareTo(o.getUserId());
    }
}
