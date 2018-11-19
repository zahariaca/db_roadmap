package com.zahariaca.users;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public interface User<T, K> {
    void promptUserOptions();

    boolean handleUserInput(String input) throws InterruptedException;

    void setCommandQueue(T commandQueue);

    void setResultQueue(K resultQueue);

    default String getUsername() {
        return "";
    }
}
