package com.zahariaca.dao;

import com.zahariaca.pojo.Product;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @param <T> is the pojo type that is used by dao
 * @param <K> is the expected parameter of the dao
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 18.11.2018
 */
public interface Dao<T, K> {
    Optional<T> get(K id);

    Set<T> getAll();

    Set<T> getAll(K id);

    void save(T t);

    void update(T t, String[] params);

    void delete(T t);

    default String[] validateUpdateParams(Product product, String[] params) {
        String productName = Objects.requireNonNullElse(
                params[0],
                product.getName());
        String productDescription = Objects.requireNonNullElse(
                params[1],
                product.getDescription());
        String productPrice = Objects.requireNonNullElse(
                params[2],
                String.valueOf(product.getPrice()));
        String productId = Objects.requireNonNull(
                params[3],
                "Product Unique ID cannot be null");
        String supplierID = Objects.requireNonNull(
                params[4],
                "SupplierCli Id cannot be null");
        return new String[]{productId, productName, productDescription, productPrice, supplierID};
    }
}
