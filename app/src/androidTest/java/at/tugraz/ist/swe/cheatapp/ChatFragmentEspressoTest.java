package at.tugraz.ist.swe.cheatapp;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.internal.util.Checks;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnHolderItem;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.hasBackground;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.lang.Thread.sleep;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ChatFragmentEspressoTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    private MessageRepository messageRepository;

    @Before
    public void setUp() throws InterruptedException {
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
        String testText = "I'm a dummy test!";

        DummyBluetoothProvider provider = new DummyBluetoothProvider();
        provider.enableDummyDevices(1);
        mainActivityTestRule.getActivity().setBluetoothProvider(provider);

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
    public void chatHistoryScrollable() throws InterruptedException {
        Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        DatabaseIntegrationTest db = new DatabaseIntegrationTest();
        db.deleteDatabase(context);

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
        sleep(1000);
        onView(withId(R.id.rvChat)).perform(RecyclerViewActions.scrollToPosition(0));
        sleep(1000);
        onView(withId(R.id.rvChat)).perform(RecyclerViewActions.scrollToPosition(20));
        sleep(1000);
        onView(withId(R.id.rvChat)).perform(RecyclerViewActions.scrollToPosition(0));
    }

    @Test
    public void testChatHistoryOnMessageSend() throws InterruptedException {

        DummyBluetoothProvider provider = new DummyBluetoothProvider();
        provider.enableDummyDevices(1);
        mainActivityTestRule.getActivity().setBluetoothProvider(provider);

        Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        DatabaseIntegrationTest db = new DatabaseIntegrationTest();
        db.deleteDatabase(context);

        String testText = "Hello, I am a test message. ;-)";
        onView(withId(R.id.txt_chat_entry)).perform(typeText(testText), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());
        provider.getThread().join();

        messageRepository = new MessageRepository(mainActivityTestRule.getActivity().getApplicationContext());

        Message receiveMessage = messageRepository.getRawMessagesByUserId(1).get(0);
        assertEquals(testText, receiveMessage.getMessageText());
    }


    @Test
    public void timestampVisibleOnSend() throws InterruptedException {
        DummyBluetoothProvider provider = new DummyBluetoothProvider();
        provider.enableDummyDevices(1);
        mainActivityTestRule.getActivity().setBluetoothProvider(provider);

        Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        DatabaseIntegrationTest db = new DatabaseIntegrationTest();
        db.deleteDatabase(context);

        String testText = "Hello, I is there a timestamp?";
        onView(withId(R.id.txt_chat_entry)).perform(typeText(testText), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());
        provider.getThread().join();

        onView(withId(R.id.txt_chat_receivedMessage)).check(matches(isDisplayed()));
    }

    @Test
    public void testGetMessageInEntryTextOnLongClick() throws InterruptedException {
        DummyBluetoothProvider provider = new DummyBluetoothProvider();
        provider.enableDummyDevices(1);
        mainActivityTestRule.getActivity().setBluetoothProvider(provider);

        Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        DatabaseIntegrationTest db = new DatabaseIntegrationTest();
        db.deleteDatabase(context);

        String testText = "Hello, please edit me!";
        messageRepository = new MessageRepository(mainActivityTestRule.getActivity().getApplicationContext());

        messageRepository.insertMessage(new Message(1, testText, true));
        onView(withId(R.id.txt_chat_entry)).perform(typeText("hallo"), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());
        provider.getThread().join();

        onView(withText(testText)).perform(longClick());
        onView(withId(R.id.txt_chat_entry)).check(matches(withText(testText)));
    }

    @Test
    public void testEditMessage() throws InterruptedException {
        DummyBluetoothProvider provider = new DummyBluetoothProvider();
        provider.enableDummyDevices(1);
        mainActivityTestRule.getActivity().setBluetoothProvider(provider);

        Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        DatabaseIntegrationTest db = new DatabaseIntegrationTest();
        db.deleteDatabase(context);
        String testText = "Hello, please edit me!";

        messageRepository = new MessageRepository(mainActivityTestRule.getActivity().getApplicationContext());

        messageRepository.insertMessage(new Message(1, testText, true));
        onView(withId(R.id.txt_chat_entry)).perform(typeText("hallo"), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());
        provider.getThread().join();

        String editText = "I'm edited!";

        onView(withText(testText)).perform(longClick());

        EditText textEntry = mainActivityTestRule.getActivity().getChatFragment().getTextEntry();
        textEntry.setText("");

        onView(withId(R.id.txt_chat_entry)).perform(typeText(editText), closeSoftKeyboard());
        onView(withId(R.id.btn_edit_send)).perform(click());
        provider.getThread().join();


        Message receiveMessage = messageRepository.getRawMessagesByUserId(1).get(0);
        assertEquals(editText, receiveMessage.getMessageText());

    }

    @Test
    public void testAbortAndEditButtonVisible() throws Exception{
        DummyBluetoothProvider provider = new DummyBluetoothProvider();
        provider.enableDummyDevices(1);
        mainActivityTestRule.getActivity().setBluetoothProvider(provider);

        Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        DatabaseIntegrationTest db = new DatabaseIntegrationTest();
        db.deleteDatabase(context);

        String testText = "Hello, please edit me!";
        messageRepository = new MessageRepository(mainActivityTestRule.getActivity().getApplicationContext());

        messageRepository.insertMessage(new Message(1, testText, true));
        onView(withId(R.id.txt_chat_entry)).perform(typeText("hallo"), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());
        provider.getThread().join();

        onView(withText(testText)).perform(longClick());
        onView(withId(R.id.btn_abort_edit)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_edit_send)).check(matches(isDisplayed()));

    }

}
