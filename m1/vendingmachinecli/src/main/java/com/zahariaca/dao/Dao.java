package com.zahariaca.dao;

import java.util.Optional;
import java.util.Set;

/**
 * @param <T> is the pojo type that is used by dao
 * @param <K> is the expected parameter of the dao
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 18.11.2018
 */
public interface Dao<T, K> {
    Optional<T> get(K id);

    Set<T> getAll();

    void save(T t);

    void update(T t, String[] params);

    void delete(T t);
}
