package at.tugraz.ist.swe.cheatapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringEndsWith.endsWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityEspressoTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        mainActivityTestRule.getActivity().showConnectFragment();
    }

    @Test
    public void testMenuItemsVisible() {
        openActionBarOverflowOrOptionsMenu(mainActivityTestRule.getActivity().getApplicationContext());
        onView(withText(R.string.menu_set_nickname)).check(matches(isDisplayed()));
    }

    @Test
    public void testSetNicknameDialogVisible() {
        openActionBarOverflowOrOptionsMenu(mainActivityTestRule.getActivity().getApplicationContext());
        onView(withText(R.string.menu_set_nickname)).check(matches(isDisplayed()));
        onView(withText(R.string.menu_set_nickname)).perform(click()); // TODO: This only works on a physical Android 9 device with Animations disabled
        onView(withId(android.R.id.button1)).check(matches(isDisplayed())); // positive button (save)
        onView(withId(android.R.id.button2)).check(matches(isDisplayed())); // negative button (cancel)
    }

    @Test
    public void testSetNicknameDialogSave() {
        for(int i = 0; i <= 1; i++) {
            String testNickname = "Nickname " + i;
            openActionBarOverflowOrOptionsMenu(mainActivityTestRule.getActivity().getApplicationContext());
            onView(withText(R.string.menu_set_nickname)).perform(click());
            onView(withClassName(endsWith("EditText"))).perform(replaceText(testNickname));
            onView(withId(android.R.id.button1)).perform(click());

            openActionBarOverflowOrOptionsMenu(mainActivityTestRule.getActivity().getApplicationContext());
            onView(withText(R.string.menu_set_nickname)).perform(click());
            onView(withText(testNickname)).check(matches(isDisplayed()));
            onView(withId(android.R.id.button2)).perform(click());
        }
    }

    @Test
    public void testNicknameTruncation() {
        String testNickname = "Too looooooong Nickname!!!";
        String truncatedNickname = "Too looooooong Nickname!!";
        openActionBarOverflowOrOptionsMenu(mainActivityTestRule.getActivity().getApplicationContext());
        onView(withText(R.string.menu_set_nickname)).perform(click());
        onView(withClassName(endsWith("EditText"))).perform(replaceText(testNickname));
        onView(withId(android.R.id.button1)).perform(click());

        openActionBarOverflowOrOptionsMenu(mainActivityTestRule.getActivity().getApplicationContext());
        onView(withText(R.string.menu_set_nickname)).perform(click());
        onView(withText(truncatedNickname)).check(matches(isDisplayed()));
        onView(withId(android.R.id.button2)).perform(click());
    }

    @Test
    public void testSetNicknameDialogCancel() {
        openActionBarOverflowOrOptionsMenu(mainActivityTestRule.getActivity().getApplicationContext());
        onView(withText(R.string.menu_set_nickname)).perform(click());
        onView(withId(android.R.id.button2)).perform(click());
    }
}
