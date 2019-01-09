package com.zahariaca.dao.mysql;

import com.zahariaca.dao.Dao;
import com.zahariaca.pojo.Product;
import com.zahariaca.pojo.users.User;
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
public class MySqlUserDao implements Dao<User, String> {
    private static final Logger logger = LogManager.getLogger(MySqlUserDao.class);

    @Override
    public Optional<User> get(String username) {
        try (Connection conn = MySqlDaoFactory.createConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getInt(4) != 0);

                return Optional.of(user);
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, ">E: method get(): ", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Set<User> getAll() {
        Set<User> products = new TreeSet<>();
        try (Connection conn = MySqlDaoFactory.createConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM users");

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getInt(4) != 0);

                products.add(user);
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, ">E: method getAll(): ", e.getMessage());
        }
        return products.isEmpty() ? Collections.emptySet() : products;
    }

    @Override
    public Set<User> getAll(String username) {
        Set<User> products = new TreeSet<>();
        try (Connection conn = MySqlDaoFactory.createConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getInt(4) != 0);

                products.add(user);
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, ">E: method getAll(): ", e.getMessage());
        }
        return products.isEmpty() ? Collections.emptySet() : products;
    }

    @Override
    public void save(User user) {
        throw new UnsupportedOperationException("User saving not implemented!");
    }

    @Override
    public void update(User user, String[] params) {
        throw new UnsupportedOperationException("User updating not implemented!");
    }

    @Override
    public void delete(User user) {
        throw new UnsupportedOperationException("User deleting not implemented!");
    }
}
