package com.zahariaca.pojo.users;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 20.11.2018
 */
public class UserTest {
    private User supplierOne;
    private User supplietTwo;

    @BeforeEach
    void init() {
        supplierOne = new User("admin", DigestUtils.sha256Hex("admin"), true);
        supplietTwo = new User("azaharia", DigestUtils.sha256Hex("admin"), true);
    }

    @Test
    void testNotEqual() {
        assertNotEquals(supplierOne, supplietTwo);
        assertNotEquals(supplierOne.hashCode(), supplietTwo.hashCode());
        assertNotEquals(supplierOne.getUserId(), supplietTwo.getUserId());
    }

    @Test
    void testCompareToReturnsNonZeroCode() {
        assertFalse(0 == supplierOne.compareTo(new User("test", DigestUtils.sha256Hex("test"), false)));
    }

    @Test
    void testCompareToReturnsNonZeroCodeWithSameUsername() {
        assertTrue(0 != supplierOne.compareTo(new User("admin", DigestUtils.sha256Hex("admin"), true)));
    }


    @Test
    void testCorrectSerialization() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        String data = gson.toJson(supplierOne);
        System.out.println(data);

        User deserializedUser = gson.fromJson(data, new TypeToken<User>() {
        }.getType());

        assertEquals(supplierOne, deserializedUser);
        assertEquals(supplierOne.hashCode(), deserializedUser.hashCode());
    }
}
