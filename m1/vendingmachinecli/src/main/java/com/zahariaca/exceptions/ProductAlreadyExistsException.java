package com.zahariaca.exceptions;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 04.11.2018
 */
public class ProductAlreadyExistsException extends Throwable {
    public ProductAlreadyExistsException(String s) {
        super(s);
    }
}
