package com.zahariaca.dao.mysql;

import com.zahariaca.dao.Dao;
import com.zahariaca.pojo.Product;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
//TODO: Optimise duplicate code.
public class MySqlVendingMachineDao implements Dao<Product, Integer> {
    private static final Logger logger = LogManager.getLogger(MySqlVendingMachineDao.class);

    @Override
    public Optional<Product> get(Integer id) {
        try (Connection conn = MySqlDaoFactory.createConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM products WHERE product_id = ?");
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getFloat(4),
                        resultSet.getInt(5));
                return Optional.of(product);
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, ">E: method get(): {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Set<Product> getAll() {
        Set<Product> products = new TreeSet<>();
        try (Connection conn = MySqlDaoFactory.createConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM products");

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getFloat(4),
                        resultSet.getInt(5));
                products.add(product);
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, ">E: method getAll(): {}", e.getMessage());
        }
        return products.isEmpty() ? Collections.emptySet() : products;
    }

    @Override
    public Set<Product> getAll(Integer id) {
        Set<Product> products = new TreeSet<>();
        try (Connection conn = MySqlDaoFactory.createConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM products WHERE supplier_id = ?");
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getFloat(4),
                        resultSet.getInt(5));
                products.add(product);
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, ">E: method getAll(): {}", e.getMessage());
        }
        return products.isEmpty() ? Collections.emptySet() : products;
    }

    @Override
    public void save(Product product) {
        try (Connection conn = MySqlDaoFactory.createConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO products VALUES (NULL, ?,?,?,?)");
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getDescription());
            preparedStatement.setFloat(3, product.getPrice());
            preparedStatement.setInt(4, product.getSupplierId());

            preparedStatement.execute();
        } catch (SQLException e) {
            logger.log(Level.ERROR, ">E: method save(...): {}", e.getMessage());
        }
    }

    @Override
    public void update(Product product, String[] params) {
        String[] validatedParams = validateUpdateParams(product, params);

        try (Connection conn = MySqlDaoFactory.createConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("UPDATE products SET name = ?, description = ?, price = ? WHERE product_id = ? AND supplier_id = ?");
            preparedStatement.setString(1, validatedParams[1]);
            preparedStatement.setString(2, validatedParams[2]);
            preparedStatement.setFloat(3, Float.valueOf(validatedParams[3]));
            preparedStatement.setInt(4, Integer.valueOf(validatedParams[0]));
            preparedStatement.setInt(5, Integer.valueOf(validatedParams[4]));

            preparedStatement.execute();
        } catch (SQLException e) {
            logger.log(Level.ERROR, ">E: method update(...): {}", e.getMessage());
        }
    }

    @Override
    public void delete(Product product) {
        try (Connection conn = MySqlDaoFactory.createConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM products WHERE product_id = ? AND supplier_id = ?");
            preparedStatement.setInt(1, product.getUniqueId());
            preparedStatement.setInt(2, product.getSupplierId());

            preparedStatement.execute();
        } catch (SQLException e) {
            logger.log(Level.ERROR, ">E: method delete(...): {}", e.getMessage());
        }
    }
}
