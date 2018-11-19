package com.zahariaca.threads;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zahariaca.dao.Dao;
import com.zahariaca.exceptions.IllegalProductOperation;
import com.zahariaca.exceptions.NoSuchProductException;
import com.zahariaca.exceptions.ProductAlreadyExistsException;
import com.zahariaca.filehandlers.PersistenceFileWriter;
import com.zahariaca.pojo.Product;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import com.zahariaca.threads.events.TransactionWriterOperationType;
import com.zahariaca.pojo.users.User;
import com.zahariaca.utils.FileUtils;
import com.zahariaca.vendingmachine.OperatorInteractions;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 30.10.2018
 */
public class VendingMachineRunnable implements Runnable {
    private static final String EXCEPTION_MESSAGE = ">E: Message: ";
    private final Logger logger = LogManager.getLogger(VendingMachineRunnable.class);
    private final OperatorInteractions<Product, String[]> vendingMachine;
    private final BlockingQueue<OperationsEvent<OperationType, String[]>> commandQueue;
    private final BlockingQueue<OperationsEvent<ResultOperationType, String>> resultQueue;
    private final BlockingQueue<OperationsEvent<TransactionWriterOperationType, Product>> transactionsQueue;
    private final Dao<User, String> usersDao;
    private volatile boolean continueCondition = true;

    public VendingMachineRunnable(BlockingQueue<OperationsEvent<OperationType, String[]>> commandQueue,
                                  BlockingQueue<OperationsEvent<ResultOperationType, String>> resultQueue,
                                  BlockingQueue<OperationsEvent<TransactionWriterOperationType, Product>> transactionsQueue,
                                  OperatorInteractions<Product, String[]> vendingMachine,
                                  Dao<User, String> usersDao) {
        this.commandQueue = commandQueue;
        this.transactionsQueue = transactionsQueue;
        this.vendingMachine = vendingMachine;
        this.resultQueue = resultQueue;
        this.usersDao = usersDao;
        logger.log(Level.INFO, ">O: instantiated");
    }

    @Override
    public void run() {
        try {
            while (continueCondition) {
                logger.log(Level.DEBUG, ">O: infinite loop enter.");
                OperationsEvent<OperationType, String[]> receivedCommandEvent = commandQueue.take();
                if (receivedCommandEvent.getType().equals(OperationType.USER_LOGIN)) {
                    handleUserLogin(receivedCommandEvent);
                } else if (receivedCommandEvent.getType().equals(OperationType.DISPLAY)) {
                    handleDisplayProcess(receivedCommandEvent);
                } else if (receivedCommandEvent.getType().equals(OperationType.BUY)) {
                    handleBuyProcess(receivedCommandEvent);
                } else if (receivedCommandEvent.getType().equals(OperationType.ADD)) {
                    handleAddEvent(receivedCommandEvent.getPayload());
                } else if (receivedCommandEvent.getType().equals(OperationType.DELETE)) {
                    handleDeleteEvent(receivedCommandEvent.getPayload());
                } else if (receivedCommandEvent.getType().equals(OperationType.CHANGE_PRODUCT)) {
                    handleChangeEvent(receivedCommandEvent.getPayload());
                } else if (receivedCommandEvent.getType().equals(OperationType.QUIT)) {
                    handleShutdown();
                }
            }
            logger.log(Level.DEBUG, ">O: infinite loop exit.");
        } catch (InterruptedException e) {
            logger.log(Level.ERROR, ">O: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void handleUserLogin(OperationsEvent<OperationType, String[]> receivedEvent) throws InterruptedException {
        String username = receivedEvent.getPayload()[0];
        String password = receivedEvent.getPayload()[1];

        Optional<User> userOptional = usersDao.get(username);

        if (userOptional.isPresent() && userOptional.get().getUserPassword().equals(password)) {
            addEventToResultQueue(ResultOperationType.SUCCESS, serializeJson(userOptional.get()));
        } else {
            addEventToResultQueue(ResultOperationType.LOGIN_ERROR, "");
        }
    }

    private void handleDisplayProcess(OperationsEvent<OperationType, String[]> receivedEvent) throws InterruptedException {
        if (receivedEvent.getPayload()[0].equals("")) {
            vendingMachine.displayProducts();
        } else {
            vendingMachine.displayProducts(receivedEvent.getPayload());
        }
        addEventToResultQueue(ResultOperationType.SUCCESS, null);
    }

    private void handleBuyProcess(OperationsEvent<OperationType, String[]> receivedEvent) throws InterruptedException {
        try {
            sendProductToClient(receivedEvent.getPayload());
        } catch (NoSuchProductException e) {
            logger.log(Level.WARN, ">E: Product does not exist. " + e.getMessage());
            handleNoProduct();
        }
    }

    private void handleAddEvent(String[] payload) throws InterruptedException {
        String productName = Objects.requireNonNull(payload[0], "Name cannot be null");
        String productDescription = Objects.requireNonNull(payload[1], "Description cannot be null");
        String productPrice = Objects.requireNonNull(payload[2], "Price cannot be null");
        String supplierId = Objects.requireNonNull(payload[3], "SupplierCli Id cannot be null");
        Product newProduct = new Product(productName, productDescription, Float.parseFloat(productPrice), supplierId);

        try {
            vendingMachine.addProduct(newProduct);
            addEventToResultQueue(ResultOperationType.SUCCESS, null);
        } catch (ProductAlreadyExistsException e) {
            logger.log(Level.ERROR, EXCEPTION_MESSAGE, e.getMessage());
            addEventToResultQueue(ResultOperationType.ADD_ERROR, null);
        }
    }


    private void handleDeleteEvent(String[] payload) throws InterruptedException {
        try {
            vendingMachine.deleteProduct(payload);
            addEventToResultQueue(ResultOperationType.SUCCESS, null);
        } catch (NoSuchProductException | IllegalProductOperation e) {
            logger.log(Level.ERROR, EXCEPTION_MESSAGE, e.getMessage());
            addEventToResultQueue(ResultOperationType.DELETE_ERROR, null);
        }
    }

    private void handleChangeEvent(String[] payload) throws InterruptedException {
        try {
            vendingMachine.changeProduct(payload);
            addEventToResultQueue(ResultOperationType.SUCCESS, null);
        } catch (NoSuchProductException | ProductAlreadyExistsException e) {
            logger.log(Level.ERROR, EXCEPTION_MESSAGE, e.getMessage());
            addEventToResultQueue(ResultOperationType.CHANGE_ERROR, null);
        }
    }

    private void handleShutdown() throws InterruptedException {
        handleTransactionQueueShutdown();
        handleFinalOperation();
    }


    private void handleTransactionQueueShutdown() throws InterruptedException {
        addEventToTransactionsQueue(TransactionWriterOperationType.QUIT, null);
    }

    private void handleFinalOperation() {
        try {
            File productsFile = FileUtils.INSTANCE.getFile("persistence/products.json");
            PersistenceFileWriter.INSTANCE.handleFileWrite(productsFile, vendingMachine.getProductsSet());
            File usersFile = FileUtils.INSTANCE.getFile("persistence/users.json");
            PersistenceFileWriter.INSTANCE.handleFileWrite(usersFile, usersDao.getAll());
            System.out.println("Application terminating gracefully!");
            System.out.println("Goodbye!");
            continueCondition = false;
        } catch (IOException e) {
            logger.log(Level.ERROR, ">E: Could not write persistence file on shutdown. Message: {} ", e.getMessage());
            System.out.println("Application terminating without saving products! Error occurred!");
            System.out.println("Goodbye!");
            continueCondition = false;
        }
    }


    private void sendProductToClient(String[] payload) throws NoSuchProductException, InterruptedException {
        Product returnedProduct = vendingMachine.buyProduct(payload);
        System.out.println("Delivering your product: " + returnedProduct);
        // TODO: maybe change this and serialize product...posibly not needed though
        addEventToResultQueue(ResultOperationType.RETURN_PRODUCT, returnedProduct.toString());
        addEventToTransactionsQueue(TransactionWriterOperationType.WRITE, returnedProduct);
    }

    private void handleNoProduct() throws InterruptedException {
        addEventToResultQueue(ResultOperationType.PRODUCT_NOT_FOUND, null);
    }


    private Product deserializeJsonProduct(String payload) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(payload, new TypeToken<Product>() {
        }.getType());
    }

    private void addEventToResultQueue(ResultOperationType resultOperationType, String returnedProduct) throws InterruptedException {
        resultQueue.put(new OperationsEvent<>() {
            @Override
            public ResultOperationType getType() {
                return resultOperationType;
            }

            @Override
            public String getPayload() {
                return returnedProduct;
            }
        });
    }

    private void addEventToTransactionsQueue(TransactionWriterOperationType writeOperation, Product returnedProduct) throws InterruptedException {
        transactionsQueue.put(new OperationsEvent<>() {
            @Override
            public TransactionWriterOperationType getType() {
                return writeOperation;
            }

            @Override
            public Product getPayload() {
                return returnedProduct;
            }
        });
    }

    private String serializeJson(User payload) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.excludeFieldsWithoutExposeAnnotation().create();
        return  gson.toJson(payload, new TypeToken<User>(){}.getType());
    }
}
