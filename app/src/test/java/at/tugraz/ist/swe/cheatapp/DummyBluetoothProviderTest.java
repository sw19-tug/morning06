package at.tugraz.ist.swe.cheatapp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class DummyBluetoothProviderTest {
    private DummyBluetoothProvider bluetoothProvider;

    @Before
    public void setUp() {
        bluetoothProvider = new DummyBluetoothProvider();
    }

    @Test
    public void testWithoutPairedDevices() {
        bluetoothProvider.setNumberOfEnabledDummyDevices(0);
        assertEquals(0L, bluetoothProvider.getPairedDevices().size());
    }

    @Test
    public void testWithPairedDevices() {
        bluetoothProvider.setNumberOfEnabledDummyDevices(5);
        assertEquals(5L, bluetoothProvider.getPairedDevices().size());
    }

    @Test
    public void testAddEventHandler() {
        BluetoothEventHandler handler = new BluetoothEventHandler();

        this.bluetoothProvider.registerHandler(handler);

        List<BluetoothEventHandler> handlers = this.bluetoothProvider.getEventHandlers();

        assertEquals(handlers.size(), 1);
        assertEquals(handlers.get(0), handler);
    }

    @Test
    public void testRemoveEventHandler() {
        BluetoothEventHandler handler = new BluetoothEventHandler();

        this.bluetoothProvider.registerHandler(handler);
        this.bluetoothProvider.unregisterHandler(handler);

        List<BluetoothEventHandler> handlers = this.bluetoothProvider.getEventHandlers();

        assertEquals(handlers.size(), 0);
    }

    @Test
    public void testOnConnectedCallback() throws InterruptedException {
        BluetoothEventHandler handler = Mockito.mock(BluetoothEventHandler.class);

        this.bluetoothProvider.registerHandler(handler);
        List<Device> devices = this.bluetoothProvider.getPairedDevices();

        verify(handler, never()).onConnected();

        this.bluetoothProvider.connectToDevice(devices.get(0));
        this.bluetoothProvider.getThread().join();

        verify(handler, times(1)).onConnected();
    }

    @Test
    public void testOnMessageReceivedCallback() throws InterruptedException {
        BluetoothEventHandler handler = Mockito.mock(BluetoothEventHandler.class);

        this.bluetoothProvider.registerHandler(handler);
        List<Device> devices = this.bluetoothProvider.getPairedDevices();
        this.bluetoothProvider.connectToDevice(devices.get(0));
        this.bluetoothProvider.getThread().join();
        final ChatMessage message = new ChatMessage(0, "test", true, false);

        verify(handler, never()).onMessageReceived(message);
        this.bluetoothProvider.setReceivedMessage(message);
        verify(handler, times(1)).onMessageReceived(message);
    }

    @Test
    public void testOnDisconnectedCallback() throws InterruptedException {
        BluetoothEventHandler handler = Mockito.mock(BluetoothEventHandler.class);

        this.bluetoothProvider.registerHandler(handler);
        List<Device> devices = this.bluetoothProvider.getPairedDevices();
        this.bluetoothProvider.connectToDevice(devices.get(0));
        this.bluetoothProvider.getThread().join();

        verify(handler, never()).onDisconnected();

        this.bluetoothProvider.disconnect();
        this.bluetoothProvider.getThread().join();

        verify(handler, times(1)).onDisconnected();
    }

    @Test
    public void testOnErrorCallback() throws InterruptedException {
        BluetoothEventHandler handler = Mockito.mock(BluetoothEventHandler.class);

        this.bluetoothProvider.registerHandler(handler);
        this.bluetoothProvider.connectToDevice(null);
        this.bluetoothProvider.getThread().join();

        verify(handler, times(1)).onError("No device provided");
    }

    @Test
    public void testConnectToDevice() throws InterruptedException {
        List<Device> devices = this.bluetoothProvider.getPairedDevices();
        this.bluetoothProvider.connectToDevice(devices.get(0));
        this.bluetoothProvider.getThread().join();

        assertEquals(bluetoothProvider.getConnectedDevice().getDeviceId(), 1);
        assertEquals(bluetoothProvider.getConnectedDevice().getDeviceName(), "1");
        assertTrue(bluetoothProvider.isConnected());
    }

    @Test
    public void testDisconnectFromDevice() throws InterruptedException {
        bluetoothProvider.disconnect();
        this.bluetoothProvider.getThread().join();
        assertFalse(bluetoothProvider.isConnected());
    }

    @Test
    public void testGetUserId() throws InterruptedException {
        this.bluetoothProvider.addDummyDevice("dummy", "C0:EE:FB:D8:74:6F");
        List<Device> devices = this.bluetoothProvider.getPairedDevices();
        this.bluetoothProvider.connectToDevice(devices.get(0));
        this.bluetoothProvider.getThread().join();

        long test_id = Long.valueOf("212132660016239");
        assertEquals(test_id, this.bluetoothProvider.getConnectedDevice().getDeviceId());
        assertEquals("dummy", this.bluetoothProvider.getConnectedDevice().getDeviceName());
    }

    @Test
    public void testGetDeviceByID() {
        Device device = this.bluetoothProvider.getDeviceByID(1);

        assertNotNull(device);
        assertEquals(device.getDeviceId(), 1);
    }

    @Test
    public void testGetDeviceByIDNoDevice() {
        bluetoothProvider.setNumberOfEnabledDummyDevices(0);
        Device device = this.bluetoothProvider.getDeviceByID(1);
        assertNull(device);
    }

    @Test
    public void testNoNickname() {
        bluetoothProvider.getOwnNickname();
        assertNull(bluetoothProvider.getOwnNickname());
    }

    @Test
    public void testSetNickname() {
        bluetoothProvider.setOwnNickname("testing");
        assertEquals("testing", bluetoothProvider.getOwnNickname());
    }
}