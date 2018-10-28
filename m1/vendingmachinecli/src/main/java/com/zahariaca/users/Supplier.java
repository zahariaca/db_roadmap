package com.zahariaca.users;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class Supplier implements User {
    @Override
    public void handleUserInput() {
        // offer supplier specific option and handle appropriately
        System.out.println("++ handling supplier");
    }
}
