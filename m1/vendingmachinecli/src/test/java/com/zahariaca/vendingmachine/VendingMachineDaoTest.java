package com.zahariaca.vendingmachine;

import com.zahariaca.exceptions.NoSuchProductException;
import com.zahariaca.pojo.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 12.11.2018
 */
public class VendingMachineDaoTest {
    private Product productOne;
    private Product productTwo;
    private Set<Product> productSet;
    private VendingMachineDao vendingMachine;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void init() {
        productOne = new Product("Soda", "Sugary refreshing beverage", 5.6f);
        productTwo = new Product("Chips", "Salty pack of thin potatoes", 8f);
        productSet = new HashSet<Product>();
        productSet.add(productOne);
        productSet.add(productTwo);
        vendingMachine = new VendingMachineDao(productSet);
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void testDisplayProducts() {
        StringBuilder testOutput = new StringBuilder();
        productSet.stream().forEach(p -> testOutput.append(String.format("%s%n", p)));
        vendingMachine.displayProducts();
        assertEquals(testOutput.toString(), outContent.toString());
    }

    @Test
    public void testAddProduct() {
        Product newProduct = new Product("NewProduct", "Test product description", 80f);
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
    public void testDeleteProduct() {
        try {
            vendingMachine.deleteProduct(productOne.getName(), productOne.getUniqueId());
        } catch (NoSuchProductException e) {
            fail(e.getMessage());
        }
        assertEquals(1, vendingMachine.getProducts().size());
        assertThrows(NoSuchProductException.class, () -> vendingMachine.buyProduct(productOne.getName()));
    }

    @Test
    public void testDeleteProductThrowsException() {
        assertThrows(NoSuchProductException.class,
                () -> {
                    vendingMachine.deleteProduct("InvalidProduct", "InvalidUniqueID");
                }, "Product: InvalidProduct with uniqueID: InvalidUniqueID, was not found. Could not be deleted!");
    }

    @Test
    public void testChangeProduct() {
        String newName = "NewName";
        String newDescription = "New description to be changed";
        String newPrice = "7f";
        String uniqueId = productOne.getUniqueId();
        vendingMachine.changeProduct(newName, newDescription, newPrice, uniqueId);
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
    public void testBuyProductReturnsCorrectProduct() throws NoSuchProductException {
        Product returnedProduct = vendingMachine.buyProduct(productOne.getName());
        assertSame(productOne, returnedProduct);
    }

    @Test
    public void testBuyProductThrowsException() {
        assertThrows(NoSuchProductException.class,
                () -> vendingMachine.deleteProduct("InvalidProduct", "InvalidUniqueID"),
                "Product: InvalidProduct does not exist.");
    }


    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

}
