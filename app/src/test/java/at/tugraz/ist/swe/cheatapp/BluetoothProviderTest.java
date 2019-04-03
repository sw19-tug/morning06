package at.tugraz.ist.swe.cheatapp;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BluetoothProviderTest {
    private DummyBluetoothProvider bluetoothProvider;

    @Before
    public void setUp() {
        bluetoothProvider = new DummyBluetoothProvider();
    }

    @Test
    public void testWithoutPairedDevices() {
        assertEquals(0L, bluetoothProvider.getPairedDevices().size());
    }

    @Test
    public void testWithPairedDevices() {
        bluetoothProvider.enableDummyDevices(5);
        assertEquals(5L, bluetoothProvider.getPairedDevices().size());
    }

    @Test
    public void sendMessage() {
        String message = "Test Message";
        bluetoothProvider.sendMessage(message);
        assertEquals(message, bluetoothProvider.checkSendMessage());
    }
    @Test
    public void disconnectFromDevice() {
        bluetoothProvider.disconnect();
        assertFalse(bluetoothProvider.isConnected());
    }
}