package com.zahariaca.dao;

import com.zahariaca.pojo.Product;

import java.util.Set;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 03.11.2018
 */
public interface Dao<T, K> {
    void addProduct(T product);
    void displayProducts();
    void deleteProduct(T product);
    void changeProduct(T product, K uniqueId);
    T buyProduct(K productName);
}