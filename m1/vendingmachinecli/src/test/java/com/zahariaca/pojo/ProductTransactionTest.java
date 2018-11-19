package com.zahariaca.pojo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 19.11.2018
 */
public class ProductTransactionTest {
    private int productId;
    private float price;
    private Date date;

    @BeforeEach
    void beforeAll() {
        productId = 1002;
        price = 5.66f;
        date = Date.from(Instant.now());
    }

    @Test
    void testCorrectInstantiation() {


        ProductTransaction productTransaction = new ProductTransaction(
                productId,
                price,
                date.getTime());

        assertEquals(productTransaction.getProductUniqueId(), productId);
        assertEquals(productTransaction.getPrice(), price);
        assertEquals(productTransaction.getDate(), date.getTime());
    }

    @Test
    void testCorrectSerialization() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        ProductTransaction productTransaction = new ProductTransaction(
                productId,
                price,
                date.getTime());

        String data = gson.toJson(productTransaction);

        ProductTransaction deserializedProductTransaction = gson.fromJson(data, new TypeToken<ProductTransaction>() {
        }.getType());

        assertEquals(deserializedProductTransaction.getProductUniqueId(), productId);
        assertEquals(deserializedProductTransaction.getPrice(), price);
        assertEquals(deserializedProductTransaction.getDate(), date.getTime());
        assertEquals(productTransaction, deserializedProductTransaction);
    }
}
