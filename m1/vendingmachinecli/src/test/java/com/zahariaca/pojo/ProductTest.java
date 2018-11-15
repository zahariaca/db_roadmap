package com.zahariaca.pojo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
class ProductTest {
    private Product productOne;
    private Product productOneCopy;
    private Product productTwo;

    @BeforeEach
    void init() {
        Product.setIdGenerator(new AtomicInteger(1000));
        UUID supplierOneUUID = UUID.fromString("a3af93f2-0fff-42e0-b84c-6e507ece0264");
        UUID supplierTwoUUID = UUID.fromString("ac7ed436-14ee-47f2-8005-72e7674b8be3");
        productOne = new Product("Soda", "Sugary refreshing beverage", 5.6f, supplierOneUUID);
        productOneCopy = new Product("Soda", "Sugary refreshing beverage", 5.6f, productOne.getUniqueId(), supplierOneUUID);
        productTwo = new Product("Chips", "Salty pack of thin potatoes", 8f, supplierTwoUUID);
    }


    @Test
    void testEquality() {
        assertEquals(productOne, productOneCopy);
        assertEquals(productOne.hashCode(), productOneCopy.hashCode());
    }

    @Test
    void testNotEqual() {
        assertNotEquals(productOne, productTwo);
        assertFalse(productOne.hashCode() == productTwo.hashCode());
        assertFalse(productOne.getUniqueId() == productTwo.getUniqueId());
    }
}
