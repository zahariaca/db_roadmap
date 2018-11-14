package com.zahariaca.loader;

import com.zahariaca.pojo.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class ProductFileLoaderTest {
    private Product productOne;
    private Product productTwo;

    @BeforeEach
    public void init() {
        UUID productOneUUID = UUID.fromString("8f70c754-4257-43d1-91e8-9438838d23cd");
        UUID productTwoUUID = UUID.fromString("1a24f45c-bde1-4242-8545-22c879fdf8b8");
        UUID supplierOneUUID = UUID.fromString("a3af93f2-0fff-42e0-b84c-6e507ece0264");
        UUID supplierTwoUUID = UUID.fromString("ac7ed436-14ee-47f2-8005-72e7674b8be3");
        productOne = new Product("Soda", "Sugary refreshing beverage", 5.6f, productOneUUID, supplierOneUUID);
        productTwo = new Product("Chips", "Salty pack of thin potatoes", 8f, productTwoUUID, supplierTwoUUID);
    }

    @Test
    public void testCorrectLoading() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL fileUrl = classLoader.getResource("test-products.json");
        File file = null;
        if (fileUrl != null) {
            file = new File(fileUrl.getFile());
        } else {
            fail("File not found.");
        }

        Set<Product> testSet = ProductFileLoader.INSTANCE.loadFromFile(file);
        assertTrue(!testSet.isEmpty());
        assertTrue(testSet.size() == 2);

        Optional<Product> optionalOne = testSet.stream().filter(product -> product.getUniqueId().equals(productOne.getUniqueId())).findFirst();
        assertTrue(optionalOne.get().equals(productOne));

        Optional<Product> optionalTwo = testSet.stream().filter(product -> product.getUniqueId().equals(productTwo.getUniqueId())).findFirst();
        assertTrue(optionalTwo.get().equals(productTwo));
    }
}
