package com.zahariaca.dao;

import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import com.zahariaca.users.Supplier;
import com.zahariaca.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 19.11.2018
 */
public class UserDaoTest {
    private Set<Supplier> suppliers;
    private Dao<Supplier, String> userDao;
    private User<BlockingQueue<OperationsEvent<OperationType, String[]>>, BlockingQueue<OperationsEvent<ResultOperationType, String>>> supplier;


    @BeforeEach
    void init() {
        suppliers = new TreeSet<>();
        supplier = new Supplier("admin", "admin", true);
        suppliers.add((Supplier) supplier);
        suppliers.add(new Supplier("tester", "tester", false));
        userDao = new UserDao(suppliers);
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
        assertEquals(suppliers, userDao.getAll());
    }

    @Test
    void testSaveThrowsException() {
        assertThrows(UnsupportedOperationException.class,
                () -> userDao.save(new Supplier("new", "pass", true)));
    }

    @Test
    void testUpdateThrowsException() {
        assertThrows(UnsupportedOperationException.class,
                () -> userDao.update(
                        new Supplier("new", "pass", true),
                        new String[]{}));
    }

    @Test
    void testDeleteThrowsException() {
        assertThrows(UnsupportedOperationException.class,
                () -> userDao.delete(new Supplier("new", "pass", true)));
    }
}
