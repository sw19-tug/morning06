package at.tugraz.ist.swe.cheatapp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class UtilsTest {
    @Test
    public void testSanitizeMessageEmpty() {
        String input = "";
        String output = Utils.sanitizeMessage(input);

        assertNull(output);
    }

    @Test
    public void testSanitizeMessageOnlyWhitespace() {
        String input = "                  ";
        String output = Utils.sanitizeMessage(input);

        assertNull(output);
    }

    @Test
    public void testSanitizeMessageLeadingWhitespace() {
        String input = "                  abcd";
        String output = Utils.sanitizeMessage(input);

        assertNotNull(output);
        assertEquals("abcd", output);
    }

    @Test
    public void testSanitizeMessageTrailingWhitespace() {
        String input = "abcd           ";
        String output = Utils.sanitizeMessage(input);

        assertNotNull(output);
        assertEquals("abcd", output);
    }

    @Test
    public void testSanitizeMessageLeadingAndTrailingWhitespace() {
        String input = "            abcd           ";
        String output = Utils.sanitizeMessage(input);

        assertNotNull(output);
        assertEquals("abcd", output);
    }
}
