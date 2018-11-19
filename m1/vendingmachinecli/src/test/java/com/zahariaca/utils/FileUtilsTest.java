package com.zahariaca.utils;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 19.11.2018
 */
class FileUtilsTest {
    @Test
    void testFileIsCreatedOrFound() {
        File file = FileUtils.INSTANCE.getFile("src\\test\\resources\\test-file-creation.json");
        assertNotNull(file);
        assertTrue(file.exists());
    }
}
