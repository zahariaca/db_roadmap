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
    private static final String DBURL= "jdbc:mysql://localhost:3306/vendingmachine_m2?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false";

    public static Connection createConnection() throws SQLException {
        // TODO: connection pool
        // TODO: get rid of hardcoded url/user/pass
        return DriverManager.getConnection(
                DBURL,
                "vm_admin",
                "password");
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
