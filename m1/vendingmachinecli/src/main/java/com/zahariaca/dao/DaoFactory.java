package com.zahariaca.dao;

import com.zahariaca.dao.file.FileDaoFactory;
import com.zahariaca.dao.mysql.MySqlDaoFactory;
import com.zahariaca.pojo.Product;
import com.zahariaca.pojo.users.User;

import java.util.Set;

/**
 * @param <T> is the VendingMachineDao type
 * @param <K> is the UserDao type
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 18.12.2018
 */
public interface DaoFactory<T, K, L> {

    static DaoFactory makeFileDaoFactory(Set<Product> loadedProducts, Set<User> loadedUsers) {
        return new FileDaoFactory(loadedProducts, loadedUsers);
    }

    static DaoFactory makeMySqlDaoFactory() {
        return new MySqlDaoFactory();
    }

    static DaoFactory makeOracleDaoFactory() {
        throw new UnsupportedOperationException("Oracle DB not supported");
    }

    T getVendingMachineDao();

    K getUserDao();

    L getTransactionsDao();
}
