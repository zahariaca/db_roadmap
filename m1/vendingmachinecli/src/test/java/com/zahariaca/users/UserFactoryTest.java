package com.zahariaca.users;

import com.zahariaca.exceptions.UnknownUserTypeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class UserFactoryTest {
    @Test
    public void testCustomerUser() throws UnknownUserTypeException {
        User customer = UserFactory.getUser(TypeOfUser.CUSTOMER);
        assertTrue(customer instanceof Customer);
    }

    @Test
    public void testSupplierUser() throws UnknownUserTypeException {
        User customer = UserFactory.getUser(TypeOfUser.SUPPLIER);
        assertTrue(customer instanceof Supplier);
    }

    @Test
    public void textException() {
        assertThrows(UnknownUserTypeException.class, () -> UserFactory.getUser(null), "No such user, try again.");
    }
}
