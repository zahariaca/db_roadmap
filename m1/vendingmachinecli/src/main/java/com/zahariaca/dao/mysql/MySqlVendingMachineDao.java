package com.zahariaca.dao.mysql;

import com.zahariaca.dao.Dao;
import com.zahariaca.pojo.Product;

import java.util.Optional;
import java.util.Set;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 18.12.2018
 */
public class MySqlVendingMachineDao implements Dao<Product, Integer> {

    @Override
    public Optional<Product> get(Integer id) {
        return Optional.empty();
    }

    @Override
    public Set<Product> getAll() {
        return null;
    }

    @Override
    public void save(Product product) {
        throw new UnsupportedOperationException("DB mode not implemented!");
    }

    @Override
    public void update(Product product, String[] params) {
        throw new UnsupportedOperationException("DB mode not implemented!");
    }

    @Override
    public void delete(Product product) {
        throw new UnsupportedOperationException("DB mode not implemented!");
    }
}
