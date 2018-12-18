package com.zahariaca.dao.mysql;

import com.zahariaca.dao.Dao;
import com.zahariaca.dao.DaoFactory;
import com.zahariaca.pojo.Product;
import com.zahariaca.pojo.users.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 18.12.2018
 */
public class MySqlDaoFactory implements DaoFactory {
    private static final String DBURL= "jdbc:mysql://localhost:3306/cli_login?autoReconnect=true&useSSL=false";

    public static Connection createConnection() throws SQLException {
        // TODO: connection pool
        // TODO: get rid of hardcoded url/user/pass
        return DriverManager.getConnection(
                DBURL,
                "root",
                "Mon.2017");
    }

    @Override
    public Dao<User, String> getUserDao() {
        return new MySqlUserDao();
    }

    @Override
    public Dao<Product, Integer> getVendingMachineDao() {
        return new MySqlVendingMachineDao();
    }
}
