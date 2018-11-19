package com.zahariaca.dao;

import com.zahariaca.pojo.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 19.11.2018
 */
public class VendingMachineDaoTest {
    public static final String UPDATED_NAME = "UpdatedName";
    public static final String UPDATED_DESCRIPTION = "UpdatedDescription";
    public static final String PRICE = "6.67";
    private Set<Product> products;
    private Product productOne;
    private Product productTwo;
    private Set<Product> productSet;
    private VendingMachineDao vendingMachineDao;
    private String supplierTwoUUID = "b1f2aebc61a4ee3ed0c429fe44c259612c2d857abcca0b632530fe70f0950b05";

    @BeforeEach
    void init() {
        Product.setIdGenerator(new AtomicInteger(1000));
        String supplierOneUUID = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918";
        productOne = new Product("Soda", "Sugary refreshing beverage", 5.6f, supplierOneUUID);
        productTwo = new Product("Chips", "Salty pack of thin potatoes", 8f, supplierTwoUUID);
        productSet = new TreeSet<>();
        productSet.add(productOne);
        productSet.add(productTwo);
        vendingMachineDao = new VendingMachineDao(productSet);
    }

    @Test
    void testGetById() {
        assertEquals(productOne, vendingMachineDao.get(productOne.getUniqueId()).get());
    }

    @Test
    void testGetByIdReturnEmptyOptional() {
        assertEquals(Optional.empty(), vendingMachineDao.get(55555));
    }

    @Test
    void testGetAll() {
        assertEquals(productSet, vendingMachineDao.getAll());
    }

    @Test
    void testGetAllReturnEmptySetWhenInitiatedIncorrectly() {
        VendingMachineDao emptyVendingMachine = new VendingMachineDao(null);
        assertEquals(Collections.emptySet(), emptyVendingMachine.getAll());
    }

    @Test
    void testSave() {
        Product newProduct = new Product("NewProduct", "ProductDescription", 6.67f, supplierTwoUUID);
        vendingMachineDao.save(newProduct);
        assertTrue(vendingMachineDao.getAll().contains(newProduct));
    }

    @Test
    void testDelete() {
        vendingMachineDao.delete(productOne);
        assertFalse(vendingMachineDao.getAll().contains(productOne));
    }


    @Test
    void testUpdate() {
        vendingMachineDao.update(productTwo, new String[]{
                UPDATED_NAME,
                UPDATED_DESCRIPTION,
                PRICE,
                String.valueOf(productTwo.getUniqueId()),
                supplierTwoUUID});

        Product updateProduct = vendingMachineDao.get(productTwo.getUniqueId()).get();

        assertNotEquals(productTwo, updateProduct);
        assertTrue(updateProduct.getName().equals(UPDATED_NAME));
        assertTrue(updateProduct.getDescription().equals(UPDATED_DESCRIPTION));
        assertTrue(updateProduct.getPrice() == (Float.parseFloat(PRICE)));
    }

    @Test
    void testUpdateInheritsNameWhenEmptyStringProvided() {
        vendingMachineDao.update(productTwo, new String[]{
                null,
                UPDATED_DESCRIPTION,
                PRICE,
                String.valueOf(productTwo.getUniqueId()),
                supplierTwoUUID});

        Product updateProduct = vendingMachineDao.get(productTwo.getUniqueId()).get();

        assertNotEquals(productTwo, updateProduct);
        assertTrue(updateProduct.getName().equals(productTwo.getName()));
        assertTrue(updateProduct.getDescription().equals(UPDATED_DESCRIPTION));
        assertTrue(updateProduct.getPrice() == (Float.parseFloat(PRICE)));
    }

    @Test
    void testUpdateInheritsDescriptionWhenEmptyStringProvided() {
        vendingMachineDao.update(productTwo, new String[]{
                UPDATED_NAME,
                null,
                PRICE,
                String.valueOf(productTwo.getUniqueId()),
                supplierTwoUUID});

        Product updateProduct = vendingMachineDao.get(productTwo.getUniqueId()).get();

        assertNotEquals(productTwo, updateProduct);
        assertTrue(updateProduct.getName().equals(UPDATED_NAME));
        assertTrue(updateProduct.getDescription().equals(productTwo.getDescription()));
        assertTrue(updateProduct.getPrice() == (Float.parseFloat(PRICE)));
    }

    @Test
    void testUpdateInheritsPriceWhenEmptyStringProvided() {
        vendingMachineDao.update(productTwo, new String[]{
                UPDATED_NAME,
                UPDATED_DESCRIPTION,
                null,
                String.valueOf(productTwo.getUniqueId()),
                supplierTwoUUID});

        Product updateProduct = vendingMachineDao.get(productTwo.getUniqueId()).get();

        assertNotEquals(productTwo, updateProduct);
        assertTrue(updateProduct.getName().equals(UPDATED_NAME));
        assertTrue(updateProduct.getDescription().equals(UPDATED_DESCRIPTION));
        assertTrue(updateProduct.getPrice() == productTwo.getPrice());
    }

    @Test
    void testUpdateThrowsNullPointerIfNoProductIdProvided() {
        assertThrows(NullPointerException.class,
                () -> vendingMachineDao.update(productTwo, new String[]{
                        UPDATED_NAME,
                        UPDATED_DESCRIPTION,
                        PRICE,
                        null,
                        supplierTwoUUID}));
    }

    @Test
    void testUpdateThrowsNullPointerIfNoSupplierIdProvided() {
        assertThrows(NullPointerException.class,
                () -> vendingMachineDao.update(productTwo, new String[]{
                        UPDATED_NAME,
                        UPDATED_DESCRIPTION,
                        PRICE,
                        String.valueOf(productTwo.getUniqueId()),
                        null}));
    }
}
