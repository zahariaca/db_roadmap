package com.zahariaca.vendingmachine;

import com.zahariaca.pojo.Product;

import java.util.Set;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class VendingMachine {
    Set<Product> products;

    public VendingMachine(Set<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {

    }

    public void displayProducts() {

    }

    public void deleteProduct(Product product) {

    }

    public void changeProduct(Product product, String uniqueId) {

    }

    public void buyProduct(Product product) {

    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }
}
