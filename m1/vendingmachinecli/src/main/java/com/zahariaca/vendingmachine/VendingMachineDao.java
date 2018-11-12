package com.zahariaca.vendingmachine;

import com.zahariaca.dao.Dao;
import com.zahariaca.exceptions.NoSuchProductException;
import com.zahariaca.exceptions.ProductAlreadyExistsException;
import com.zahariaca.pojo.Product;

import java.util.Optional;
import java.util.Set;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class VendingMachineDao implements Dao<Product, String> {
    private Set<Product> productsSet;

    //TODO: When should the write be done? On shutdown? Or sequentially with each operation? Maybe on shutdown for simplicity in m1
    public VendingMachineDao(Set<Product> products) {
        this.productsSet = products;
    }

    @Override
    public void addProduct(Product product) throws ProductAlreadyExistsException {
        if (!productsSet.stream().anyMatch(product::equals)) {
            productsSet.add(product);
        } else {
            throw new ProductAlreadyExistsException(String.format("The product: %n%s%n already exists! Cannot add!", product));
        }
    }

    @Override
    public void displayProducts() {
        productsSet.stream().forEach(p -> System.out.println(p.toString()));
    }

    @Override
    public void deleteProduct(String productName, String uniqueId) throws NoSuchProductException {
        Optional<Product> product = productsSet.stream().filter(p -> productName.equals(p.getName()) && uniqueId.equals(p.getUniqueId())).findAny();
        if (product.isPresent()) {
            productsSet.remove(product.get());
        } else {
            throw new NoSuchProductException(String.format("Product: %s with uniqueID: %s, was not found. Could not be deleted!", productName, uniqueId));
        }
    }

    @Override
    public void changeProduct(String productName, String productDescription, String productPrice, String uniqueId) {

    }

    @Override
    public Product buyProduct(String productName) throws NoSuchProductException {
        Optional<Product> product = productsSet.stream().filter(p -> p.getName().equalsIgnoreCase(productName)).findAny();
        if (product.isPresent()) {
            return product.get();
        }
        throw new NoSuchProductException(String.format("Product: %s does not exist.", productName));
    }


    public Set<Product> getProductsSet() {
        return productsSet;
    }
}
