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

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
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
    DummyBluetoothProvider provider;

    @Before
    public void setUp() throws InterruptedException {
        mainActivityTestRule.getActivity().showConnectFragment();
        provider = new DummyBluetoothProvider();
        provider.enableDummyDevices(1);

        mainActivityTestRule.getActivity().setBluetoothProvider(provider, true);
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
    public void testButtonsVisible() {
        onView(withId(R.id.btn_connect_disconnect)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_connect_disconnect)).check(matches(withText(R.string.connect)));
    }

    @Test
    public void testListVisible() {
        onView(withId(R.id.lv_con_devices)).check(matches(isDisplayed()));
    }

    @Test
    public void testSelectListElementAndConnect() throws InterruptedException {
        onData(allOf(is(instanceOf(String.class)), is("1")))
                .perform(click());

        onView(withId(R.id.btn_connect_disconnect)).perform(click());
        provider.getThread().join();

        assertTrue(provider.isConnected());
        assertEquals(provider.getConnectedDevice().getDeviceId(), 1);
        assertEquals(provider.getConnectedDevice().getDeviceName(), "1");
    }

    @Test
    public void testConnectWithoutSelection() throws InterruptedException {
        onView(withId(R.id.btn_connect_disconnect)).perform(click());

        assertFalse(provider.isConnected());
        assertNull(provider.getConnectedDevice());
    }

    @Test
    public void testConnectWithSelection() throws InterruptedException {
        onData(allOf(is(instanceOf(String.class)), is("1")))
                .perform(click());

        onView(withId(R.id.btn_connect_disconnect)).perform(click());
        provider.getThread().join();

        assertTrue(provider.isConnected());
        assertEquals(provider.getConnectedDevice().getDeviceId(), 1);
        assertEquals(provider.getConnectedDevice().getDeviceName(), "1");
    }

    @Test
    public void testChangeViewOnConnect() throws InterruptedException {
        onData(allOf(is(instanceOf(String.class)), is("1")))
                .perform(click());
        onView(withId(R.id.btn_connect_disconnect)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_connect_disconnect)).perform(click());
        provider.getThread().join();

        onView(withId(R.id.btn_chat_send)).check(matches(isDisplayed()));
    }

    @Test
    public void testChangeViewOnDisconnect() throws InterruptedException {
        onData(allOf(is(instanceOf(String.class)), is("1")))
                .perform(click());
        onView(withId(R.id.btn_connect_disconnect)).perform(click());
        provider.getThread().join();

        onView(withId(R.id.btn_connect_disconnect)).perform(click());
        provider.getThread().join();

        onView(withId(R.id.btn_connect_disconnect)).check(matches(isDisplayed()));
    }

    @Test
    public void testDisconnect() throws InterruptedException {
        onData(allOf(is(instanceOf(String.class)), is("1")))
                .perform(click());

        onView(withId(R.id.btn_connect_disconnect)).perform(click());
        provider.getThread().join();

        onView(withId(R.id.btn_connect_disconnect)).perform(click());
        provider.getThread().join();

        assertFalse(provider.isConnected());
        assertNull(provider.getConnectedDevice());

        onData(instanceOf(String.class)).inAdapterView(withId(R.id.lv_con_devices))
                .atPosition(0).check(matches(withText("1")));
    }

    @Test
    public void testHandshakeMessageAfterConnect() throws InterruptedException {
        onData(allOf(is(instanceOf(String.class)), is("1")))
                .perform(click());
        onView(withId(R.id.btn_connect_disconnect)).perform(click());
        provider.getThread().join();

        onView(withText(R.string.connected))
                .inRoot(withDecorView(not(is(mainActivityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testHandshakeMessageAfterDisconnect() throws InterruptedException {
        DummyBluetoothProvider provider = new DummyBluetoothProvider();
        provider.enableDummyDevices(1);

        mainActivityTestRule.getActivity().setBluetoothProvider(provider, true);

        onData(allOf(is(instanceOf(String.class)), is("1")))
                .perform(click());

        onView(withId(R.id.btn_connect_disconnect)).perform(click());
        provider.getThread().join();

        onView(withId(R.id.btn_connect_disconnect)).perform(click());
        provider.getThread().join();

        onView(withText(R.string.disconnected))
                .inRoot(withDecorView(not(is(mainActivityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testRefreshOnSwipe() throws InterruptedException {
        onView(withId(R.id.swp_pull_to_refresh)).check(matches(isDisplayed()));

        DummyBluetoothProvider provider = new DummyBluetoothProvider();
        provider.enableDummyDevices(1);
        mainActivityTestRule.getActivity().setBluetoothProvider(provider, true);
        Thread.sleep(500);
        int count = mainActivityTestRule.getActivity().getListView().getAdapter().getCount();
        assertEquals(count, 1);

        provider.enableDummyDevices(2);
        mainActivityTestRule.getActivity().setBluetoothProvider(provider, false);

        onView(withId(R.id.lv_con_devices)).perform(swipeDown());
        Thread.sleep(500);
        count = mainActivityTestRule.getActivity().getListView().getAdapter().getCount();

        assertEquals(count, 2);
    }

    @Test
    public void testConnectedDeviceSavedInSharedPreferences() throws InterruptedException {
        onData(allOf(is(instanceOf(String.class)), is("1")))
                .perform(click());
        onView(withId(R.id.btn_connect_disconnect)).perform(click());
        provider.getThread().join();
        Thread.sleep(100);
        SharedPreferences sharedPreferences =
                mainActivityTestRule.getActivity().getSharedPreferences("CheatAppSharedPreferences", Context.MODE_PRIVATE);
        long lastConnectedDeviceID = sharedPreferences.getLong("lastConDev", 0);

        assertEquals(1, lastConnectedDeviceID);
    }
}
