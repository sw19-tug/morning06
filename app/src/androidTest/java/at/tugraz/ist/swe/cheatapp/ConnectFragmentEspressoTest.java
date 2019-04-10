package at.tugraz.ist.swe.cheatapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
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
public class ConnectFragmentEspressoTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        mainActivityTestRule.getActivity().showConnectFragment();
    }

    @Test
    public void testButtonsVisible() {
        onView(withId(R.id.btn_con_connect)).check(matches(isDisplayed()));

    }

    @Test
    public void testListVisible() {
        onView(withId(R.id.lv_con_devices)).check(matches(isDisplayed()));
    }

    @Test
    public void testSelectListElementAndConnect() {
        DummyBluetoothProvider provider = new DummyBluetoothProvider();
        provider.enableDummyDevices(1);

        mainActivityTestRule.getActivity().setBluetoothProvider(provider);

        onData(allOf(is(instanceOf(String.class)), is("0")))
                .perform(click());

        onView(withId(R.id.btn_con_connect)).perform(click());

        assertTrue(provider.isConnected());
        assertEquals(provider.getConnectedDevice().getID(), "0");
    }

    @Test
    public void testConnectWithoutSelection() {
        DummyBluetoothProvider provider = new DummyBluetoothProvider();
        provider.enableDummyDevices(1);

        mainActivityTestRule.getActivity().setBluetoothProvider(provider);

        onView(withId(R.id.btn_con_connect)).perform(click());

        assertFalse(provider.isConnected());
        assertNull(provider.getConnectedDevice());
    }

    @Test
    public void testConnectWithSelection() {
        DummyBluetoothProvider provider = new DummyBluetoothProvider();
        provider.enableDummyDevices(1);

        mainActivityTestRule.getActivity().setBluetoothProvider(provider);

        onData(allOf(is(instanceOf(String.class)), is("0")))
                .perform(click());

        onView(withId(R.id.btn_con_connect)).perform(click());

        assertTrue(provider.isConnected());
        assertEquals(provider.getConnectedDevice().getID(), "0");
    }

    @Test
    public void testChangeViewOnConnect() {
        DummyBluetoothProvider provider = new DummyBluetoothProvider();
        provider.enableDummyDevices(1);

        mainActivityTestRule.getActivity().setBluetoothProvider(provider);

        onData(allOf(is(instanceOf(String.class)), is("0")))
                .perform(click());
        onView(withId(R.id.btn_con_connect)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_con_connect)).perform(click());

        onView(withId(R.id.btn_chat_send)).check(matches(isDisplayed()));
    }

    @Test
    public void testChangeViewOnDisconnect() {
        DummyBluetoothProvider provider = new DummyBluetoothProvider();
        provider.enableDummyDevices(1);

        mainActivityTestRule.getActivity().setBluetoothProvider(provider);

        onData(allOf(is(instanceOf(String.class)), is("0")))
                .perform(click());
        onView(withId(R.id.btn_con_connect)).perform(click());
        onView(withId(R.id.btn_chat_disconnect)).perform(click());

        onView(withId(R.id.btn_con_connect)).check(matches(isDisplayed()));

    }
}
