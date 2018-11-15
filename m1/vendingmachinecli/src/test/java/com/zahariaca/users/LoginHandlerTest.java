package com.zahariaca.users;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
class LoginHandlerTest {


    @Test
    void testIncorrectUserType() {
        assertFalse(LoginHandler.INSTANCE.checkUserCredentials(null));
    }

    @Test
    void testCorrectCredentials() {
        String dummySystemIn = String.format("%s%n%s%n", "admin", "password");
        InputStream stdin = System.in;

        try {
            System.setIn(new ByteArrayInputStream(dummySystemIn.getBytes()));
            assertTrue(LoginHandler.INSTANCE.checkUserCredentials(TypeOfUser.SUPPLIER));
        } finally {
            System.setIn(stdin);
        }
    }

    @Test
    void testIncorrectCredentials() {
        String dummySystemIn = String.format("%s%n%s%n", "admin", "incorrect");
        InputStream stdin = System.in;

        try {
            System.setIn(new ByteArrayInputStream(dummySystemIn.getBytes()));
            assertFalse(LoginHandler.INSTANCE.checkUserCredentials(TypeOfUser.SUPPLIER));
        } finally {
            System.setIn(stdin);
        }
    }

}
