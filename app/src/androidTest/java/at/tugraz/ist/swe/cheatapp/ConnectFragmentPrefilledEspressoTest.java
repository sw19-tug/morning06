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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ConnectFragmentPrefilledEspressoTest {
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
    private DummyBluetoothProvider provider;

    @Before
    public void setUp() {
        activity = mainActivityTestRule.getActivity();
        provider = (DummyBluetoothProvider) activity.getBluetoothProvider();

        SharedPreferences.Editor prefrencesEditor =
                activity.getSharedPreferences("CheatAppSharedPreferences", Context.MODE_PRIVATE).edit();
        prefrencesEditor.putLong("lastConDev", 1);
        prefrencesEditor.commit();
        activity.showConnectFragment();
    }

    @Test
    public void testReconnectWithSavedDevice() throws InterruptedException {
        ConnectFragment fragment = activity.getConnectFragment();

        SharedPreferences sharedPreferences =
                activity.getSharedPreferences("CheatAppSharedPreferences", Context.MODE_PRIVATE);
        long lastConnectedDeviceID = sharedPreferences.getLong("lastConDev", 0);

        fragment.tryReconnectByDeviceId(lastConnectedDeviceID);
        provider.getThread().join();

        onView(withId(R.id.btn_chat_send)).check(matches(isDisplayed()));
    }
}
