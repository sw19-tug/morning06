package at.tugraz.ist.swe.cheatapp;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
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
    public void testInitialState() {
        assertEquals(0L, bluetoothProvider.getPairedDevices().size());
    }

    @Test
    public void testDiscoverDevicesNoneAvailable() {
        bluetoothProvider.startDiscover();
        assertEquals(0L, bluetoothProvider.getPairedDevices().size());
    }

    @Test
    public void testDiscoverDevicesAvailable() {
        bluetoothProvider.enableDummyDevices(5);
        bluetoothProvider.startDiscover();
        assertEquals(5L, bluetoothProvider.getPairedDevices().size());
    }

    @Test
    public void testIsDiscoverable() {
        bluetoothProvider.startDiscoverability();
        assertTrue(bluetoothProvider.isDiscoverable());
    }
}