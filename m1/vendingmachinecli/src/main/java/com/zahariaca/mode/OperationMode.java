package com.zahariaca.mode;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 18.12.2018
 */
public enum OperationMode {
    FILE("file"),
    DB("db");
    private final String mode;

    OperationMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}
