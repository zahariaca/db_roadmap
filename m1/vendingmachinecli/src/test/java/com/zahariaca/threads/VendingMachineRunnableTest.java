package com.zahariaca.threads;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zahariaca.dao.Dao;
import com.zahariaca.dao.file.FileUserDao;
import com.zahariaca.dao.file.FileVendingMachineDao;
import com.zahariaca.exceptions.NoSuchProductException;
import com.zahariaca.exceptions.ProductAlreadyExistsException;
import com.zahariaca.pojo.Product;
import com.zahariaca.pojo.users.User;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import com.zahariaca.threads.events.TransactionWriterOperationType;
import com.zahariaca.vendingmachine.OperatorInteractions;
import com.zahariaca.vendingmachine.VendingMachineInteractions;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 20.11.2018
 */
//TODO: fix sleep issue
class VendingMachineRunnableTest {
    private Runnable fileVendingMachineRunnable;
    private BlockingQueue<OperationsEvent<OperationType, String[]>> commandQueue;
    private BlockingQueue<OperationsEvent<ResultOperationType, String>> resultQueue;
    private BlockingQueue<OperationsEvent<TransactionWriterOperationType, Product>> transactionsQueue;
    private OperatorInteractions<Product, String[]> vendingMachine;
    private Dao<Product, Integer> vendingMachineDao;
    private Dao<User, String> userDao;
    private Set<User> userSet;
    private Set<Product> productSet;
    private User supplier;
    private Product product;

    @BeforeEach
    void init() {
        Product.setIdGenerator(new AtomicInteger(1000));
        commandQueue = new LinkedBlockingQueue<>(1);
        resultQueue = spy(new LinkedBlockingQueue<>(1));
        transactionsQueue = spy(new LinkedBlockingQueue<>(10));
        userSet = new TreeSet<>();
        supplier = new User("admin", "admin", true);
        userSet.add(supplier);
        userDao = new FileUserDao(userSet);
        productSet = new TreeSet<>();
        product = new Product("Soda", "Sugary refreshing beverage", 5.6f, supplier.getUserId());
        productSet.add(product);
        vendingMachineDao = new FileVendingMachineDao(productSet);
        vendingMachine = spy(new VendingMachineInteractions(vendingMachineDao));
        fileVendingMachineRunnable = VendingMachineRunnable.makeFileVendingMachineRunnable(commandQueue, resultQueue, transactionsQueue, vendingMachine, userDao);
    }

    @Test
    void testQuit() throws InterruptedException {
        Thread t = new Thread(fileVendingMachineRunnable);
        t.start();

        commandQueue.put(new OperationsEvent<OperationType, String[]>() {
            @Override
            public OperationType getType() {
                return OperationType.QUIT;
            }

            @Override
            public String[] getPayload() {
                return new String[0];
            }
        });

        Thread.sleep(1000);

        verify(transactionsQueue, times(1)).put(any());
    }

    @Test
    void testUserLoginSuccess() throws InterruptedException {
        Thread t = new Thread(fileVendingMachineRunnable);
        t.start();

        commandQueue.put(new OperationsEvent<OperationType, String[]>() {
            @Override
            public OperationType getType() {
                return OperationType.USER_LOGIN;
            }

            @Override
            public String[] getPayload() {
                return new String[]{"admin", DigestUtils.sha256Hex("admin")};
            }
        });

        Thread.sleep(1000);

        ArgumentCaptor<OperationsEvent> argument = ArgumentCaptor.forClass(OperationsEvent.class);
        verify(resultQueue).put(argument.capture());
        assertEquals(ResultOperationType.SUCCESS, argument.getValue().getType());
        assertEquals(serializeJson(supplier), argument.getValue().getPayload());
    }

    @Test
    void testUserLoginFail() throws InterruptedException {
        Thread t = new Thread(fileVendingMachineRunnable);
        t.start();

        commandQueue.put(new OperationsEvent<OperationType, String[]>() {
            @Override
            public OperationType getType() {
                return OperationType.USER_LOGIN;
            }

            @Override
            public String[] getPayload() {
                return new String[]{"admin", "wrong"};
            }
        });

        Thread.sleep(1000);

        ArgumentCaptor<OperationsEvent> argument = ArgumentCaptor.forClass(OperationsEvent.class);
        verify(resultQueue).put(argument.capture());
        assertEquals(ResultOperationType.LOGIN_ERROR, argument.getValue().getType());
        assertNull(argument.getValue().getPayload());
    }

    @Test
    void testDisplay() throws InterruptedException {
        Thread t = new Thread(fileVendingMachineRunnable);
        t.start();

        commandQueue.put(new OperationsEvent<OperationType, String[]>() {
            @Override
            public OperationType getType() {
                return OperationType.DISPLAY;
            }

            @Override
            public String[] getPayload() {
                return new String[]{""};
            }
        });

        Thread.sleep(1000);

        verify(vendingMachine, times(1)).displayProducts();

        ArgumentCaptor<OperationsEvent> argument = ArgumentCaptor.forClass(OperationsEvent.class);
        verify(resultQueue).put(argument.capture());
        assertEquals(ResultOperationType.SUCCESS, argument.getValue().getType());
        assertNull(argument.getValue().getPayload());
    }

    @Test
    void testDisplayWithArg() throws InterruptedException {
        Thread t = new Thread(fileVendingMachineRunnable);
        t.start();

        commandQueue.put(new OperationsEvent<OperationType, String[]>() {
            @Override
            public OperationType getType() {
                return OperationType.DISPLAY;
            }

            @Override
            public String[] getPayload() {
                return new String[]{supplier.getUserId()};
            }
        });

        Thread.sleep(1000);

        verify(vendingMachine, times(1)).displayProducts(new String[]{supplier.getUserId()});

        ArgumentCaptor<OperationsEvent> argument = ArgumentCaptor.forClass(OperationsEvent.class);
        verify(resultQueue).put(argument.capture());
        assertEquals(ResultOperationType.SUCCESS, argument.getValue().getType());
        assertNull(argument.getValue().getPayload());
    }

    @Test
    void testBuy() throws InterruptedException, NoSuchProductException {
        Thread t = new Thread(fileVendingMachineRunnable);
        t.start();

        commandQueue.put(new OperationsEvent<OperationType, String[]>() {
            @Override
            public OperationType getType() {
                return OperationType.BUY;
            }

            @Override
            public String[] getPayload() {
                return new String[]{"Soda"};
            }
        });

        Thread.sleep(1000);

        verify(vendingMachine, times(1)).buyProduct(new String[]{"Soda"});

        ArgumentCaptor<OperationsEvent> argument = ArgumentCaptor.forClass(OperationsEvent.class);
        verify(resultQueue).put(argument.capture());
        assertEquals(ResultOperationType.RETURN_PRODUCT, argument.getValue().getType());
        assertEquals("Product{name='Soda', description='Sugary refreshing beverage', price=5.6, uniqueId='1001'}",
                argument.getValue().getPayload());

        doNothing().when(transactionsQueue).put(any());
    }

    @Test
    void testAdd() throws InterruptedException, ProductAlreadyExistsException {
        Product newProduct = new Product("New Product", "New Description", 5.66f, supplier.getUserId());
        String[] payload = new String[]{newProduct.getName(), newProduct.getDescription(), String.valueOf(newProduct.getPrice()), supplier.getUserId()};
        Thread t = new Thread(fileVendingMachineRunnable);
        t.start();

        commandQueue.put(new OperationsEvent<OperationType, String[]>() {
            @Override
            public OperationType getType() {
                return OperationType.ADD;
            }

            @Override
            public String[] getPayload() {
                return payload;
            }
        });

        Thread.sleep(1000);

        ArgumentCaptor<OperationsEvent> argument = ArgumentCaptor.forClass(OperationsEvent.class);
        verify(resultQueue).put(argument.capture());
        assertEquals(ResultOperationType.SUCCESS, argument.getValue().getType());
        assertNull(argument.getValue().getPayload());
    }

    @Test
    void testAddFail() throws InterruptedException, ProductAlreadyExistsException {
        Thread t = new Thread(fileVendingMachineRunnable);
        t.start();

        commandQueue.put(new OperationsEvent<OperationType, String[]>() {
            @Override
            public OperationType getType() {
                return OperationType.ADD;
            }

            @Override
            public String[] getPayload() {
                return new String[]{product.getName(), product.getDescription(), String.valueOf(product.getPrice()), supplier.getUserId()};
            }
        });

        Thread.sleep(1000);

        ArgumentCaptor<OperationsEvent> argument = ArgumentCaptor.forClass(OperationsEvent.class);
        verify(resultQueue).put(argument.capture());
        assertEquals(ResultOperationType.ADD_ERROR, argument.getValue().getType());
        assertNull(argument.getValue().getPayload());
    }


    @Test
    void testDelete() throws InterruptedException, ProductAlreadyExistsException {
        Thread t = new Thread(fileVendingMachineRunnable);
        t.start();

        commandQueue.put(new OperationsEvent<OperationType, String[]>() {
            @Override
            public OperationType getType() {
                return OperationType.DELETE;
            }

            @Override
            public String[] getPayload() {
                return new String[]{String.valueOf(product.getUniqueId()), supplier.getUserId()};
            }
        });

        Thread.sleep(1000);

        ArgumentCaptor<OperationsEvent> argument = ArgumentCaptor.forClass(OperationsEvent.class);
        verify(resultQueue).put(argument.capture());
        assertEquals(ResultOperationType.SUCCESS, argument.getValue().getType());
        assertNull(argument.getValue().getPayload());
    }

    @Test
    void testDeleteFail() throws InterruptedException, ProductAlreadyExistsException {
        Thread t = new Thread(fileVendingMachineRunnable);
        t.start();

        commandQueue.put(new OperationsEvent<OperationType, String[]>() {
            @Override
            public OperationType getType() {
                return OperationType.DELETE;
            }

            @Override
            public String[] getPayload() {
                return new String[]{"1001111"};
            }
        });

        Thread.sleep(1000);

        ArgumentCaptor<OperationsEvent> argument = ArgumentCaptor.forClass(OperationsEvent.class);
        verify(resultQueue).put(argument.capture());
        assertEquals(ResultOperationType.DELETE_ERROR, argument.getValue().getType());
        assertNull(argument.getValue().getPayload());
    }


    @Test
    void testChange() throws InterruptedException {
        Product newProduct = new Product("New Product", "New Description", 5.66f, supplier.getUserId());
        String[] payload = new String[]{newProduct.getName(), newProduct.getDescription(), String.valueOf(newProduct.getPrice()), String.valueOf(product.getUniqueId()), supplier.getUserId()};
        Thread t = new Thread(fileVendingMachineRunnable);
        t.start();

        commandQueue.put(new OperationsEvent<OperationType, String[]>() {
            @Override
            public OperationType getType() {
                return OperationType.CHANGE_PRODUCT;
            }

            @Override
            public String[] getPayload() {
                return payload;
            }
        });

        Thread.sleep(1000);

        ArgumentCaptor<OperationsEvent> argument = ArgumentCaptor.forClass(OperationsEvent.class);
        verify(resultQueue).put(argument.capture());
        assertEquals(ResultOperationType.SUCCESS, argument.getValue().getType());
        assertNull(argument.getValue().getPayload());
    }

    @Test
    void testChangeFail() throws InterruptedException {
        Product newProduct = new Product("New Product", "New Description", 5.66f, supplier.getUserId());
        String[] payload = new String[]{newProduct.getName(), newProduct.getDescription(), "101111", String.valueOf(product.getUniqueId()), "wrong"};
        Thread t = new Thread(fileVendingMachineRunnable);
        t.start();

        commandQueue.put(new OperationsEvent<OperationType, String[]>() {
            @Override
            public OperationType getType() {
                return OperationType.CHANGE_PRODUCT;
            }

            @Override
            public String[] getPayload() {
                return payload;
            }
        });

        Thread.sleep(1000);

        ArgumentCaptor<OperationsEvent> argument = ArgumentCaptor.forClass(OperationsEvent.class);
        verify(resultQueue).put(argument.capture());
        assertEquals(ResultOperationType.CHANGE_ERROR, argument.getValue().getType());
        assertNull(argument.getValue().getPayload());
    }


    private String serializeJson(User payload) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(payload, new TypeToken<User>() {
        }.getType());
    }
}
