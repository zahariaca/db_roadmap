package com.zahariaca.filehandlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zahariaca.dao.Dao;
import com.zahariaca.dao.UserDao;
import com.zahariaca.threads.events.OperationType;
import com.zahariaca.threads.events.OperationsEvent;
import com.zahariaca.threads.events.ResultOperationType;
import com.zahariaca.users.Supplier;
import com.zahariaca.users.User;
import com.zahariaca.utils.FileUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;

import static org.junit.Assert.*;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 19.11.2018
 */
class PersistenceFileWriterTest {
    private Set<Supplier> suppliers;
    private User<BlockingQueue<OperationsEvent<OperationType, String[]>>, BlockingQueue<OperationsEvent<ResultOperationType, String>>> supplier;


    @BeforeEach
    void init() {
        suppliers = new TreeSet<>();
        supplier = new Supplier("admin", "admin", true);
        suppliers.add((Supplier) supplier);
        suppliers.add(new Supplier("tester", "tester", false));
    }

    @Test
    void testHandleFileWrite() throws IOException {
        File usersFile = FileUtils.INSTANCE.getFile("src\\test\\resources\\test-file-creation.json");
        PersistenceFileWriter.INSTANCE.handleFileWrite(
                usersFile,
                suppliers);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        String data = gson.toJson(suppliers);

        usersFile = FileUtils.INSTANCE.getFile("src\\test\\resources\\test-file-creation.json");

        byte[] encoded = Files.readAllBytes(Paths.get(usersFile.getPath()));
        String loadedFromFile =  new String(encoded, Charset.defaultCharset());

        assertEquals(data, loadedFromFile);
    }
}
