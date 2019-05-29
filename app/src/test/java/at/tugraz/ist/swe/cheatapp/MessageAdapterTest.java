package at.tugraz.ist.swe.cheatapp;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MessageAdapterTest {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    @Test
    public void testGetItemViewType() {
        ChatMessage messageSent = new ChatMessage(1, "Hi, how are you?", true, false);
        ChatMessage messageReceived = new ChatMessage(2, "I'm fine. Thanks.", false, false);

        List<ChatMessage> messageList = new ArrayList<>();
        messageList.add(messageSent);
        messageList.add(messageReceived);

        ChatFragment chatFragment = new ChatFragment();
        MessageAdapter messageListAdapter = new MessageAdapter(messageList, chatFragment);
        assertEquals(VIEW_TYPE_MESSAGE_SENT, messageListAdapter.getItemViewType(0));
        assertEquals(VIEW_TYPE_MESSAGE_RECEIVED, messageListAdapter.getItemViewType(1));
        assertEquals(messageList.size(), messageListAdapter.getItemCount());
    }
}
