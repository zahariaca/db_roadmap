package com.zahariaca.filehandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zahariaca.pojo.users.User;
import com.zahariaca.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 19.11.2018
 */
class PersistenceFileWriterTest {
    private Set<User> suppliers;
    private User supplier;


    @BeforeEach
    void init() {
        suppliers = new TreeSet<>();
        supplier = new User("admin", "admin", true);
        suppliers.add(supplier);
        suppliers.add(new User("azaharia", "123456", false));
    }

    @Test
    void testHandleFileWrite() throws IOException {
        File usersFile = FileUtils.INSTANCE.getFile("src/test/resources/test-file-creation.json");
        PersistenceFileWriter.INSTANCE.handleFileWrite(
                usersFile,
                suppliers);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        String data = gson.toJson(suppliers);

        usersFile = FileUtils.INSTANCE.getFile("src/test/resources/test-file-creation.json");

        byte[] encoded = Files.readAllBytes(Paths.get(usersFile.getPath()));
        String loadedFromFile = new String(encoded, Charset.defaultCharset());

        assertEquals(data, loadedFromFile);
    }
}
