package at.tugraz.ist.swe.cheatapp;

import java.util.ArrayList;
import java.util.List;
import static at.tugraz.ist.swe.cheatapp.Constants.ON_CONNECTED_MESSAGE;

public class DummyBluetoothProvider extends BluetoothProvider {
    private List<Device> devices;
    private Device connectedDevice;
    private boolean connected;
    private String sendMessage;
    private  Thread connectThread;

    public DummyBluetoothProvider() {
        this.devices = new ArrayList<>();
    }

    @Override
    public List<Device> getPairedDevices() {
        return this.devices;
    }

    public Thread getConnectThread() {
        return connectThread;
    }

    @Override
    public void connectToDevice(Device device) {
        connectedDevice = device;
        connected = true;
        connectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                DummyBluetoothProvider.super.onConnected();
                DummyBluetoothProvider.super.onMessageReceived(String.format
                        (ON_CONNECTED_MESSAGE, connectedDevice.getID()));
            }
        });
        connectThread.start();
    }

    @Override
    public void sendMessage(String message) {
        sendMessage = message;
    }

    @Override
    public void disconnect() {
        connectedDevice = null;
        connected = false;
        super.onDisconnected();
    }

    public String checkSendMessage() {
        return sendMessage;
    }

    public void enableDummyDevices(int count) {
        this.devices.clear();

        for (int i = 0; i < count; i++) {
            this.devices.add(new DummyDevice(Integer.toString(i)));
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public Device getConnectedDevice() {
        return connectedDevice;
    }

    public List<BluetoothEventHandler> getEventHandlers() {
        return this.eventHandlerList;
    }

    // TODO just for testing purposes, maybe remove later
    public void setReceivedMessage(String message) {
        super.onMessageReceived(message);
    }
}
