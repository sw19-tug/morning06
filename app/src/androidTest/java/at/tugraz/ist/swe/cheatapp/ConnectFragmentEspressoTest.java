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

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.is;
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
        activity.showConnectFragment();
        provider = (DummyBluetoothProvider) activity.getBluetoothProvider();
    }

    @Test
    public void testButtonsVisible() {
        onView(withId(R.id.btn_con_disconnect)).check(matches(isDisplayed()));
    }

    @Test
    public void testListVisible() {
        onView(withId(R.id.lv_con_devices)).check(matches(isDisplayed()));
    }

    @Test
    public void testSelectListElementAndConnect() throws InterruptedException {
        onData(allOf(is(instanceOf(String.class)), is("1")))
                .perform(click());

        onView(withId(R.id.btn_con_disconnect)).perform(click());
        provider.getThread().join();

        assertTrue(provider.isConnected());
        assertEquals(provider.getConnectedDevice().getDeviceId(), 1);
        assertEquals(provider.getConnectedDevice().getDeviceName(), "1");
    }

    @Test
    public void testConnectWithoutSelection() {
        onView(withId(R.id.btn_con_disconnect)).perform(click());

        assertFalse(provider.isConnected());
        assertNull(provider.getConnectedDevice());
    }

    @Test
    public void testConnectWithSelection() throws InterruptedException {
        onData(allOf(is(instanceOf(String.class)), is("1")))
                .perform(click());

        onView(withId(R.id.btn_con_disconnect)).perform(click());
        provider.getThread().join();

        assertTrue(provider.isConnected());
        assertEquals(provider.getConnectedDevice().getDeviceId(), 1);
        assertEquals(provider.getConnectedDevice().getDeviceName(), "1");
    }

    @Test
    public void testChangeViewOnConnect() throws InterruptedException {
        onData(allOf(is(instanceOf(String.class)), is("1")))
                .perform(click());
        onView(withId(R.id.btn_con_disconnect)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_con_disconnect)).perform(click());
        provider.getThread().join();

        onView(withId(R.id.btn_chat_send)).check(matches(isDisplayed()));
    }

    @Test
    public void testChangeViewOnDisconnect() throws InterruptedException {
        onData(allOf(is(instanceOf(String.class)), is("1")))
                .perform(click());
        onView(withId(R.id.btn_con_disconnect)).perform(click());
        provider.getThread().join();

        onView(withId(R.id.btn_con_disconnect)).perform(click());
        provider.getThread().join();

        onView(withId(R.id.btn_con_disconnect)).check(matches(isDisplayed()));
    }

    @Test
    public void testDisconnect() throws InterruptedException {
        onData(allOf(is(instanceOf(String.class)), is("1")))
                .perform(click());

        onView(withId(R.id.btn_con_disconnect)).perform(click());
        provider.getThread().join();

        onView(withId(R.id.btn_con_disconnect)).perform(click());
        provider.getThread().join();

        assertFalse(provider.isConnected());
        assertNull(provider.getConnectedDevice());

        onData(instanceOf(String.class)).inAdapterView(withId(R.id.lv_con_devices))
                .atPosition(0).check(matches(withText("1")));
    }

    @Test
    public void testRefreshOnSwipe() throws InterruptedException {
        onView(withId(R.id.swp_con_pullToRefresh)).check(matches(isDisplayed()));

        Thread.sleep(500);
        int count = activity.getListView().getAdapter().getCount();
        assertEquals(count, 1);

        provider.setNumberOfEnabledDummyDevices(2);

        onView(withId(R.id.lv_con_devices)).perform(swipeDown());
        Thread.sleep(500);
        count = activity.getListView().getAdapter().getCount();

        assertEquals(count, 2);
    }

    @Test
    public void testConnectedDeviceSavedInSharedPreferences() throws InterruptedException {
        onData(allOf(is(instanceOf(String.class)), is("1")))
                .perform(click());
        onView(withId(R.id.btn_con_disconnect)).perform(click());
        provider.getThread().join();
        Thread.sleep(100);
        SharedPreferences sharedPreferences =
                activity.getSharedPreferences("CheatAppSharedPreferences", Context.MODE_PRIVATE);
        long lastConnectedDeviceID = sharedPreferences.getLong("lastConDev", 0);

        assertEquals(1, lastConnectedDeviceID);
    }

    @Test
    public void testReconnectDevice() throws InterruptedException {
        SharedPreferences.Editor preferencesEditor =
                activity.getSharedPreferences("CheatAppSharedPreferences", Context.MODE_PRIVATE).edit();
        preferencesEditor.putLong("lastConDev", 123456);
        preferencesEditor.commit();

        activity.restartApp();
        Thread.sleep(1000);

        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText("Try to reconnect...")))
                .check(matches(isDisplayed()));
    }
}
