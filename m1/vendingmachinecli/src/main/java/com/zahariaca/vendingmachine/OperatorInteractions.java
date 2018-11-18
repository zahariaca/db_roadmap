package com.zahariaca.vendingmachine;

import com.zahariaca.exceptions.IllegalProductOperation;
import com.zahariaca.exceptions.NoSuchProductException;
import com.zahariaca.exceptions.ProductAlreadyExistsException;

import java.util.Collections;
import java.util.Set;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 03.11.2018
 */
public interface OperatorInteractions<T, K> {
    void addProduct(T product) throws ProductAlreadyExistsException;

    void displayProducts();

    void displayProducts(K uniqueId);

    void deleteProduct(K uniqueId) throws NoSuchProductException, IllegalProductOperation;

    void changeProduct(K product) throws NoSuchProductException, ProductAlreadyExistsException;

    T buyProduct(K productName) throws NoSuchProductException;

    default Set<T> getProductsSet() {
        return Collections.emptySet();
    }
}