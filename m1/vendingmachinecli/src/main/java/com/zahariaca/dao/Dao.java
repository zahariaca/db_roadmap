package com.zahariaca.dao;

import com.zahariaca.exceptions.NoSuchProductException;
import com.zahariaca.exceptions.ProductAlreadyExistsException;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 03.11.2018
 */
public interface Dao<T, K> {
    void addProduct(T product) throws ProductAlreadyExistsException;

    void displayProducts();

    void deleteProduct(K productName, K uniqueId) throws NoSuchProductException;

    void changeProduct(T product) throws NoSuchProductException, ProductAlreadyExistsException;

    T buyProduct(K productName) throws NoSuchProductException;
}
