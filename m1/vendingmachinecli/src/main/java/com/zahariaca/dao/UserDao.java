package com.zahariaca.dao;

import com.zahariaca.users.Supplier;

import java.util.Optional;
import java.util.Set;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 18.11.2018
 */
public class UserDao implements Dao<Supplier, String> {
    private Set<Supplier> suppliers;

    public UserDao(Set<Supplier> suppliers) {
        this.suppliers = suppliers;
    }

    @Override
    public Optional<Supplier> get(String id) {
        return suppliers.stream().filter(u -> u.getUsername().equals(id)).findAny();
    }

    @Override
    public Set<Supplier> getAll() {
        return suppliers;
    }

    @Override
    public void save(Supplier user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Supplier user, String[] params) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Supplier user) {
        throw new UnsupportedOperationException();
    }
}
