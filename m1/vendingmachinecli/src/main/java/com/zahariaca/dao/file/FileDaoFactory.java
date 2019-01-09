package com.zahariaca.dao.file;

import com.zahariaca.dao.Dao;
import com.zahariaca.dao.DaoFactory;
import com.zahariaca.pojo.Product;
import com.zahariaca.pojo.users.User;

import java.util.Set;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 18.12.2018
 */
public class FileDaoFactory implements DaoFactory {
    private final Set<Product> loadedProducts;
    private final Set<User> loadedUsers;

    public FileDaoFactory(Set<Product> loadedProducts, Set<User> loadedUsers) {
        this.loadedProducts = loadedProducts;
        this.loadedUsers = loadedUsers;
    }

    @Override
    public Dao<User, String> getUserDao() {
        return new FileUserDao(loadedUsers);
    }

    @Override
    public Dao<Product, Integer> getVendingMachineDao() {
        return new FileVendingMachineDao(loadedProducts);
    }

    @Override
    public Dao getTransactionsDao() {
        return null;
    }

}
