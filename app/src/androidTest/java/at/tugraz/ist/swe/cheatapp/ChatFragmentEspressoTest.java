package at.tugraz.ist.swe.cheatapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ChatFragmentEspressoTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    private MessageRepository messageRepository;
    private DummyBluetoothProvider provider;

    @Before
    public void setUp() throws InterruptedException {
        Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        DatabaseIntegrationTest db = new DatabaseIntegrationTest();
        db.deleteDatabase(context);
        provider = new DummyBluetoothProvider();
        provider.enableDummyDevices(1);
        mainActivityTestRule.getActivity().setBluetoothProvider(provider,true);
        provider.connectToDevice(provider.getPairedDevices().get(0));
        mainActivityTestRule.getActivity().showChatFragment();
    }

    @Test
    public void testFieldVisible() {
        onView(withId(R.id.txt_chat_entry)).check(matches(isDisplayed()));
    }

    @Test
    public void testInput() {
        String testText = "Test_Input";
        onView(withId(R.id.txt_chat_entry)).perform(replaceText(""));
        onView(withId(R.id.txt_chat_entry)).perform(typeText(testText), closeSoftKeyboard());
        onView(withId(R.id.txt_chat_entry)).check(matches(withText(testText)));
    }

    @Test
    public void testSendButtonVisible() {
        onView(withId(R.id.btn_chat_send)).check(matches(isDisplayed()));
    }


    @Test
    public void testDisconnectButtonVisible() {
        onView(withId(R.id.btn_connect_disconnect)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_connect_disconnect)).check(matches(withText(R.string.disconnect)));
    }

    @Test
    public void clearTextField() {
        String testText = "This text is redundant because it will be cleared anyway ;-)";
        onView(withId(R.id.txt_chat_entry)).perform(typeText(testText), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());
        onView(withId(R.id.txt_chat_entry)).check(matches(withText("")));
    }

    @Test
    public void saveTextIntoDummyDevice() throws InterruptedException {
        String testText = "Hello World";

        onView(withId(R.id.txt_chat_entry)).perform(typeText(testText), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());
        provider.getThread().join();

        MessageAdapter messageAdapter = mainActivityTestRule.getActivity().getChatFragment().getMessageAdapter();
        List<Message> messageList = messageAdapter.getMessageList();

        assertEquals(messageList.get(messageList.size() - 1).getMessageText(), testText);
    }

    @Test
    public void chatHistoryVisible() {
        onView(withId(R.id.rvChat)).check(matches(isDisplayed()));
    }


    @Test
    // TODO this test does not check anything
    public void chatHistoryScrollable() {
        messageRepository = new MessageRepository(mainActivityTestRule.getActivity().getApplicationContext());

        messageRepository.insertMessage(new Message(1, "Hi, how are you?", true));
        messageRepository.insertMessage(new Message(1, "I'm fine. Thanks.", false));
        messageRepository.insertMessage(new Message(1, "What are you doing?", true));
        messageRepository.insertMessage(new Message(1, "Nothing. Wanna drink some coffee?", false));
        messageRepository.insertMessage(new Message(1, "Sure!", true));
        messageRepository.insertMessage(new Message(1, "When? Where?", true));
        messageRepository.insertMessage(new Message(1, "At 3? Ducks Coffee?", false));
        messageRepository.insertMessage(new Message(1, "You are late!!", false));
        messageRepository.insertMessage(new Message(1, "-.-", false));
        messageRepository.insertMessage(new Message(1, "Sorry, the tram is late...", true));
        messageRepository.insertMessage(new Message(1, "I'll buy you some cake :D", true));
        messageRepository.insertMessage(new Message(1, "Hmmm. ok...", false));
        messageRepository.insertMessage(new Message(1, "It was a nice day with you :)", false));
        messageRepository.insertMessage(new Message(1, "We definitely have to do this again!", true));
        messageRepository.insertMessage(new Message(1, "On Saturday?", false));
        messageRepository.insertMessage(new Message(1, "Sure! Same time, same place?", true));
        messageRepository.insertMessage(new Message(1, "Perfect. See you then. :)", false));
        messageRepository.insertMessage(new Message(1, "See you :D", true));
        messageRepository.insertMessage(new Message(1, "I think I forgot my jacket in your flat", true));
        messageRepository.insertMessage(new Message(1, "Can you bring it on Saturday?", true));
        messageRepository.insertMessage(new Message(1, "I hope I won't forget ;)", false));
        messageRepository.insertMessage(new Message(1, "Send me a reminder please xD", false));
        messageRepository.insertMessage(new Message(1, "OK :D", true));
        messageRepository.insertMessage(new Message(1, ":P", true));
        messageRepository.insertMessage(new Message(1, ":)", true));

        onView(withId(R.id.rvChat)).perform(RecyclerViewActions.scrollToPosition(20));
        onView(withId(R.id.rvChat)).perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.rvChat)).perform(RecyclerViewActions.scrollToPosition(20));
        onView(withId(R.id.rvChat)).perform(RecyclerViewActions.scrollToPosition(0));
    }

    @Test
    public void testChatHistoryOnMessageSend() throws InterruptedException {
        String testText = "Hello, I am a test message. ;-)";
        onView(withId(R.id.txt_chat_entry)).perform(typeText(testText), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());
        provider.getThread().join();

        messageRepository = new MessageRepository(mainActivityTestRule.getActivity().getApplicationContext());
        // TODO solve race condition
        int listLength = 0;
        while(listLength == 0)
        {
            listLength = messageRepository.getRawMessagesByUserId(1).size();
            Thread.sleep(100);
        }
        Message receiveMessage = messageRepository.getRawMessagesByUserId(1).get(listLength-1);
        assertEquals(testText, receiveMessage.getMessageText());
    }

    /*
    @Test
    public void timestampVisibleOnSend() throws InterruptedException {
        String testText = "Hello, I is there a timestamp?";
        onView(withId(R.id.txt_chat_entry)).perform(typeText(testText), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());
        provider.getThread().join();

        onView(withId(R.id.txt_chat_receivedMessage)).check(matches(isDisplayed()));
    }

    @Test
    public void testIfMessageTextIsSanitized() throws InterruptedException {
        String testString = "        Sanitize me please!!!!                      ";
        String sanitizedString = Utils.sanitizeMessage(testString);
        onView(withId(R.id.txt_chat_entry)).perform(typeText(testString), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());
        provider.getThread().join();

        messageRepository = new MessageRepository(mainActivityTestRule.getActivity().getApplicationContext());
        Message receivedMessage = messageRepository.getRawMessagesByUserId(1).get(0);

        assertEquals(receivedMessage.getMessageText(), sanitizedString);
    } */

    @Test
    public void testIfEmptyMessageIsSendable() {
        String testString = "         ";
        onView(withId(R.id.txt_chat_entry)).perform(typeText(testString), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());

        messageRepository = new MessageRepository(mainActivityTestRule.getActivity().getApplicationContext());

        assertTrue(messageRepository.getRawMessagesByUserId(1).isEmpty());
    }
}
