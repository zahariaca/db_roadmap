package com.zahariaca.dao.mysql;

import com.zahariaca.dao.Dao;
import com.zahariaca.pojo.users.User;

import java.util.Optional;
import java.util.Set;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 18.12.2018
 */
public class MySqlUserDao implements Dao<User, String> {

    @Override
    public Optional<User> get(String id) {
        return Optional.empty();
    }

    @Override
    public Set<User> getAll() {
        return null;
    }

    @Override
    public void save(User user) {
        throw new UnsupportedOperationException("DB mode not implemented!");
    }

    @Override
    public void update(User user, String[] params) {
        throw new UnsupportedOperationException("DB mode not implemented!");
    }

    @Override
    public void delete(User user) {
        throw new UnsupportedOperationException("DB mode not implemented!");
    }
}
