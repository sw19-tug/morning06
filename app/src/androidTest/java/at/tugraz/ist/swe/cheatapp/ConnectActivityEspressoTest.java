package at.tugraz.ist.swe.cheatapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ConnectActivityEspressoTest {
    @Rule
    public ActivityTestRule<ConnectActivity> mainActivityTestRule = new ActivityTestRule<>(ConnectActivity.class);

    @Test
    public void testButtonsVisible()
    {
        onView(withId(R.id.bt_con_connect)).check(matches(isDisplayed()));
        onView(withId(R.id.bt_con_enable_discoverability)).check(matches(isDisplayed()));

    }

    @Test
    public void testListVisible() {
        onView(withId(R.id.lv_con_devices)).check(matches(isDisplayed()));
    }
}
