package com.zahariaca.cli;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 19.11.2018
 */
public interface Cli<T, K> {
    void promptUserOptions();

    void setCommandQueue(T commandQueue);

    void setResultQueue(K resultQueue);
}
