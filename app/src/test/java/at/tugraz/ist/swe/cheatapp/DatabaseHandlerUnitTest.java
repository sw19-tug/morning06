package at.tugraz.ist.swe.cheatapp;

import android.arch.persistence.room.Room;
import android.content.Context;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DatabaseHandlerUnitTest {
    private MessageRepository messageRepository;
    private Message message;
    private CheatAppDatabase cheatappDatabase;

    @Mock
    private Context context;

    private String DB_NAME = "cheatapp_db";

    @Before
    public void setUp() {
        messageRepository = new MessageRepository(context.getApplicationContext());
    }

    @Test
    public void testInitialization() {
        assertNotNull(messageRepository);
        assertNotNull(cheatappDatabase);
        String text = "Test Message";
        int userId = 37;
        boolean sent = true;
        messageRepository.insertMessage(userId, text, sent);
        message = messageRepository.getMessagesByUserId(userId).getValue().get(0);
        assertEquals(message.getMessageText(), text);
        assertEquals(message.getUserId(), userId);
        assertEquals(message.getMessageSent(), sent);
    }
}