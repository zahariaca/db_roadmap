package com.zahariaca.utils;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public enum UserInputUtils {
    INSTANCE;

    private Logger logger = LogManager.getLogger(UserInputUtils.class);

    public boolean checkQuitCondition(String userInput) {
        return "quit".equalsIgnoreCase(userInput) || "q".equalsIgnoreCase(userInput);
    }

    public boolean checkIsNumericCharacter(String userInput) {
        try {
            Integer.parseInt(userInput);
        } catch (NumberFormatException e) {
            logger.log(Level.WARN, e.getMessage());
            return false;
        }

        return true;
    }

    public String constructPromptMessage(String... strings) {
        return Stream.of(strings).collect(joining());
    }

}
