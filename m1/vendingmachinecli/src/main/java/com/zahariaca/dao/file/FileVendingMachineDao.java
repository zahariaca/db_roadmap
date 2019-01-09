package com.zahariaca.dao.file;

import com.zahariaca.dao.Dao;
import com.zahariaca.pojo.Product;
import com.zahariaca.pojo.users.User;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 18.11.2018
 */
public class FileVendingMachineDao implements Dao<Product, Integer> {
    private Set<Product> products;

    public FileVendingMachineDao(Set<Product> products) {
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
    public Set<Product> getAll(Integer id) {
        return products.stream().filter(product -> product.getSupplierId() == id).collect(Collectors.toSet());
    }

    @Override
    public void save(Product product) {
        products.add(product);
    }

    @Override
    public void update(Product product, String[] params) {
        String[] validatedParams = validateUpdateParams(product, params);

        delete(product);

        save(new Product(Integer.valueOf(validatedParams[0]), validatedParams[1], validatedParams[2], Float.valueOf(validatedParams[3]), Integer.valueOf(validatedParams[4])));
    }

    @Override
    public void delete(Product product) {
        products.remove(product);
    }
}
