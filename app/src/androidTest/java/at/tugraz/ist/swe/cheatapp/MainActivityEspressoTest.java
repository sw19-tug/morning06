package at.tugraz.ist.swe.cheatapp;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MainActivityEspressoTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

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

    @Test
    public void chatHistoryViewMessageBubbles(){
        Message messageSent = new Message(1, "Hi, how are you?", true);
        Message messageReceived = new Message(2, "I'm fine. Thanks.", false);

        List<Message> messageList = new ArrayList<>();
        messageList.add(messageSent);
        messageList.add(messageReceived);

        DummyDevice device = new DummyDevice("1");
        mainActivityTestRule.getActivity().setDevice(device);

        // TODO
        //onView(withId(R.id.rvChat))
            //    .check(matches(hasDescendant(withText("Hi, how are you?")))) ;
    }

}
