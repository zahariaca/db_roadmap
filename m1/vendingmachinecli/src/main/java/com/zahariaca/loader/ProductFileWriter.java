package com.zahariaca.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zahariaca.pojo.Product;
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

    Logger logger = LogManager.getLogger(ProductFileWriter.class);

    public void handleFileWrite(Set<Product> vendingMachineProducts) throws IOException {
        logger.log(Level.DEBUG, "Starting write of products to persistent file.");
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String data = gson.toJson(vendingMachineProducts);
        data = data.replace("[", "[" + System.lineSeparator());
        data = data.replace("},", "}," + System.lineSeparator());
        data = data.replace("}]", "}" + System.lineSeparator() + "]");

        File file = FileUtils.INSTANCE.getFile("persistence/products.json");
        Path path = Paths.get(file.toURI());
        logger.log(Level.DEBUG, "Output file: {}", file.getAbsolutePath());
        Files.write(path, data.getBytes());
    }
}
