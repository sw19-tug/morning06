package at.tugraz.ist.swe.cheatapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ChatHistoryIntegrationTest {

    private Context context;

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    @Test
    public void chatHistoryMessagesVisible(){
        Message messageSent = new Message(1, "Hi, how are you?", true);
        Message messageReceived = new Message(2, "I'm fine. Thanks.", false);

        List<Message> messageList = new ArrayList<>();
        messageList.add(messageSent);
        messageList.add(messageReceived);

        context = InstrumentationRegistry.getTargetContext().getApplicationContext();

        MessageAdapter messageListAdapter = new MessageAdapter(context, messageList);
        assertEquals(VIEW_TYPE_MESSAGE_SENT, messageListAdapter.getItemViewType(0));
        assertEquals(VIEW_TYPE_MESSAGE_RECEIVED, messageListAdapter.getItemViewType(1));
    }
}
