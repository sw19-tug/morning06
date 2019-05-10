package at.tugraz.ist.swe.cheatapp;

import java.util.ArrayList;
import java.util.List;
import static at.tugraz.ist.swe.cheatapp.Constants.ON_CONNECTED_MESSAGE;

public class DummyBluetoothProvider extends BluetoothProvider {
    private List<Device> devices;
    private Device connectedDevice;
    private boolean connected;
    private Thread thread;

    public DummyBluetoothProvider() {
        this.devices = new ArrayList<>();
    }

    @Override
    public List<Device> getPairedDevices() {
        return this.devices;
    }

    @Override
    public void connectToDevice(Device device) {
        connectedDevice = device;
        connected = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DummyBluetoothProvider.super.onConnected();
                DummyBluetoothProvider.super.onMessageReceived(String.format
                        (ON_CONNECTED_MESSAGE, connectedDevice.getID()));
            }
        });
        thread.start();
    }

    @Override
    public void sendMessage(final String message) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DummyBluetoothProvider.super.onMessageReceived(message);
            }
        });
        thread.start();
    }

    @Override
    public void disconnect() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (DummyBluetoothProvider.this) {
                    connectedDevice = null;
                    connected = false;

                }
                DummyBluetoothProvider.super.onDisconnected();
            }
        });
        thread.start();
    }

    public void enableDummyDevices(int count) {
        this.devices.clear();

        for (int i = 0; i < count; i++) {
            this.devices.add(new DummyDevice(Integer.toString(i)));
        }
    }

    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized Device getConnectedDevice() {
        return connectedDevice;
    }

    public List<BluetoothEventHandler> getEventHandlers() {
        return this.eventHandlerList;
    }

    // TODO just for testing purposes, maybe remove later
    public void setReceivedMessage(String message) {
        super.onMessageReceived(message);
    }

    public Thread getThread() {
        return this.thread;
    }

    @Override
    public Device getDeviceByID(String deviceID) {
        for(Device device : devices)
        {
            if(device.getID().equals(deviceID))
            {
                return device;
            }
        }
        return null;
    }
}
