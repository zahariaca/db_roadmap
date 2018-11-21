package com.zahariaca.pojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        String supplierOneUUID = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918";
        String supplierTwoUUID = "b1f2aebc61a4ee3ed0c429fe44c259612c2d857abcca0b632530fe70f0950b05";
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

    @Test
    void testCorrectSerialization() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        String data = gson.toJson(productOne);
        System.out.println(data);

        Product deserializedProduct = gson.fromJson(data, new TypeToken<Product>() {
        }.getType());

        assertEquals(productOne, deserializedProduct);
        assertEquals(productOne.hashCode(), deserializedProduct.hashCode());
    }
}
