package com.zahariaca.dao;

import com.zahariaca.pojo.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 19.11.2018
 */
public class UserDaoTest {
    private Set<User> supplierClis;
    private Dao<User, String> userDao;
    private User supplier;


    @BeforeEach
    void init() {
        supplierClis = new TreeSet<>();
        supplier = new User("admin", "admin", true);
        supplierClis.add(supplier);
        supplierClis.add(new User("tester", "tester", false));
        userDao = new UserDao(supplierClis);
    }

    @Test
    void testGetById() {
        assertEquals(supplier, userDao.get(supplier.getUsername()).get());
    }

    @Test
    void testGetByIdReturnEmptyOptional() {
        assertEquals(Optional.empty(), userDao.get("wrong"));
    }

    @Test
    void testGetAll() {
        assertEquals(supplierClis, userDao.getAll());
    }

    @Test
    void testSaveThrowsException() {
        assertThrows(UnsupportedOperationException.class,
                () -> userDao.save(new User("new", "pass", true)));
    }

    @Test
    void testUpdateThrowsException() {
        assertThrows(UnsupportedOperationException.class,
                () -> userDao.update(
                        new User("new", "pass", true),
                        new String[]{}));
    }

    @Test
    void testDeleteThrowsException() {
        assertThrows(UnsupportedOperationException.class,
                () -> userDao.delete(new User("new", "pass", true)));
    }
}
