package com.zahariaca.dao.mysql;

import com.zahariaca.dao.Dao;
import com.zahariaca.dao.DaoFactory;
import com.zahariaca.pojo.Product;
import com.zahariaca.pojo.ProductTransaction;
import com.zahariaca.pojo.users.User;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 18.12.2018
 */
public class MySqlDaoFactory implements DaoFactory {
    private static final Logger logger = LogManager.getLogger(MySqlDaoFactory.class);
    private static final String DB_PARAMS = "?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false";
    private static final String DBURL = "jdbc:mysql://localhost:3306/vendingmachine_m2" + DB_PARAMS;
    public static final String USERNAME = "vm_admin";
    public static final String PASSWORD = "password";

    public static Connection createConnection() throws SQLException {
        Properties config = new Properties();
        try {
            config.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));

            String url = config.getProperty("url");
            String username = config.getProperty("username");
            String password = config.getProperty("password");

            // TODO: connection pool
            // TODO: get rid of hardcoded url/user/pass
            return DriverManager.getConnection(
                    url + DB_PARAMS,
                    username,
                    password);
        } catch (IOException e) {
            logger.log(Level.ERROR, "Config file could not be loaded. {}, using fallback url: {}, username: {}, password: {}", e.getMessage(), DBURL, USERNAME, PASSWORD);
        }

        return DriverManager.getConnection(
                DBURL,
                USERNAME,
                PASSWORD);
    }

    @Override
    public Dao<User, String> getUserDao() {
        return new MySqlUserDao();
    }

    @Override
    public Dao<Product, Integer> getVendingMachineDao() {
        return new MySqlVendingMachineDao();
    }

    @Override
    public Dao<ProductTransaction, String> getTransactionsDao() {
        return new MySqlTransactionsDao();
    }
}
