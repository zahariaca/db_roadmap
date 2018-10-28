package com.zahariaca.exceptions;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class UnknownUserException extends Exception {
    public UnknownUserException(String message) {
        super(message);
    }
}
