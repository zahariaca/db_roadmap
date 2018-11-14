package com.zahariaca.users;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zahariaca.exceptions.UserInUnsafeStateException;
import com.zahariaca.pojo.Product;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import com.zahariaca.utils.UserInputUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
// TODO: refactor, to much duplicate code between Customer and Supplier, extract in parent class?
public class Supplier implements User<BlockingQueue<OperationsEvent<OperationType, String>>, BlockingQueue<OperationsEvent<ResultOperationType, Product>>> {
    private final Logger logger = LogManager.getLogger(Supplier.class);
    private BlockingQueue<OperationsEvent<OperationType, String>> commandQueue = null;
    private BlockingQueue<OperationsEvent<ResultOperationType, Product>> resultQueue = null;
    private Scanner scanner;

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
                                    "   [4] Change price of product. %n",
                                    "   [5] Change name of product. %n",
                                    "   [q/quit] to end process. %n")));

            String userInput = scanner.nextLine();

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
        addEventToCommandQueue(OperationType.DISPLAY, "");
        System.out.println(Thread.currentThread() + " ++ Display products");
        logger.log(Level.DEBUG, ">E: firing: {}", OperationType.DISPLAY);
    }


    private void handleAddOption() throws InterruptedException {
        Product newProduct = promptForAdd();

        addEventToCommandQueue(OperationType.ADD, serializedJsonProduct(newProduct));

        System.out.println("Add product");
    }

    private Product promptForAdd() {
        String productName;
        String productDescription;
        String productPrice;
        // TODO: hardcoded supplier ID. Temporary, should be removed soon!!
        // FIXME:
        String supplierId = "a3af93f2-0fff-42e0-b84c-6e507ece0264";

        System.out.println("Name of new product: ");
        productName = scanner.nextLine();
        System.out.println("Description of new product: ");
        productDescription = scanner.nextLine();
        System.out.println("Price of new product: ");
        productPrice = scanner.nextLine();
        // TODO: Product price should be checked before being parsed as float

        return new Product(productName, productDescription, Float.parseFloat(productPrice), UUID.randomUUID(), UUID.fromString(supplierId));
    }

    //TODO: rethink...probably not the best way to send product, only like this to not create an extra queue
    private String serializedJsonProduct(Product newProduct) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(newProduct);
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

    private void addEventToCommandQueue(OperationType commandOperation, String userOrder) throws InterruptedException {
        commandQueue.put(new OperationsEvent<>() {
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
