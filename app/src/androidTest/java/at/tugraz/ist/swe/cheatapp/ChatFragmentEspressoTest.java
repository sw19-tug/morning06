package at.tugraz.ist.swe.cheatapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringEndsWith.endsWith;
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
            editor.putString("nickname", "Test Nickname");
            editor.apply();
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
        provider.getThread().join();
        messageRepository = activity.getChatFragment().getMessageRepository();
    }

    @Test
    public void testFieldVisible() {
        onView(withId(R.id.txt_chat_entry)).check(matches(isDisplayed()));
    }

    @Test
    public void testNicknameVisible() {
        onView(withText("Test Nickname")).check(matches(isDisplayed()));
    }

    @Test
    public void testIfNicknameChangesAtReconnect() {
        onView(withText("Test Nickname")).check(matches(isDisplayed()));
        onView(withId(R.id.btn_con_disconnect)).perform(click());

        openActionBarOverflowOrOptionsMenu(activity.getApplicationContext());
        onView(withText(R.string.menu_set_nickname)).perform(click());
        onView(withClassName(endsWith("EditText"))).perform(replaceText("New Nickname"));

        provider.connectToDevice(provider.getPairedDevices().get(0));

        onView(withText("New Nickname")).check(matches(isDisplayed()));
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
        onView(withId(R.id.btn_con_disconnect)).check(matches(isDisplayed()));
    }

    @Test
    public void testClearTextField() {
        String testText = "This text is redundant because it will be cleared anyway ;-)";
        onView(withId(R.id.txt_chat_entry)).perform(typeText(testText), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());
        onView(withId(R.id.txt_chat_entry)).check(matches(withText("")));
    }

    @Test
    public void testSaveTextIntoDummyDevice() throws InterruptedException {
        String testText = "Hello World";

        onView(withId(R.id.txt_chat_entry)).perform(typeText(testText), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());
        provider.getThread().join();

        MessageAdapter messageAdapter = activity.getChatFragment().getMessageAdapter();
        List<ChatMessage> messageList = messageAdapter.getMessageList();

        assertEquals(messageList.get(messageList.size() - 1).getMessageText(), testText);
    }

    @Test
    public void testChatHistoryVisible() {
        onView(withId(R.id.rv_chat)).check(matches(isDisplayed()));
    }

    @Test
    public void testChatHistoryScrollable() {
        messageRepository.insertMessage(new ChatMessage(1, "Hi, how are you?", true, false));
        messageRepository.insertMessage(new ChatMessage(1, "I'm fine. Thanks.", false, false));
        messageRepository.insertMessage(new ChatMessage(1, "What are you doing?", true, false));
        messageRepository.insertMessage(new ChatMessage(1, "Nothing. Wanna drink some coffee?", false, false));
        messageRepository.insertMessage(new ChatMessage(1, "Sure!", true, false));
        messageRepository.insertMessage(new ChatMessage(1, "When? Where?", true, false));
        messageRepository.insertMessage(new ChatMessage(1, "At 3? Ducks Coffee?", false, false));
        messageRepository.insertMessage(new ChatMessage(1, "You are late!!", false, false));
        messageRepository.insertMessage(new ChatMessage(1, "-.-", false, false));
        messageRepository.insertMessage(new ChatMessage(1, "Sorry, the tram is late...", true, false));
        messageRepository.insertMessage(new ChatMessage(1, "I'll buy you some cake :D", true, false));
        messageRepository.insertMessage(new ChatMessage(1, "Hmmm. ok...", false, false));
        messageRepository.insertMessage(new ChatMessage(1, "It was a nice day with you :)", false, false));
        messageRepository.insertMessage(new ChatMessage(1, "We definitely have to do this again!", true, false));
        messageRepository.insertMessage(new ChatMessage(1, "On Saturday?", false, false));
        messageRepository.insertMessage(new ChatMessage(1, "Sure! Same time, same place?", true, false));
        messageRepository.insertMessage(new ChatMessage(1, "Perfect. See you then. :)", false, false));
        messageRepository.insertMessage(new ChatMessage(1, "See you :D", true, false));
        messageRepository.insertMessage(new ChatMessage(1, "I think I forgot my jacket in your flat", true, false));
        messageRepository.insertMessage(new ChatMessage(1, "Can you bring it on Saturday?", true, false));
        messageRepository.insertMessage(new ChatMessage(1, "I hope I won't forget ;)", false, false));
        messageRepository.insertMessage(new ChatMessage(1, "Send me a reminder please xD", false, false));
        messageRepository.insertMessage(new ChatMessage(1, "OK :D", true, false));
        messageRepository.insertMessage(new ChatMessage(1, ":P", true, false));
        messageRepository.insertMessage(new ChatMessage(1, ":)", true, false));

        onView(withId(R.id.rv_chat)).perform(RecyclerViewActions.scrollToPosition(20));
        onView(withId(R.id.rv_chat)).perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.rv_chat)).perform(RecyclerViewActions.scrollToPosition(20));
        onView(withId(R.id.rv_chat)).perform(RecyclerViewActions.scrollToPosition(0));
    }

    @Test
    public void testChatHistoryOnMessageSend() throws InterruptedException {
        String testText = "Hello, I am a test message. ;-)";
        onView(withId(R.id.txt_chat_entry)).perform(typeText(testText), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());
        provider.getThread().join();

        int listLength = 0;
        while (listLength == 0) {
            listLength = messageRepository.getRawMessagesByUserId(1).size();
            Thread.sleep(100);
        }
        ChatMessage receiveMessage = messageRepository.getRawMessagesByUserId(1).get(listLength - 1);
        assertEquals(testText, receiveMessage.getMessageText());
    }


    @Test
    public void testTimestampVisibleOnSend() throws InterruptedException {
        String testText = "Hello, I is there a timestamp?";
        onView(withId(R.id.txt_chat_entry)).perform(typeText(testText), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());
        provider.getThread().join();

        onView(withId(R.id.txt_chat_receivedMessage)).check(matches(isDisplayed()));
    }

    @Test
    public void testGetMessageInEntryTextOnLongClick() throws InterruptedException {
        String testText = "Hello, please edit me!";
        messageRepository.insertMessage(new ChatMessage(1, testText, true, false));
        onView(withId(R.id.txt_chat_entry)).perform(typeText("hallo"), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());
        provider.getThread().join();

        onView(withText(testText)).perform(longClick());
        onView(withId(R.id.txt_chat_entry)).check(matches(withText(testText)));
    }

    @Test
    public void testEditMessage() throws InterruptedException {
        String testText = "Hello, please edit me!";

        messageRepository.insertMessage(new ChatMessage(1, testText, true, false));
        onView(withId(R.id.txt_chat_entry)).perform(typeText("hallo"), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());
        provider.getThread().join();

        String editText = "I'm edited!";

        onView(withText(testText)).perform(longClick());
        EditText textEntry = mainActivityTestRule.getActivity().getChatFragment().getTextEntry();
        textEntry.setText("");
        activity.getChatFragment().clearTextEntry();

        onView(withId(R.id.txt_chat_entry)).perform(typeText(editText), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_editSend)).perform(click());
        provider.getThread().join();

        ChatMessage receiveMessage = messageRepository.getRawMessagesByUserId(1).get(0);
        assertEquals(editText, receiveMessage.getMessageText());
    }

    @Test
    public void testReceivedEditedMessage() throws InterruptedException, ExecutionException {
        String testText = "Hello, please edit me!";
        ChatMessage testMessage = new ChatMessage(1, testText, false, false);

        String uuid = testMessage.getMessageUUID();

        messageRepository.insertMessage(testMessage).get();
        String editText = "I'm edited!";
        ChatMessage editedMessage = new ChatMessage(testMessage);
        editedMessage.setMessageText(editText);
        editedMessage.setMessageUUID(uuid);
        editedMessage.setMessageEdited(true);
        provider.sendMessage(editedMessage);
        Thread.sleep(100);

        ChatMessage receiveMessage = messageRepository.getRawMessagesByUserId(1).get(0);
        assertEquals(editText, receiveMessage.getMessageText());
    }

    @Test
    public void testAbortAndEditButtonVisible() throws Exception {
        String testText = "Hello, please edit me!";

        messageRepository.insertMessage(new ChatMessage(1, testText, true, false));
        onView(withId(R.id.txt_chat_entry)).perform(typeText("hallo"), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());
        provider.getThread().join();

        onView(withText(testText)).perform(longClick());
        onView(withId(R.id.btn_chat_editAbort)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_chat_editSend)).check(matches(isDisplayed()));
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
        onView(withId(R.id.btn_chat_emojiKeyboard)).check(matches(isDisplayed()));
    }

    @Test
    public void testIfEmojiKeyboardIsShown() throws InterruptedException {
        assertFalse(activity.getChatFragment().isEmojiKeyboardShowing());
        onView(withId(R.id.btn_chat_emojiKeyboard)).perform(click());
        Thread.sleep(300);
        assertTrue(activity.getChatFragment().isEmojiKeyboardShowing());
        onView(withId(R.id.btn_chat_emojiKeyboard)).perform(click());
        Thread.sleep(300);
        assertFalse(activity.getChatFragment().isEmojiKeyboardShowing());
    }


    @Test
    public void testEditedIndicatorVisibleOnEdit() throws InterruptedException {
        String testText = "Hello, please edit me!";

        messageRepository.insertMessage(new ChatMessage(1, testText, true, false));
        String editText = "I'm done!";
        sleep(200);
        onView(withText(testText)).perform(longClick());
        EditText textEntry = mainActivityTestRule.getActivity().getChatFragment().getTextEntry();
        textEntry.setText("");
        activity.getChatFragment().clearTextEntry();

        onView(withId(R.id.txt_chat_entry)).perform(typeText(editText), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_editSend)).perform(click());
        provider.getThread().join();


        onView(withId(R.id.txt_message_messageEdited)).check(matches(withText("edited")));
    }

    @Test
    public void testTimestampUpdatedOnEdit() throws InterruptedException {
        Format dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String testText = "Hello, please edit me!";
        ChatMessage firstMessage = new ChatMessage(1, testText, true, false);
        long currentTimestamp = firstMessage.getTimestamp() - 1000000;

        firstMessage.setTimestamp(currentTimestamp);
        messageRepository.insertMessage(firstMessage);
        String editText = "I'm done!";

        sleep(200);
        onView(withText(testText)).perform(longClick());
        EditText textEntry = mainActivityTestRule.getActivity().getChatFragment().getTextEntry();
        textEntry.setText("");
        activity.getChatFragment().clearTextEntry();

        onView(withId(R.id.txt_chat_entry)).perform(typeText(editText), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_editSend)).perform(click());
        provider.getThread().join();

        onView(withId(R.id.txt_message_messageTime)).check(matches(not((withText(dateFormat.format(currentTimestamp))))));
    }
}
