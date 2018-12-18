package com.zahariaca.cli;

import javax.validation.constraints.NotNull;

/**
 * @param <T> type of command queue to be set
 * @param <K> type of result queue to be set
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 19.11.2018
 */
public interface Cli<T, K> {
    void promptUserOptions();

    void setCommandQueue(@NotNull T commandQueue);

    void setResultQueue(@NotNull K resultQueue);
}
