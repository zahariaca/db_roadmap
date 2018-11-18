package com.zahariaca.filehandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zahariaca.utils.FileUtils;
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
public enum ProductFileWriter {
    INSTANCE;

    private final Logger logger = LogManager.getLogger(ProductFileWriter.class);
    private final GsonBuilder builder = new GsonBuilder();
    private final Gson gson = builder.setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public void handleFileWrite(String fileLocation, Set<?> persistentData) throws IOException {
        logger.log(Level.DEBUG, "Starting write of products to persistent file.");

        String data = gson.toJson(persistentData);

        File file = FileUtils.INSTANCE.getFile(fileLocation);
        Path path = Paths.get(file.toURI());
        logger.log(Level.DEBUG, "Output file: {}", file.getAbsolutePath());
        Files.write(path, data.getBytes());
    }
}
