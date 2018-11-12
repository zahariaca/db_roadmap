package com.zahariaca.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zaharia Costin-Alexandru (zaharia.c.alexandru@gmail.com) on 12.11.2018
 */
public class UserInputUtilsTest {
    @Test
    public void testCheckQuitConditionReturnsTrue() {
        assertTrue(UserInputUtils.INSTANCE.checkQuitCondition("quit"));
        assertTrue(UserInputUtils.INSTANCE.checkQuitCondition("q"));
    }

    @Test
    public void testCheckQuitConditionReturnsFalse() {
        assertFalse(UserInputUtils.INSTANCE.checkQuitCondition("1"));
        assertFalse(UserInputUtils.INSTANCE.checkQuitCondition("ABC"));
    }

    @Test
    public void testCheckIsNumericCharacterReturnsTrue() {
        assertTrue(UserInputUtils.INSTANCE.checkIsNumericCharacter("1"));
    }

    @Test
    public void testCheckIsNumericCharacterReturnsFalse() {
        assertFalse(UserInputUtils.INSTANCE.checkIsNumericCharacter("abc"));
    }

    @Test
    public void testConstructPromptMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("%nSelect an operation:%n");
        builder.append("   [1] Login as Customer. %n");
        builder.append("   [2] Login as Supplier. %n");
        builder.append("   [q/quit] to end process. %n");
        String resultedConstruct = UserInputUtils.INSTANCE.constructPromptMessage("%nSelect an operation:%n",
                                                                                    "   [1] Login as Customer. %n",
                                                                                    "   [2] Login as Supplier. %n",
                                                                                    "   [q/quit] to end process. %n");
        assertEquals(builder.toString(), resultedConstruct);
    }

}
