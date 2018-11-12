package com.zahariaca.pojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    //TODO: REMOVE THIS TESTING AREA
    @Test
    public void testTest() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Set<Product> testSet = new HashSet<>();
        testSet.add(productOne);
        testSet.add(productTwo);
        String data = gson.toJson(testSet);
        System.out.println(data);

        Product[] testDeserialize = gson.fromJson(data, Product[].class);

        //De-serialization
//get the type of the collection.


        Set<Product> deserializedSet = Stream.of(testDeserialize).collect(Collectors.toSet());

        Stream.of(testDeserialize).forEach(System.out::println);


        System.out.println("-----------------------");

        Type hashSetType = new TypeToken<HashSet<Product>>(){}.getType();
        Set<Product> deserializedSetWithToken = gson.fromJson(data, hashSetType);
        System.out.println(deserializedSetWithToken.getClass());
        deserializedSet.stream().forEach(System.out::println);

    }
}
