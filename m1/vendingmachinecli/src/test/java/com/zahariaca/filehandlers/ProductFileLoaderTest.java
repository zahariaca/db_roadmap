package com.zahariaca.filehandlers;

import com.zahariaca.pojo.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
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
        String supplierOneUUID = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918";
        String supplierTwoUUID = "b1f2aebc61a4ee3ed0c429fe44c259612c2d857abcca0b632530fe70f0950b05";
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

        Set<Product> testSet = ProductFileLoader.INSTANCE.loadProductsFromFile(file);
        assertTrue(!testSet.isEmpty());
        assertTrue(testSet.size() == 2);

        Optional<Product> optionalOne = testSet.stream().filter(product -> product.getUniqueId() == productOne.getUniqueId()).findAny();
        assertTrue(optionalOne.get().equals(productOne));

        Optional<Product> optionalTwo = testSet.stream().filter(product -> product.getUniqueId() == productTwo.getUniqueId()).findAny();
        assertTrue(optionalTwo.get().equals(productTwo));
    }
}
