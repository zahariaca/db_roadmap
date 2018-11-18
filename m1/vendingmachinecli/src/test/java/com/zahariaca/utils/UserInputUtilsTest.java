package com.zahariaca.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 12.11.2018
 */
class UserInputUtilsTest {
    @Test
    void testCheckQuitConditionReturnsTrue() {
        assertTrue(UserInputUtils.INSTANCE.checkQuitCondition("quit"));
        assertTrue(UserInputUtils.INSTANCE.checkQuitCondition("q"));
    }

    @Test
    void testCheckQuitConditionReturnsFalse() {
        assertFalse(UserInputUtils.INSTANCE.checkQuitCondition("1"));
        assertFalse(UserInputUtils.INSTANCE.checkQuitCondition("ABC"));
    }

    @Test
    void testCheckIsNumericCharacterReturnsTrue() {
        assertTrue(UserInputUtils.INSTANCE.checkIsInteger("1"));
    }

    @Test
    void testCheckIsNumericCharacterReturnsFalse() {
        assertFalse(UserInputUtils.INSTANCE.checkIsInteger("abc"));
    }

    @Test
    void testConstructPromptMessage() {
        String resultedConstruct = UserInputUtils.INSTANCE.constructPromptMessage("%nSelect an operation:%n",
                "   [1] Login as Customer. %n",
                "   [2] Login as Supplier. %n",
                "   [q/quit] to end process. %n");
        String builder = "%nSelect an operation:%n" +
                "   [1] Login as Customer. %n" +
                "   [2] Login as Supplier. %n" +
                "   [q/quit] to end process. %n";
        assertEquals(builder, resultedConstruct);
    }

}
