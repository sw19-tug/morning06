package at.tugraz.ist.swe.cheatapp;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;


import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseIntegrationTest {
    private MessageRepository messageRepository;
    private Context context;

    private void deleteDatabase(Context appContext) {
        File databases = new File(appContext.getApplicationInfo().dataDir + "/databases");
        File db = new File(databases, "cheatapp_db");
        if (db.delete())
            System.out.println("Database deleted");
        else
            System.out.println("Failed to delete database");
    }

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        deleteDatabase(context);
        messageRepository = new MessageRepository(context);
    }

    @Test
    public void testInitialization() {
        assertNotNull(messageRepository);
    }

    @Test
    public void testInsertAndLoadOneMessage() throws InterruptedException {
        messageRepository.insertMessage(0, "First Message!", true);

        Thread.sleep(100);

        List<Message> allMessages = messageRepository.getRawMessagesByUserId(0);
        Message message = allMessages.get(0);

        assertEquals(1, allMessages.size());
        assertEquals("First Message!", message.getMessageText());
        assertEquals(0, message.getUserId());
        assertEquals(true, message.getMessageSent());
        assertEquals(1, message.getMessageId());
    }

    @Test
    public void testInsertAndLoadMultipleMessages() throws InterruptedException {
        messageRepository.insertMessage(1, "First Message!", true);
        messageRepository.insertMessage(1, "Response to first Message!", false);
        messageRepository.insertMessage(1, "Second Message!", true);

        Thread.sleep(100);

        List<Message> allMessages = messageRepository.getRawMessagesByUserId(1);
        Message message1 = allMessages.get(0);
        Message message2 = allMessages.get(1);
        Message message3 = allMessages.get(2);

        assertEquals(3, allMessages.size());

        assertEquals("First Message!", message1.getMessageText());
        assertEquals(1, message1.getUserId());
        assertEquals(true, message1.getMessageSent());
        assertTrue(message1.getMessageId() < message2.getMessageId()
                && message1.getMessageId() < message3.getMessageId());

        assertEquals("Response to first Message!", message2.getMessageText());
        assertEquals(1, message2.getUserId());
        assertEquals(false, message2.getMessageSent());
        assertTrue(message2.getMessageId() > message1.getMessageId()
                && message2.getMessageId() < message3.getMessageId());

        assertEquals("Second Message!", message3.getMessageText());
        assertEquals(1, message3.getUserId());
        assertEquals(true, message3.getMessageSent());
        assertTrue(message3.getMessageId() > message1.getMessageId()
                && message3.getMessageId() > message2.getMessageId());
    }

    @Test
    public void testSpecialCharacters() throws InterruptedException {
        messageRepository.insertMessage(2, "\uD83D\uDE85\uD83D\uDCBE\uD83C\uDDE6\uD83C\uDDF9", true);
        messageRepository.insertMessage(2, "′^°£%©±", true);

        Thread.sleep(100);

        List<Message> allMessages = messageRepository.getRawMessagesByUserId(2);
        Message message1 = allMessages.get(0);
        Message message2 = allMessages.get(1);

        assertEquals(2, allMessages.size());

        assertEquals("\uD83D\uDE85\uD83D\uDCBE\uD83C\uDDE6\uD83C\uDDF9", message1.getMessageText());
        assertEquals("′^°£%©±", message2.getMessageText());
    }
}