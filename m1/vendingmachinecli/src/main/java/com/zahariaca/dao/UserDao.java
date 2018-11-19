package com.zahariaca.dao;

import com.zahariaca.cli.SupplierCli;
import com.zahariaca.users.User;

import java.util.Optional;
import java.util.Set;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 18.11.2018
 */
public class UserDao implements Dao<User, String> {
    private Set<User> users;

    public UserDao(Set<User> users) {
        this.users = users;
    }

    @Override
    public Optional<User> get(String id) {
        return users.stream().filter(u -> u.getUsername().equals(id)).findAny();
    }

    @Override
    public Set<User> getAll() {
        return users;
    }

    @Override
    public void save(User user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(User user, String[] params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(User user) {
        throw new UnsupportedOperationException();
    }
}
