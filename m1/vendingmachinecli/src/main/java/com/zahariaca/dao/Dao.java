package com.zahariaca.dao;

import com.zahariaca.exceptions.NoSuchProductException;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 03.11.2018
 */
public interface Dao<T, K> {
    void addProduct(T product);

    void displayProducts();

    void deleteProduct(K productName, K uniqueId) throws NoSuchProductException;

    void changeProduct(K productName, K productDescription, K productPrice, K uniqueId);

    T buyProduct(K productName) throws NoSuchProductException;
}
