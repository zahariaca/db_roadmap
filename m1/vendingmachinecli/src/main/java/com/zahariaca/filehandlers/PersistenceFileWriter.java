package com.zahariaca.filehandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;


/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 28.10.2018
 */
public enum PersistenceFileWriter {
    INSTANCE;

    private final Logger logger = LogManager.getLogger(PersistenceFileWriter.class);
    private final GsonBuilder builder = new GsonBuilder();
    private final Gson gson = builder.setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public void handleFileWrite(File file, Set<?> persistentData) throws IOException {
        logger.log(Level.DEBUG, "Starting write of products to persistent file.");

        String data = gson.toJson(persistentData);

        Path path = Paths.get(file.toURI());
        logger.log(Level.DEBUG, "Output file: {}", file.getAbsolutePath());
        Files.write(path, data.getBytes());
    }
}
