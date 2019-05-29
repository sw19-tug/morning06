package at.tugraz.ist.swe.cheatapp;

import android.content.Context;
import android.content.SharedPreferences;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ChatFragmentEspressoTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            super.beforeActivityLaunched();
            Utils.setTesting(true);

            Context ctx = InstrumentationRegistry.getTargetContext();
            SharedPreferences.Editor editor = ctx.getSharedPreferences("CheatAppSharedPreferences", Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.commit();
        }
    };

    private DummyBluetoothProvider provider;
    private MainActivity activity;
    private MessageRepository messageRepository;

    @Before
    public void setUp() throws InterruptedException {
        activity = mainActivityTestRule.getActivity();
        provider = (DummyBluetoothProvider) activity.getBluetoothProvider();
        provider.connectToDevice(provider.getPairedDevices().get(0));
        activity.showChatFragment();
        messageRepository = activity.getChatFragment().getMessageRepository();
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

        MessageAdapter messageAdapter = activity.getChatFragment().getMessageAdapter();
        List<ChatMessage> messageList = messageAdapter.getMessageList();

        assertEquals(messageList.get(messageList.size() - 1).getMessageText(), testText);
    }

    @Test
    public void chatHistoryVisible() {
        onView(withId(R.id.rvChat)).check(matches(isDisplayed()));
    }


    @Test
    // TODO this test does not check anything
    public void chatHistoryScrollable() {
        messageRepository.insertMessage(new ChatMessage(1, "Hi, how are you?", true));
        messageRepository.insertMessage(new ChatMessage(1, "I'm fine. Thanks.", false));
        messageRepository.insertMessage(new ChatMessage(1, "What are you doing?", true));
        messageRepository.insertMessage(new ChatMessage(1, "Nothing. Wanna drink some coffee?", false));
        messageRepository.insertMessage(new ChatMessage(1, "Sure!", true));
        messageRepository.insertMessage(new ChatMessage(1, "When? Where?", true));
        messageRepository.insertMessage(new ChatMessage(1, "At 3? Ducks Coffee?", false));
        messageRepository.insertMessage(new ChatMessage(1, "You are late!!", false));
        messageRepository.insertMessage(new ChatMessage(1, "-.-", false));
        messageRepository.insertMessage(new ChatMessage(1, "Sorry, the tram is late...", true));
        messageRepository.insertMessage(new ChatMessage(1, "I'll buy you some cake :D", true));
        messageRepository.insertMessage(new ChatMessage(1, "Hmmm. ok...", false));
        messageRepository.insertMessage(new ChatMessage(1, "It was a nice day with you :)", false));
        messageRepository.insertMessage(new ChatMessage(1, "We definitely have to do this again!", true));
        messageRepository.insertMessage(new ChatMessage(1, "On Saturday?", false));
        messageRepository.insertMessage(new ChatMessage(1, "Sure! Same time, same place?", true));
        messageRepository.insertMessage(new ChatMessage(1, "Perfect. See you then. :)", false));
        messageRepository.insertMessage(new ChatMessage(1, "See you :D", true));
        messageRepository.insertMessage(new ChatMessage(1, "I think I forgot my jacket in your flat", true));
        messageRepository.insertMessage(new ChatMessage(1, "Can you bring it on Saturday?", true));
        messageRepository.insertMessage(new ChatMessage(1, "I hope I won't forget ;)", false));
        messageRepository.insertMessage(new ChatMessage(1, "Send me a reminder please xD", false));
        messageRepository.insertMessage(new ChatMessage(1, "OK :D", true));
        messageRepository.insertMessage(new ChatMessage(1, ":P", true));
        messageRepository.insertMessage(new ChatMessage(1, ":)", true));

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

        // TODO solve race condition
        int listLength = 0;
        while(listLength == 0)
        {
            listLength = messageRepository.getRawMessagesByUserId(1).size();
            Thread.sleep(100);
        }
        ChatMessage receiveMessage = messageRepository.getRawMessagesByUserId(1).get(listLength-1);
        assertEquals(testText, receiveMessage.getMessageText());
    }


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

        ChatMessage receivedMessage = messageRepository.getRawMessagesByUserId(1).get(0);
        assertEquals(sanitizedString, receivedMessage.getMessageText());
    }

    @Test
    public void testIfEmptyMessageIsSendable() {
        String testString = "         ";
        onView(withId(R.id.txt_chat_entry)).perform(typeText(testString), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());

        assertTrue(messageRepository.getRawMessagesByUserId(1).isEmpty());
    }

    @Test
    public void testIfEmojiButtonVisible() {
        onView(withId(R.id.btn_emoji_keyboard)).check(matches(isDisplayed()));
    }

    @Test
    public void testIfEmojiKeyboardIsShown() throws InterruptedException{
        assertFalse(activity.getChatFragment().isEmojiKeyboardShowing());
        onView(withId(R.id.btn_emoji_keyboard)).perform(click());
        Thread.sleep(300);
        assertTrue(activity.getChatFragment().isEmojiKeyboardShowing());
        onView(withId(R.id.btn_emoji_keyboard)).perform(click());
        Thread.sleep(300);
        assertFalse(activity.getChatFragment().isEmojiKeyboardShowing());
    }
}
