package com.zahariaca.users;

import com.zahariaca.exceptions.UnknownUserTypeException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class UserFactory {
    private static Logger logger = LogManager.getLogger(UserFactory.class);

    public static User getUser(TypeOfUser typeOfUser) throws UnknownUserTypeException {
        if (TypeOfUser.CUSTOMER.equals(typeOfUser)) {
            logger.log(Level.INFO, "Current user is of type: {}", typeOfUser.getType());
            return new Customer();
        } else if (TypeOfUser.SUPPLIER.equals(typeOfUser)) {
            logger.log(Level.INFO, "Current user is of type: {}", typeOfUser.getType());
            return new Supplier();
        }

        throw new UnknownUserTypeException("No such user type, try again.");
    }

}
