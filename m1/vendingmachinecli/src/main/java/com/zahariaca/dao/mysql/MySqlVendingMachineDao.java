package com.zahariaca.dao.mysql;

import com.zahariaca.dao.Dao;
import com.zahariaca.pojo.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

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
        Set<Product> products = new TreeSet<>();
        try(Connection conn = MySqlDaoFactory.createConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM products");

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Product p = new Product(
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getFloat(4),
                        String.valueOf(resultSet.getInt(5)));
                products.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products.isEmpty() ? Collections.emptySet() : products;
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
