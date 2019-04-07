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
        Message messageSent = new Message(1, "Hi, how are you?", true);
        Message messageReceived = new Message(2, "I'm fine. Thanks.", false);

        List<Message> messageList = new ArrayList<>();
        messageList.add(messageSent);
        messageList.add(messageReceived);

        MessageAdapter messageListAdapter = new MessageAdapter(messageList);
        assertEquals(VIEW_TYPE_MESSAGE_SENT, messageListAdapter.getItemViewType(0));
        assertEquals(VIEW_TYPE_MESSAGE_RECEIVED, messageListAdapter.getItemViewType(1));
        assertEquals(messageList.size(), messageListAdapter.getItemCount());
    }

}
