package com.zahariaca.users;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public interface User<T> {
    void setCommandQueue(T commandQueue);
    String promptUserOptions();
    boolean handleUserInput(String input) throws InterruptedException;
}
