package com.zahariaca.loader;

import com.zahariaca.pojo.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
class ProductFileLoaderTest {
    private Product productOne;
    private Product productTwo;

    @BeforeEach
    void init() {
        Product.setIdGenerator(new AtomicInteger(1000));
        UUID supplierOneUUID = UUID.fromString("a3af93f2-0fff-42e0-b84c-6e507ece0264");
        UUID supplierTwoUUID = UUID.fromString("ac7ed436-14ee-47f2-8005-72e7674b8be3");
        productOne = new Product("Soda", "Sugary refreshing beverage", 5.6f, supplierOneUUID);
        productTwo = new Product("Chips", "Salty pack of thin potatoes", 8f, supplierTwoUUID);
    }

    @Test
    void testCorrectLoading() {
        String fileUrl = "src\\test\\resources\\test-products.json";
        File file = null;
        if (fileUrl != null) {
            file = new File(fileUrl);
        } else {
            fail("File not found.");
        }

        Set<Product> testSet = ProductFileLoader.INSTANCE.loadFromFile(file);
        assertTrue(!testSet.isEmpty());
        assertTrue(testSet.size() == 2);

        Optional<Product> optionalOne = testSet.stream().filter(product -> product.getUniqueId() == productOne.getUniqueId()).findAny();
        assertTrue(optionalOne.get().equals(productOne));

        Optional<Product> optionalTwo = testSet.stream().filter(product -> product.getUniqueId() == productTwo.getUniqueId()).findAny();
        assertTrue(optionalTwo.get().equals(productTwo));
    }
}
