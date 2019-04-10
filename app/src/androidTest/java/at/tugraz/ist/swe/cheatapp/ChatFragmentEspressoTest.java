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

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ChatFragmentEspressoTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    private MessageRepository messageRepository;

    @Before
    public void setUp() {
        try {
            mainActivityTestRule.getActivity().showChatFragment();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
    public void buttonVisible() {
        onView(withId(R.id.btn_chat_send)).check(matches(isDisplayed()));
    }

    @Test
    public void clearTextField() {
        String testText = "This text is redundant because it will be cleared anyway ;-)";
        onView(withId(R.id.txt_chat_entry)).perform(typeText(testText), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());
        onView(withId(R.id.txt_chat_entry)).check(matches(withText("")));
    }

    @Test
    public void saveTextIntoDummyDevice() {
        String testText = "Hello World";
        ChatFragment chatFragment = mainActivityTestRule.getActivity().getChatFragment();
        DummyDevice device = new DummyDevice("1", chatFragment);

        mainActivityTestRule.getActivity().setDevice(device);

        onView(withId(R.id.txt_chat_entry)).perform(typeText(testText), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());

        assertEquals(testText, device.getMessage());
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

        DummyDevice device = new DummyDevice("1");
        mainActivityTestRule.getActivity().setDevice(device);

        onView(withId(R.id.rvChat)).perform(RecyclerViewActions.scrollToPosition(20));
        sleep(1000);
        onView(withId(R.id.rvChat)).perform(RecyclerViewActions.scrollToPosition(0));
        sleep(1000);
        onView(withId(R.id.rvChat)).perform(RecyclerViewActions.scrollToPosition(20));
        sleep(1000);
        onView(withId(R.id.rvChat)).perform(RecyclerViewActions.scrollToPosition(0));
    }

    @Test
    public void testChatHistoryOnMessageSend() {
        Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        DatabaseIntegrationTest db = new DatabaseIntegrationTest();
        db.deleteDatabase(context);

        String testText = "Hello, I am a test message. ;-)";
        onView(withId(R.id.txt_chat_entry)).perform(typeText(testText), closeSoftKeyboard());
        onView(withId(R.id.btn_chat_send)).perform(click());

        messageRepository = new MessageRepository(mainActivityTestRule.getActivity().getApplicationContext());

        Message receiveMessage = messageRepository.getRawMessagesByUserId(1).get(0);
        assertEquals(testText, receiveMessage.getMessageText());
    }

}
