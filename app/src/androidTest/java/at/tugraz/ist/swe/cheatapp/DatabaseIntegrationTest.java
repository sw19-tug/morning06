package at.tugraz.ist.swe.cheatapp;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;


import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseIntegrationTest {
    private MessageRepository messageRepository;
    private Message message;
    Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        messageRepository = new MessageRepository(context);
    }

    @Test
    public void testInitialization() throws InterruptedException {
        assertNotNull(messageRepository);
        String text = "This is a test Message!";
        int userId = 9;
        boolean sent = true;
        messageRepository.insertMessage(userId, text, sent);

        Thread.sleep(100);

        message = messageRepository.getRawMessagesByUserId(userId).get(messageRepository.getRawMessagesByUserId(userId).size() - 1);
        List<Message> allMessages = messageRepository.getRawMessagesByUserId(userId);
        for(Message msg : allMessages) {
            System.out.println(msg.getUserId() + ": " + msg.getMessageId() + ": "
                    + msg.getMessageText() + "  " + msg.getMessageSent());
        }

        assertEquals(message.getMessageText(), text);
        assertEquals(message.getUserId(), userId);
        assertEquals(message.getMessageSent(), sent);
    }
}