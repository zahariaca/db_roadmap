package com.zahariaca.loader;

import com.zahariaca.pojo.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class FileLoaderTest {
    private Product productOne;
    private Product productTwo;

    @BeforeEach
    public void init() {
        productOne = new Product("Soda", "Sugary refreshing beverage", 5.6f);
        productTwo = new Product("Chips", "Salty pack of thin potatoes", 8f);
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

        Set<Product> testSet = FileLoader.INSTANCE.loadFromFile(file);
        assertTrue(!testSet.isEmpty());
        assertTrue(testSet.size() == 2);

        Optional<Product> optionalOne =testSet.stream().filter(product -> product.getUniqueId().equals(productOne.getUniqueId())).findFirst();
        assertTrue(optionalOne.get().equals(productOne));

        Optional<Product> optionalTwo =testSet.stream().filter(product -> product.getUniqueId().equals(productTwo.getUniqueId())).findFirst();
        assertTrue(optionalTwo.get().equals(productTwo));
    }
}
