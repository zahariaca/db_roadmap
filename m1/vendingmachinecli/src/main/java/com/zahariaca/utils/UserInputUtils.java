package com.zahariaca.utils;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public class UserInputUtils {

    public static boolean checkQuitCondition(String userInput) {
        if ("quit".equalsIgnoreCase(userInput) || "q".equalsIgnoreCase(userInput)) {
            return true;
        }

        return false;
    }

    public static String constructPromptMessage(String... strings) {
        return Stream.of(strings).collect(joining());
    }

}
