package at.tugraz.ist.swe.cheatapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseIntegrationTest {
    private MessageRepository messageRepository;

    @Before
    public void setUp() {
        Context context;
        context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        messageRepository = MessageRepository.createInMemoryRepository(context);
    }

    @Test
    public void testInitialization() {
        assertNotNull(messageRepository);
    }

    @Test
    public void testInsertAndLoadOneMessage() throws InterruptedException, ExecutionException {
        messageRepository.insertMessage(new ChatMessage(0, "First Message!", true, false)).get();

        List<ChatMessage> allMessages = messageRepository.getRawMessagesByUserId(0);
        ChatMessage message = allMessages.get(0);

        assertEquals(1, allMessages.size());
        assertEquals("First Message!", message.getMessageText());
        assertEquals(0, message.getUserId());
        assertEquals(true, message.getMessageSent());
        assertEquals(1, message.getMessageId());
        assertEquals(false, message.getMessageEdited());
        assertNotNull(message.getMessageUUID());
    }

    @Test
    public void testInsertAndLoadMultipleMessages() throws InterruptedException, ExecutionException {
        messageRepository.insertMessage(new ChatMessage(1, "First Message!", true, false)).get();
        messageRepository.insertMessage(new ChatMessage(1, "Response to first Message!", false, false)).get();
        messageRepository.insertMessage(new ChatMessage(1, "Second Message!", true, false)).get();

        List<ChatMessage> allMessages = messageRepository.getRawMessagesByUserId(1);
        ChatMessage message1 = allMessages.get(0);
        ChatMessage message2 = allMessages.get(1);
        ChatMessage message3 = allMessages.get(2);

        assertEquals(3, allMessages.size());

        assertEquals("First Message!", message1.getMessageText());
        assertEquals(1, message1.getUserId());
        assertEquals(true, message1.getMessageSent());
        assertEquals(false, message1.getMessageEdited());
        assertNotNull(message1.getMessageUUID());
        assertTrue(message1.getMessageId() < message2.getMessageId()
                && message1.getMessageId() < message3.getMessageId());

        assertEquals("Response to first Message!", message2.getMessageText());
        assertEquals(1, message2.getUserId());
        assertEquals(false, message2.getMessageSent());
        assertEquals(false, message2.getMessageEdited());
        assertNotNull(message2.getMessageUUID());
        assertTrue(message2.getMessageId() > message1.getMessageId()
                && message2.getMessageId() < message3.getMessageId());

        assertEquals("Second Message!", message3.getMessageText());
        assertEquals(1, message3.getUserId());
        assertEquals(true, message3.getMessageSent());
        assertEquals(false, message3.getMessageEdited());
        assertNotNull(message3.getMessageUUID());
        assertTrue(message3.getMessageId() > message1.getMessageId()
                && message3.getMessageId() > message2.getMessageId());
    }

    @Test
    public void testSpecialCharacters() throws InterruptedException, ExecutionException {
        messageRepository.insertMessage(new ChatMessage(2, "\uD83D\uDE85\uD83D\uDCBE\uD83C\uDDE6\uD83C\uDDF9", true, false)).get();
        messageRepository.insertMessage(new ChatMessage(2, "′^°£%©±", true, false)).get();

        List<ChatMessage> allMessages = messageRepository.getRawMessagesByUserId(2);
        ChatMessage message1 = allMessages.get(0);
        ChatMessage message2 = allMessages.get(1);

        assertEquals(2, allMessages.size());

        assertEquals("\uD83D\uDE85\uD83D\uDCBE\uD83C\uDDE6\uD83C\uDDF9", message1.getMessageText());
        assertEquals("′^°£%©±", message2.getMessageText());
    }

    @Test
    public void testMessageEditing() throws InterruptedException, ExecutionException {
        int userId = 32;
        String originalMessageText = "I will be edited, hopefully!";
        String editedMessageText = "Now I'm edited!";
        ChatMessage originalMessage = new ChatMessage(userId, originalMessageText, true, false);

        messageRepository.insertMessage(originalMessage).get();
        ChatMessage originalMessageDTO = messageRepository.getMessagesByMessageUUID(originalMessage.getMessageUUID()).get(0);
        assertMessageEqual(originalMessageDTO, originalMessage);

        originalMessageDTO.setMessageText(editedMessageText);
        originalMessageDTO.setMessageEdited(true);
        originalMessageDTO.setTimestamp(System.currentTimeMillis());

        messageRepository.updateMessage(originalMessageDTO).get();

        ChatMessage editedMessageDTO = messageRepository.getMessagesByMessageUUID(originalMessage.getMessageUUID()).get(0);
        assertMessageEqual(originalMessageDTO, editedMessageDTO);

        List<ChatMessage> allMessages = messageRepository.getRawMessagesByUserId(userId);
        assertEquals(allMessages.size(), 1);
    }

    private void assertMessageEqual(ChatMessage original, ChatMessage copy)
    {
        assertEquals(original.getMessageText(), copy.getMessageText());
        assertEquals(original.getMessageUUID(), copy.getMessageUUID());
        assertEquals(original.getTimestamp(), copy.getTimestamp());
        assertEquals(original.getMessageSent(), copy.getMessageSent());
        assertEquals(original.getMessageEdited(), copy.getMessageEdited());
        assertEquals(original.getUserId(), copy.getUserId());
        assertEquals(original.getJsonString(), copy.getJsonString());
    }
}