package at.tugraz.ist.swe.cheatapp;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DatabaseHandlerUnitTest {
    private DatabaseHandler databaseHandler;

    @Before
    public void setUp() {
        databaseHandler = new DatabaseHandler();
    }

    @Test
    public void testInitialization() {
        assertNotNull(databaseHandler);
        assertTrue(databaseHandler.isDatabaseOpen());
    }
}