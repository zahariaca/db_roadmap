package com.zahariaca.users;

import com.zahariaca.exceptions.UnknownUserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class UserFactoryTest {
    @Test
    public void testCustomerUser() throws UnknownUserException {
        User customer = UserFactory.getUser(TypeOfUser.CUSTOMER);
        assertTrue(customer instanceof Customer);
    }

    @Test
    public void testSupplierUser() throws UnknownUserException {
        User customer = UserFactory.getUser(TypeOfUser.SUPPLIER);
        assertTrue(customer instanceof Supplier);
    }

    @Test
    public void textException() {
        assertThrows(UnknownUserException.class, () -> UserFactory.getUser(null), "No such user, try again.");
    }
}
