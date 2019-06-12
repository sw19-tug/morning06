package at.tugraz.ist.swe.cheatapp;

import java.util.ArrayList;
import java.util.List;

public class DummyBluetoothProvider extends BluetoothProvider {
    private List<Device> devices;
    private boolean connected;
    private Thread thread;
    private boolean bluetoothEnabled;

    public DummyBluetoothProvider() {
        this.devices = new ArrayList<>();
        this.bluetoothEnabled = true;
        this.connected = false;

        setNumberOfEnabledDummyDevices(1);
    }

    @Override
    public List<Device> getPairedDevices() {
        return this.devices;
    }
    
    @Override
    public void connectToDevice(final Device device) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (device == null)
                    throw new RuntimeException("No device provided");

                synchronized (DummyBluetoothProvider.this) {
                    connectedDevice = device;
                    connected = true;
                }

                connectedDevice.setNickname(DummyBluetoothProvider.super.getOwnNickname());
                connectedDevice.setProfilePicture(DummyBluetoothProvider.super.getOwnProfilePicture());
                DummyBluetoothProvider.super.onConnected();
            }
        });
        thread.setUncaughtExceptionHandler(createExceptionHandler());
        thread.start();
    }

    @Override
    public void sendMessage(final ChatMessage message) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final ChatMessage receivedMessage = new ChatMessage(message);
                receivedMessage.setMessageSent(false);
                DummyBluetoothProvider.super.onMessageReceived(receivedMessage);
            }
        });
        thread.setUncaughtExceptionHandler(createExceptionHandler());
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
        thread.setUncaughtExceptionHandler(createExceptionHandler());
        thread.start();
    }

    public void setNumberOfEnabledDummyDevices(int count) {
        this.devices.clear();
        for (int i = 1; i <= count; i++) {
            this.devices.add(new DummyDevice(Integer.toString(i), Integer.toString(i)));
        }
    }

    public void addDummyDevice(String name, String id) {
        this.devices.clear();
        this.devices.add(new DummyDevice(name, id));
    }

    public synchronized boolean isConnected() {
        return connected;
    }

    public List<BluetoothEventHandler> getEventHandlers() {
        return this.eventHandlerList;
    }

    public void setReceivedMessage(final ChatMessage message) {
        super.onMessageReceived(message);
    }

    public Thread getThread() {
        return this.thread;
    }

    @Override
    public boolean isBluetoothEnabled() {
        return this.bluetoothEnabled;
    }

    @Override
    public Device getDeviceByID(long deviceID) {
        for(Device device : devices)
        {
            if(device.getDeviceId() == deviceID)
            {
                return device;
            }
        }
        return null;
    }
}
