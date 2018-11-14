package com.zahariaca.exceptions;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 14.11.2018
 */
public class UserInUnsafeStateException extends RuntimeException {
    public UserInUnsafeStateException(String message) {
        super(message);
    }
}
