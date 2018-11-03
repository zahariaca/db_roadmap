package com.zahariaca.vendingmachine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zahariaca.Main;
import com.zahariaca.dao.Dao;
import com.zahariaca.pojo.Product;
import com.zahariaca.pojo.ProductTransaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.Set;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class VendingMachineDao implements Dao<Product, String > {
    Set<Product> products;

    //TODO: When should the write be done? On shutdown? Or sequentially with each operation? Maybe on shutdown for simplicity
    public VendingMachineDao(Set<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {

    }

    public void displayProducts() {
        products.stream().forEach(p -> System.out.println(p.toString()));
    }

    public void deleteProduct(Product product) {

    }

    public void changeProduct(Product product, String uniqueId) {

    }

    public Product buyProduct(String productName) {
        Optional<Product> product = products.stream().filter(p -> p.getName().equalsIgnoreCase(productName)).findAny();
        return product.orElse(null);
    }



    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }
}
