package at.tugraz.ist.swe.cheatapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static at.tugraz.ist.swe.cheatapp.Constants.ON_CONNECTED_MESSAGE;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ConnectFragmentPrefilledEspressoTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        SharedPreferences.Editor prefrencesEditor =
                mainActivityTestRule.getActivity().getSharedPreferences("CheatAppSharedPreferences", Context.MODE_PRIVATE).edit();
        prefrencesEditor.clear();
        prefrencesEditor.putString("conDev","0");
        prefrencesEditor.commit();
        mainActivityTestRule.getActivity().showConnectFragment();
    }

    @After
    public void cleanUp()
    {
        SharedPreferences.Editor prefrencesEditor =
                mainActivityTestRule.getActivity().getSharedPreferences("CheatAppSharedPreferences", Context.MODE_PRIVATE).edit();
        prefrencesEditor.clear();
        prefrencesEditor.commit();
    }

    @Test
    public void testReconnectWithSavedDevice() throws InterruptedException {

        DummyBluetoothProvider provider = new DummyBluetoothProvider();
        provider.enableDummyDevices(1);
        mainActivityTestRule.getActivity().setBluetoothProvider(provider);

        ConnectFragment fragment = mainActivityTestRule.getActivity().getConnectFragment();

        SharedPreferences sharedPreferences =
                mainActivityTestRule.getActivity().getSharedPreferences("CheatAppSharedPreferences", Context.MODE_PRIVATE);
        String lastConnectedDeviceName = sharedPreferences.getString("conDev", "Fail");

        fragment.tryConnectByDeviceName(lastConnectedDeviceName);
        provider.getThread().join();

        onView(withId(R.id.btn_chat_send)).check(matches(isDisplayed()));
    }
}
