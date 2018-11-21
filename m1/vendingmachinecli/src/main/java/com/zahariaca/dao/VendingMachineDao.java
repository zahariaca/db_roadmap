package com.zahariaca.dao;

import com.zahariaca.pojo.Product;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 18.11.2018
 */
public class VendingMachineDao implements Dao<Product, Integer> {
    private Set<Product> products;

    public VendingMachineDao(Set<Product> products) {
        if (products != null) {
            this.products = products;
        } else {
            this.products = Collections.emptySet();
        }
    }

    @Override
    public Optional<Product> get(Integer id) {
        return products.stream().filter(p -> p.getUniqueId() == id).findAny();
    }

    @Override
    public Set<Product> getAll() {
        return products;
    }

    @Override
    public void save(Product t) {
        products.add(t);
    }

    @Override
    public void update(Product t, String[] params) {
        delete(t);

        String productName = Objects.requireNonNullElse(
                params[0],
                t.getName());
        String productDescription = Objects.requireNonNullElse(
                params[1],
                t.getDescription());
        String productPrice = Objects.requireNonNullElse(
                params[2],
                String.valueOf(t.getPrice()));
        String productId = Objects.requireNonNull(
                params[3],
                "Product Unique ID cannot be null");
        String supplierID = Objects.requireNonNull(
                params[4],
                "SupplierCli Id cannot be null");

        save(new Product(productName, productDescription, Float.valueOf(productPrice), Integer.valueOf(productId), supplierID));
    }

    @Override
    public void delete(Product t) {
        products.remove(t);
    }
}
