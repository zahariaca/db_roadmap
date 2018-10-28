package com.zahariaca.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zahariaca.pojo.Product;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public enum FileLoader {
    INSTANCE;

    Logger logger = LogManager.getLogger(FileLoader.class);

    public Set<Product> loadFromFile(File file) {
        logger.log(Level.DEBUG, "Reading from file: {}", file.getAbsolutePath());

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            Type hashSetType = new TypeToken<HashSet<Product>>(){}.getType();
            Set<Product> deserializedSet = gson.fromJson(bufferedReader, hashSetType);

            logger.log(Level.DEBUG, "Deserialization successful, returning set.");
            return deserializedSet;
        } catch (IOException e) {
            logger.log(Level.ERROR, e.getMessage());
        }

        logger.log(Level.ERROR, "Something went wrong, returning empty collection.");
        return Collections.emptySet();
    }
}
