package com.zahariaca.users;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public enum LoginHandler {
    INSTANCE;
    private Logger logger = LogManager.getLogger(LoginHandler.class);

    public boolean checkUserCredentials(TypeOfUser typeOfUser) {
        if (!TypeOfUser.SUPPLIER.equals(typeOfUser)) {
            return false;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Input username: ");
        String username = scanner.next();
        System.out.println("Input password: ");
        String password = scanner.next();

        //TODO: get rid of dummy data, should checked with DB Query. Maybe leave for m2 for simplicity
        return "admin".equals(username) && "password".equals(password);
    }
}
