package at.tugraz.ist.swe.cheatapp;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerViewAccessibilityDelegate;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
//import android.support.test.espresso.;

@RunWith(AndroidJUnit4.class)
public class ChatFragmentEspressoTest {

    private MessageRepository messageRepository;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        mainActivityTestRule.getActivity().showChatFragment();
    }

    @Test
    public void testFieldVisible() {
        onView(withId(R.id.textEntry)).check(matches(isDisplayed()));
    }

    @Test
    public void testInput() {
        String testText = "Test_Input";
        onView(withId(R.id.textEntry)).perform(replaceText(""));
        onView(withId(R.id.textEntry)).perform(typeText(testText), closeSoftKeyboard());
        onView(withId(R.id.textEntry)).check(matches(withText(testText)));
    }

    @Test
    public void buttonVisible() {
        onView(withId(R.id.sendButton)).check(matches(isDisplayed()));
    }

    @Test
    public void clearTextField() {
        String testText = "This text is redundant because it will be cleared anyway ;-)";
        onView(withId(R.id.textEntry)).perform(typeText(testText), closeSoftKeyboard());
        onView(withId(R.id.sendButton)).perform(click());
        onView(withId(R.id.textEntry)).check(matches(withText("")));
    }

    @Test
    public void saveTextIntoDummyDevice() {
        String testText = "Hello World";
        DummyDevice device = new DummyDevice("1");

        mainActivityTestRule.getActivity().setDevice(device);

        onView(withId(R.id.textEntry)).perform(typeText(testText), closeSoftKeyboard());
        onView(withId(R.id.sendButton)).perform(click());

        assertEquals(testText, device.getMessage());
    }

    @Test
    public void chatHistoryVisible(){ onView(withId(R.id.rvChat)).check(matches(isDisplayed())); }

    private void deleteDatabase(Context appContext) {
        File databases = new File(appContext.getApplicationInfo().dataDir + "/databases");
        File db = new File(databases, "cheatapp_db");
        if (db.delete())
            System.out.println("Database deleted");
        else
            System.out.println("Failed to delete database");
    }

    @Test
    public void chatHistoryViewMessageBubbles() throws InterruptedException {
        Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        deleteDatabase(context);

        messageRepository = new MessageRepository(mainActivityTestRule.getActivity().getApplicationContext());

        messageRepository.insertMessage(new Message(1, "Hi, how are you?", true));
        messageRepository.insertMessage(new Message(1, "I'm fine. Thanks.", false));
        messageRepository.insertMessage(new Message(1, "What are you doin'?", true));
        messageRepository.insertMessage(new Message(1, "Nothing. Wanna drink some coffee?", false));
        messageRepository.insertMessage(new Message(1, "Sure!", true));
        messageRepository.insertMessage(new Message(1, "When? Where?", true));
        messageRepository.insertMessage(new Message(1, "At 3? Ducks Coffee?", false));
        messageRepository.insertMessage(new Message(1, "You are late!!", false));
        messageRepository.insertMessage(new Message(1, "-.-", false));
        messageRepository.insertMessage(new Message(1, "Sorry, the tram is late....", true));
        messageRepository.insertMessage(new Message(1, "I'll buy you some cake :D", true));
        messageRepository.insertMessage(new Message(1, "hmmm. ok...", false));
        messageRepository.insertMessage(new Message(1, "It was a nice day with you :)", false));
        messageRepository.insertMessage(new Message(1, "We definitely have to do this again!", true));
        messageRepository.insertMessage(new Message(1, "On Saturday?", false));
        messageRepository.insertMessage(new Message(1, "Sure! Same time, same place?", true));
        messageRepository.insertMessage(new Message(1, "Perfect. See you then. :)", false));
        messageRepository.insertMessage(new Message(1, "See you :D", true));

        DummyDevice device = new DummyDevice("1");
        mainActivityTestRule.getActivity().setDevice(device);


        onView(withId(R.id.rvChat)).perform(RecyclerViewActions.actionOnItemAtPosition(6, click()));

        sleep(1000);
        onView(withId(R.id.rvChat)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.rvChat)).perform(RecyclerViewActions.scrollToPosition(3));
    }

}
