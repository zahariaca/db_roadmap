package com.zahariaca.dao;

import com.zahariaca.users.AbstractUser;
import com.zahariaca.users.Supplier;

import java.util.Optional;
import java.util.Set;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 18.11.2018
 */
public class UserDao implements Dao<Supplier, String> {
    private Set<Supplier> abstractUsers;

    public UserDao(Set<Supplier> abstractUsers) {
        this.abstractUsers = abstractUsers;
    }

    @Override
    public Optional<Supplier> get(String id) {
        return abstractUsers.stream().filter(u -> u.getUsername().equals(id)).findAny();
    }

    @Override
    public Set<Supplier> getAll() {
        return abstractUsers;
    }

    @Override
    public void save(Supplier abstractUser) {
        throw  new UnsupportedOperationException();
    }

    @Override
    public void update(Supplier abstractUser, String[] params) {
        throw  new UnsupportedOperationException();
    }

    @Override
    public void delete(Supplier abstractUser) {
        throw  new UnsupportedOperationException();
    }
}
