package com.zahariaca.vendingmachine;

import com.zahariaca.exceptions.NoSuchProductException;
import com.zahariaca.exceptions.ProductAlreadyExistsException;
import com.zahariaca.pojo.Product;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 12.11.2018
 */
class VendingMachineDaoTest {
    private Product productOne;
    private Product productTwo;
    private Set<Product> productSet;
    private VendingMachineDao vendingMachine;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void init() {
        Product.setIdGenerator(new AtomicInteger(1000));
        UUID supplierOneUUID = UUID.fromString("a3af93f2-0fff-42e0-b84c-6e507ece0264");
        UUID supplierTwoUUID = UUID.fromString("ac7ed436-14ee-47f2-8005-72e7674b8be3");
        productOne = new Product("Soda", "Sugary refreshing beverage", 5.6f, supplierOneUUID);
        productTwo = new Product("Chips", "Salty pack of thin potatoes", 8f, supplierTwoUUID);
        productSet = new HashSet<>();
        productSet.add(productOne);
        productSet.add(productTwo);
        vendingMachine = new VendingMachineDao(productSet);
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
        Product newProduct = new Product("NewProduct", "Test product description", 80f, UUID.randomUUID());
        vendingMachine.addProduct(newProduct);
        Product returnedNewProduct = null;
        try {
            returnedNewProduct = vendingMachine.buyProduct("NewProduct");
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
            vendingMachine.deleteProduct(String.valueOf(productOne.getUniqueId()));
        } catch (NoSuchProductException e) {
            fail(e.getMessage());
        }
        assertEquals(1, vendingMachine.getProductsSet().size());
        assertThrows(NoSuchProductException.class, () -> vendingMachine.buyProduct(productOne.getName()));
    }

    @Test
    void testDeleteProductThrowsException() {
        assertThrows(NoSuchProductException.class,
                () -> vendingMachine.deleteProduct("12345"));
    }

    @Test
    void testChangeProduct() {
        String newName = "NewName";
        String newDescription = "New description to be changed";
        String newPrice = "7f";
        int uniqueId = productOne.getUniqueId();
        Product changedProduct = new Product("NewName", "New description to be changed", Float.parseFloat("7f"), productOne.getUniqueId(), productOne.getSupplierId());
        try {
            vendingMachine.changeProduct(changedProduct);
        } catch (NoSuchProductException e) {
            e.printStackTrace();
        } catch (ProductAlreadyExistsException e) {
            e.printStackTrace();
        }
        Product returnedChangedProduct = null;
        try {
            returnedChangedProduct = vendingMachine.buyProduct("NewName");
        } catch (NoSuchProductException e) {
            fail("Product was not successfully changed!");
        }

        assertEquals(returnedChangedProduct.getName(), newName);
        assertEquals(returnedChangedProduct.getDescription(), newDescription);
        assertEquals(returnedChangedProduct.getPrice(), Float.parseFloat(newPrice));
        assertEquals(returnedChangedProduct.getUniqueId(), uniqueId, "Unique ID should NOT change when modifying products!");
    }

    @Test
    void testChangeProductThrowsWithWrongSupplierIdException() {
        assertThrows(NoSuchProductException.class,
                () -> vendingMachine.changeProduct(new Product("NewName", "New description to be changed", Float.parseFloat("7f"), productTwo.getSupplierId())));
    }

    @Test
    void testChangeProductThrowsWithWrongProductIDException() {
        assertThrows(NoSuchProductException.class,
                () -> vendingMachine.changeProduct(new Product("NewName", "New description to be changed", Float.parseFloat("7f"), productOne.getSupplierId())));
    }

    @Test
    void testBuyProductReturnsCorrectProduct() throws NoSuchProductException {
        Product returnedProduct = vendingMachine.buyProduct(productOne.getName());
        assertSame(productOne, returnedProduct);
    }

    @Test
    void testBuyProductThrowsException() {
        assertThrows(NoSuchProductException.class,
                () -> vendingMachine.buyProduct("InvalidProduct"),
                "Product: InvalidProduct does not exist.");
    }


    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

}
