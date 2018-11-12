package com.zahariaca.pojo;

import java.time.Instant;
import java.util.Date;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 03.11.2018
 */
public class ProductTransaction {
    private final String productUniqueId;
    private final float price;
    private final Date date;

    public ProductTransaction(String productUniqueId, float price) {
        this.productUniqueId = productUniqueId;
        this.price = price;
        date = Date.from(Instant.now());
    }
}
