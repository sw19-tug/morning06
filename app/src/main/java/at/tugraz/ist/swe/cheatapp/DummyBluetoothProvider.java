package at.tugraz.ist.swe.cheatapp;

import java.util.ArrayList;
import java.util.List;

public class DummyBluetoothProvider extends BluetoothProvider {
    private List<Device> devices;
    private Device connectedDevice;
    private boolean connected;
    private String sendMessage;
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
            }
        });
        thread.start();
    }

    @Override
    public void sendMessage(String message) {
        sendMessage = message;
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

    public String checkSendMessage() {
        return sendMessage;
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
}
