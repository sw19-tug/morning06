package at.tugraz.ist.swe.cheatapp;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
            public void onMessageReceived(Message message) {

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
            public void onMessageReceived(Message message) {

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
            public void onMessageReceived(Message message) {

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
        this.bluetoothProvider.getThread().join();

        assertTrue(calledList[0]);
    }

    @Test
    public void testOnMessageReceivedCallback() throws InterruptedException {
        // hack for setting variable out of BluetoothEventHandler class
        final Message[] calledList = new Message[1];
        calledList[0] = null;

        BluetoothEventHandler handler = new BluetoothEventHandler() {
            @Override
            public void onMessageReceived(Message message) {
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
        this.bluetoothProvider.getThread().join();

        // TODO: Maybe change the name of this method?
        this.bluetoothProvider.setReceivedMessage(new Message(0, "test", true));

        assertNotNull(calledList[0]);
        assertEquals(calledList[0].getMessageText(), "test");
    }

    @Test
    public void testOnDisconnectedCallback() throws InterruptedException {
        // hack for setting variable out of BluetoothEventHandler class
        final Boolean[] calledList = new Boolean[1];
        calledList[0] = false;

        BluetoothEventHandler handler = new BluetoothEventHandler() {
            @Override
            public void onMessageReceived(Message message) {
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
        this.bluetoothProvider.getThread().join();
        this.bluetoothProvider.disconnect();
        this.bluetoothProvider.getThread().join();

        assertTrue(calledList[0]);
    }

    @Test
    public void testConnectToDevice() throws InterruptedException {
        this.bluetoothProvider.enableDummyDevices(1);
        List<Device> devices = this.bluetoothProvider.getPairedDevices();
        this.bluetoothProvider.connectToDevice(devices.get(0));
        this.bluetoothProvider.getThread().join();

        assertEquals(bluetoothProvider.getConnectedDevice().getID(), "0");
        assertTrue(bluetoothProvider.isConnected());
    }

    @Test
    public void testDisconnectFromDevice() throws InterruptedException {
        bluetoothProvider.disconnect();
        this.bluetoothProvider.getThread().join();
        assertFalse(bluetoothProvider.isConnected());
    }
}