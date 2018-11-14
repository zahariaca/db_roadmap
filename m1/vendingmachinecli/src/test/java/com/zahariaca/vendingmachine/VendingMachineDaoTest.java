package com.zahariaca.vendingmachine;

import com.zahariaca.exceptions.NoSuchProductException;
import com.zahariaca.exceptions.ProductAlreadyExistsException;
import com.zahariaca.pojo.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
        UUID productOneUUID = UUID.fromString("8f70c754-4257-43d1-91e8-9438838d23cd");
        UUID productTwoUUID = UUID.fromString("1a24f45c-bde1-4242-8545-22c879fdf8b8");
        UUID supplierOneUUID = UUID.fromString("a3af93f2-0fff-42e0-b84c-6e507ece0264");
        UUID supplierTwoUUID = UUID.fromString("ac7ed436-14ee-47f2-8005-72e7674b8be3");
        productOne = new Product("Soda", "Sugary refreshing beverage", 5.6f, productOneUUID, supplierOneUUID);
        productTwo = new Product("Chips", "Salty pack of thin potatoes", 8f, productTwoUUID, supplierTwoUUID);
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
    public void testAddProduct() throws ProductAlreadyExistsException {
        Product newProduct = new Product("NewProduct", "Test product description", 80f, UUID.randomUUID(), UUID.randomUUID());
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
    public void testAddProductThrowsExceptionOnDuplicate() {
        assertThrows(ProductAlreadyExistsException.class,
                () -> vendingMachine.addProduct(productOne),
                String.format("The product: %n%s%n already exists! Cannot add!", productOne));
    }

    @Test
    public void testDeleteProduct() {
        try {
            vendingMachine.deleteProduct(productOne.getName(), productOne.getUniqueId().toString());
        } catch (NoSuchProductException e) {
            fail(e.getMessage());
        }
        assertEquals(1, vendingMachine.getProductsSet().size());
        assertThrows(NoSuchProductException.class, () -> vendingMachine.buyProduct(productOne.getName()));
    }

    @Test
    public void testDeleteProductThrowsException() {
        assertThrows(NoSuchProductException.class,
                () -> vendingMachine.deleteProduct("InvalidProduct", "InvalidUniqueID"));
    }

    @Test
    public void testChangeProduct() {
        String newName = "NewName";
        String newDescription = "New description to be changed";
        String newPrice = "7f";
        String uniqueId = productOne.getUniqueId().toString();
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
        assertEquals(returnedChangedProduct.getUniqueId(), UUID.fromString(uniqueId), "Unique ID should NOT change when modifying products!");
    }

    @Test
    public void testChangeProductThrowsWithWrongSupplierIdException() {
        assertThrows(NoSuchProductException.class,
                () -> vendingMachine.changeProduct(new Product("NewName", "New description to be changed", Float.parseFloat("7f"), productOne.getUniqueId(), productTwo.getSupplierId())));
    }

    @Test
    public void testChangeProductThrowsWithWrongProductIDException() {
        assertThrows(NoSuchProductException.class,
                () -> vendingMachine.changeProduct(new Product("NewName", "New description to be changed", Float.parseFloat("7f"), productTwo.getUniqueId(), productOne.getSupplierId())));
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
