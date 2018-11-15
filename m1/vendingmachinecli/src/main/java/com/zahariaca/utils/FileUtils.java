package com.zahariaca.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 04.11.2018
 */
public enum FileUtils {
    INSTANCE;

    private Logger logger = LogManager.getLogger(FileUtils.class);

    public File getFile(String filePath) {
        File file = new File(filePath);
        try {
            if (file.createNewFile()) {
                logger.log(Level.INFO, "File: {} does not exist. Creating.", file.getName());
            }
        } catch (IOException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        return file;
    }
}
