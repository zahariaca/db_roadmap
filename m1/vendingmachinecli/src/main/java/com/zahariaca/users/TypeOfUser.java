package com.zahariaca.users;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public enum TypeOfUser {
    SUPPLIER("Supplier"),
    CUSTOMER("Consumer");

    private String type;

    TypeOfUser(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
