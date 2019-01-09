package com.zahariaca.dao;

import com.zahariaca.dao.file.FileUserDao;
import com.zahariaca.pojo.users.User;
import org.apache.commons.codec.digest.DigestUtils;
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
public class FileUserDaoTest {
    public static final String USER_PASSWORD_ADMIN = DigestUtils.sha256Hex("admin");
    public static final String USER_PASSWORD_TESTER = DigestUtils.sha256Hex("tester");
    public static final String USER_PASSWORD_PASS = DigestUtils.sha256Hex("pass");
    private Set<User> supplierClis;
    private Dao<User, String> userDao;
    private User supplier;


    @BeforeEach
    void init() {
        supplierClis = new TreeSet<>();
        supplier = new User("admin", USER_PASSWORD_ADMIN, true);
        supplierClis.add(supplier);
        supplierClis.add(new User("tester", USER_PASSWORD_TESTER, false));
        userDao = new FileUserDao(supplierClis);
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
                () -> userDao.save(new User("new", USER_PASSWORD_PASS, true)));
    }

    @Test
    void testUpdateThrowsException() {
        assertThrows(UnsupportedOperationException.class,
                () -> userDao.update(
                        new User("new", USER_PASSWORD_PASS, true),
                        new String[]{}));
    }

    @Test
    void testDeleteThrowsException() {
        assertThrows(UnsupportedOperationException.class,
                () -> userDao.delete(new User("new", USER_PASSWORD_PASS, true)));
    }
}
