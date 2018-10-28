package com.zahariaca.users;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class Customer implements User {
    @Override
    public void handleUserInput() {
        // offer customer specific option and handle appropriately
        System.out.println("++ handling customer");
    }
}
