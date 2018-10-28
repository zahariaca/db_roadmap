package com.zahariaca.pojo;

import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class ProductTest {
    private Product productOne;
    private Product productOneCopy;
    private Product productTwo;

    @BeforeEach
    public void init() {
        productOne = new Product("Soda", "Sugary refreshing beverage", 5.6f);
        productOneCopy = new Product("Soda", "Sugary refreshing beverage", 5.6f);
        productTwo = new Product("Chips", "Salty pack of thin potatoes", 8f);
    }


    @Test
    public void testEquality() {
        assertTrue(productOne.equals(productOneCopy));
        assertTrue(productOne.hashCode() == productOneCopy.hashCode());
        assertTrue(productOne.getUniqueId().equals(productOneCopy.getUniqueId()));
    }

    @Test
    public void testNotEqual() {
        assertFalse(productOne.equals(productTwo));
        assertFalse(productOne.hashCode() == productTwo.hashCode());
        assertFalse(productOne.getUniqueId().equals(productTwo.getUniqueId()));
    }
}
