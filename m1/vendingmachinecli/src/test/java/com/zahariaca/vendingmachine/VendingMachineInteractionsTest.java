package com.zahariaca.vendingmachine;

import com.zahariaca.dao.Dao;
import com.zahariaca.dao.VendingMachineDao;
import com.zahariaca.exceptions.IllegalProductOperation;
import com.zahariaca.exceptions.NoSuchProductException;
import com.zahariaca.exceptions.ProductAlreadyExistsException;
import com.zahariaca.pojo.Product;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 12.11.2018
 */
class VendingMachineInteractionsTest {
    public static final String NEW_PRODUCT_NAME = "NewProduct";
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private Product productOne;
    private Product productTwo;
    private Set<Product> productSet;
    private Dao<Product, Integer> vendingMachineDao;
    private OperatorInteractions<Product, String[]> vendingMachine;
    private String supplierOneUUID = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918";


    @BeforeEach
    void init() {
        Product.setIdGenerator(new AtomicInteger(1000));
        String supplierTwoUUID = "b1f2aebc61a4ee3ed0c429fe44c259612c2d857abcca0b632530fe70f0950b05";
        productOne = new Product("Soda", "Sugary refreshing beverage", 5.6f, supplierOneUUID);
        productTwo = new Product("Chips", "Salty pack of thin potatoes", 8f, supplierTwoUUID);
        productSet = new TreeSet<>();
        productSet.add(productOne);
        productSet.add(productTwo);
        vendingMachineDao = new VendingMachineDao(productSet);
        vendingMachine = new VendingMachineInteractions(vendingMachineDao);
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    void testDisplayProducts() {
        StringBuilder testOutput = new StringBuilder();
        productSet.forEach(p -> testOutput.append(String.format("%s%n", p)));
        vendingMachine.displayProducts();
        assertEquals(testOutput.toString(), outContent.toString());
    }

    @Test
    void testAddProduct() throws ProductAlreadyExistsException {
        Product newProduct = new Product("NewProduct", "Test product description", 80f, DigestUtils.sha256Hex("TESTING"));
        vendingMachine.addProduct(newProduct);
        Product returnedNewProduct = null;
        try {
            returnedNewProduct = vendingMachine.buyProduct(new String[]{"NewProduct"});
        } catch (NoSuchProductException e) {
            fail("Adding the product was not successful");
        }
        assertSame(newProduct, returnedNewProduct);
    }

    @Test
    void testAddProductThrowsExceptionOnDuplicate() {
        assertThrows(ProductAlreadyExistsException.class,
                () -> vendingMachine.addProduct(productOne),
                String.format("The product: %n%s%n already exists! Cannot add!", productOne));
    }

    @Test
    void testDeleteProduct() {
        try {
            vendingMachine.deleteProduct(new String[]{
                    String.valueOf(productOne.getUniqueId()),
                    supplierOneUUID.toString()});
        } catch (NoSuchProductException | IllegalProductOperation e) {
            fail(e.getMessage());
        }
        assertEquals(1, vendingMachine.getProductsSet().size());
        assertThrows(NoSuchProductException.class,
                () -> vendingMachine.buyProduct(new String[]{productOne.getName()}));
    }

    @Test
    void testDeleteProductThrowsException() {
        assertThrows(NoSuchProductException.class,
                () -> vendingMachine.deleteProduct(new String[]{
                        "12345",
                        productOne.getSupplierId().toString()}));
    }

    @Test
    void testDeleteProductThrowsExceptionWhenSupplierIdIsWrong() {
        assertThrows(IllegalProductOperation.class,
                () -> vendingMachine.deleteProduct(new String[]{
                        String.valueOf(productOne.getUniqueId()),
                        productTwo.getSupplierId().toString()}));
    }

    @Test
    void testChangeProduct() {
        String newDescription = "New description to be changed";
        String newPrice = "7f";
        int uniqueId = productOne.getUniqueId();
        String[] changedProduct = new String[]{NEW_PRODUCT_NAME, "New description to be changed", "7f", String.valueOf(productOne.getUniqueId()), productOne.getSupplierId()};
        try {
            vendingMachine.changeProduct(changedProduct);
        } catch (NoSuchProductException | ProductAlreadyExistsException e) {
            e.printStackTrace();
        }
        Product returnedChangedProduct = null;
        try {
            returnedChangedProduct = vendingMachine.buyProduct(new String[]{NEW_PRODUCT_NAME});
        } catch (NoSuchProductException e) {
            fail("Product was not successfully changed!");
        }

        assertEquals(returnedChangedProduct.getName(), NEW_PRODUCT_NAME);
        assertEquals(returnedChangedProduct.getDescription(), newDescription);
        assertEquals(returnedChangedProduct.getPrice(), Float.parseFloat(newPrice));
        assertEquals(returnedChangedProduct.getUniqueId(), uniqueId, "Unique ID should NOT change when modifying products!");
    }

    @Test
    void testChangeProductThrowsWithWrongSupplierIdException() {
        assertThrows(NoSuchProductException.class,
                () -> vendingMachine.changeProduct(new String[]{
                        NEW_PRODUCT_NAME,
                        "New description to be changed",
                        "7f",
                        String.valueOf(productOne.getUniqueId()),
                        productTwo.getSupplierId()}));
    }

    @Test
    void testChangeProductThrowsWithWrongProductIDException() {
        assertThrows(NoSuchProductException.class,
                () -> vendingMachine.changeProduct(new String[]{
                        NEW_PRODUCT_NAME,
                        "New description to be changed",
                        "7f",
                        String.valueOf(productTwo.getUniqueId()),
                        productOne.getSupplierId()}));
    }

    @Test
    void testBuyProductReturnsCorrectProduct() throws NoSuchProductException {
        Product returnedProduct = vendingMachine.buyProduct(new String[]{productOne.getName()});
        assertSame(productOne, returnedProduct);
    }

    @Test
    void testBuyProductThrowsException() {
        assertThrows(NoSuchProductException.class,
                () -> vendingMachine.buyProduct(new String[]{"InvalidProduct"}),
                "Product: InvalidProduct does not exist.");
    }


    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

}
