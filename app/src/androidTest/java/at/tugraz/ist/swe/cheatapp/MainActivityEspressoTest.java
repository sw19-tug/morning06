package at.tugraz.ist.swe.cheatapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
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
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.core.StringEndsWith.endsWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityEspressoTest {

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

    private MainActivity activity;

    @Before
    public void setUp() {
        activity = mainActivityTestRule.getActivity();
        activity.showConnectFragment();
    }

    @Test
    public void testMenuItemsVisible() {
        openActionBarOverflowOrOptionsMenu(activity.getApplicationContext());
        onView(withText(R.string.menu_set_nickname)).check(matches(isDisplayed()));
    }

    @Test
    public void testSetNicknameDialogVisible() {
        openActionBarOverflowOrOptionsMenu(activity.getApplicationContext());
        onView(withText(R.string.menu_set_nickname)).check(matches(isDisplayed()));
        onView(withText(R.string.menu_set_nickname)).perform(click()); // TODO: This only works on a physical Android 9 device with Animations disabled
        onView(withId(android.R.id.button1)).check(matches(isDisplayed())); // positive button (save)
        onView(withId(android.R.id.button2)).check(matches(isDisplayed())); // negative button (cancel)
    }

    @Test
    public void testSetNicknameDialogSave() {
        for(int i = 0; i <= 1; i++) {
            String testNickname = "Nickname " + i;
            openActionBarOverflowOrOptionsMenu(activity.getApplicationContext());
            onView(withText(R.string.menu_set_nickname)).perform(click());
            onView(withClassName(endsWith("EditText"))).perform(replaceText(testNickname));
            onView(withId(android.R.id.button1)).perform(click());

            openActionBarOverflowOrOptionsMenu(activity.getApplicationContext());
            onView(withText(R.string.menu_set_nickname)).perform(click());
            onView(withText(testNickname)).check(matches(isDisplayed()));
            onView(withId(android.R.id.button2)).perform(click());
        }
    }

    @Test
    public void testNicknameTruncation() {
        String testNickname = "Too looooooong Nickname!!!";
        String truncatedNickname = "Too looooooong Nickname!!";
        openActionBarOverflowOrOptionsMenu(activity.getApplicationContext());
        onView(withText(R.string.menu_set_nickname)).perform(click());
        onView(withClassName(endsWith("EditText"))).perform(replaceText(testNickname));
        onView(withId(android.R.id.button1)).perform(click());

        openActionBarOverflowOrOptionsMenu(activity.getApplicationContext());
        onView(withText(R.string.menu_set_nickname)).perform(click());
        onView(withText(truncatedNickname)).check(matches(isDisplayed()));
        onView(withId(android.R.id.button2)).perform(click());
    }

    @Test
    public void testSetNicknameDialogCancel() {
        openActionBarOverflowOrOptionsMenu(activity.getApplicationContext());
        onView(withText(R.string.menu_set_nickname)).perform(click());
        onView(withId(android.R.id.button2)).perform(click());
    }

    @Test
    public void testNicknameSetInSharedPreferences() {
        String testNickname = "Nickname Test";
        openActionBarOverflowOrOptionsMenu(activity.getApplicationContext());
        onView(withText(R.string.menu_set_nickname)).perform(click());
        onView(withClassName(endsWith("EditText"))).perform(replaceText(testNickname));
        onView(withId(android.R.id.button1)).perform(click());

        SharedPreferences sharedPreferences =
                mainActivityTestRule.getActivity().getSharedPreferences("CheatAppSharedPreferences", Context.MODE_PRIVATE);
        String savedNickname = sharedPreferences.getString("nickname", "");
        assertEquals(testNickname, savedNickname);
    }
}
