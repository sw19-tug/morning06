package at.tugraz.ist.swe.cheatapp;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        assertEquals(0L, bluetoothProvider.getPairedDevices().size());
    }

    @Test
    public void testWithPairedDevices() {
        bluetoothProvider.enableDummyDevices(5);
        assertEquals(5L, bluetoothProvider.getPairedDevices().size());
    }

    @Test
    public void testAddEventHandler() {
        BluetoothEventHandler handler = new BluetoothEventHandler() {
            @Override
            public void onMessageReceived(String message) {

            }

            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onError(String errorMsg) {

            }
        };

        this.bluetoothProvider.registerHandler(handler);

        List<BluetoothEventHandler> handlers = this.bluetoothProvider.getEventHandlers();

        assertEquals(handlers.size(), 1);
        assertEquals(handlers.get(0), handler);
    }

    @Test
    public void testRemoveEventHandler() {
        BluetoothEventHandler handler = new BluetoothEventHandler() {
            @Override
            public void onMessageReceived(String message) {

            }

            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onError(String errorMsg) {

            }
        };

        this.bluetoothProvider.registerHandler(handler);
        this.bluetoothProvider.unregisterHandler(handler);

        List<BluetoothEventHandler> handlers = this.bluetoothProvider.getEventHandlers();

        assertEquals(handlers.size(), 0);
    }

    @Test
    public void testOnConnectedCallback() throws InterruptedException {
        // hack for setting variable out of BluetoothEventHandler class
        final Boolean[] calledList = new Boolean[1];
        calledList[0] = false;

        BluetoothEventHandler handler = new BluetoothEventHandler() {
            @Override
            public void onMessageReceived(String message) {

            }

            @Override
            public void onConnected() {
                calledList[0] = true;
            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onError(String errorMsg) {

            }
        };

        this.bluetoothProvider.registerHandler(handler);
        this.bluetoothProvider.enableDummyDevices(1);
        List<Device> devices = this.bluetoothProvider.getPairedDevices();
        this.bluetoothProvider.connectToDevice(devices.get(0));
        this.bluetoothProvider.getConnectThread().join();

        assertTrue(calledList[0]);
    }

    @Test
    public void testOnMessageReceivedCallback() {
        // hack for setting variable out of BluetoothEventHandler class
        final String[] calledList = new String[1];
        calledList[0] = "";

        BluetoothEventHandler handler = new BluetoothEventHandler() {
            @Override
            public void onMessageReceived(String message) {
                calledList[0] = message;
            }

            @Override
            public void onConnected() {
            }

            @Override
            public void onDisconnected() {
            }

            @Override
            public void onError(String errorMsg) {

            }
        };

        this.bluetoothProvider.registerHandler(handler);
        this.bluetoothProvider.enableDummyDevices(1);
        List<Device> devices = this.bluetoothProvider.getPairedDevices();
        this.bluetoothProvider.connectToDevice(devices.get(0));

        // TODO: Maybe change the name of this method?
        this.bluetoothProvider.setReceivedMessage("test");

        assertEquals(calledList[0], "test");
    }

    @Test
    public void testOnDisconnectedCallback() {
        // hack for setting variable out of BluetoothEventHandler class
        final Boolean[] calledList = new Boolean[1];
        calledList[0] = false;

        BluetoothEventHandler handler = new BluetoothEventHandler() {
            @Override
            public void onMessageReceived(String message) {
            }

            @Override
            public void onConnected() {
            }

            @Override
            public void onDisconnected() {
                calledList[0] = true;
            }

            @Override
            public void onError(String errorMsg) {

            }
        };

        this.bluetoothProvider.registerHandler(handler);
        this.bluetoothProvider.enableDummyDevices(1);
        List<Device> devices = this.bluetoothProvider.getPairedDevices();
        this.bluetoothProvider.connectToDevice(devices.get(0));
        this.bluetoothProvider.disconnect();

        assertTrue(calledList[0]);
    }

    @Test
    public void testConnectToDevice() {
        Device device = new DummyDevice("1");
        bluetoothProvider.connectToDevice(device);

        assertEquals(bluetoothProvider.getConnectedDevice().getID(), "1");
        assertTrue(bluetoothProvider.isConnected());
    }

    @Test
    public void testSendMessage() {
        String message = "Test Message";
        bluetoothProvider.sendMessage(message);
        assertEquals(message, bluetoothProvider.checkSendMessage());
    }

    @Test
    public void testDisconnectFromDevice() {
        bluetoothProvider.disconnect();
        assertFalse(bluetoothProvider.isConnected());
    }
}