package at.tugraz.ist.swe.cheatapp;

import java.util.ArrayList;
import java.util.List;

public abstract class BluetoothProvider {
    protected List<BluetoothEventHandler> eventHandlerList;
    protected Device connectedDevice;
    protected String ownNickname;

    public BluetoothProvider() {
        this.eventHandlerList = new ArrayList<>();
    }

    public abstract List<Device> getPairedDevices();

    public abstract Device getDeviceByID(long deviceID);

    public abstract void connectToDevice(Device device);

    public abstract void sendMessage(ChatMessage message);

    public void setOwnNickname(String ownNickname) {
        this.ownNickname = ownNickname;
    }

    public abstract void disconnect();

    public void registerHandler(BluetoothEventHandler handler) {
        eventHandlerList.add(handler);
    }

    public void unregisterHandler(BluetoothEventHandler handler) {
        eventHandlerList.remove(handler);
    }

    public String getOwnNickname() {
        return ownNickname;
    }

    protected void onConnected() {
        for (BluetoothEventHandler handler : eventHandlerList) {
            handler.onConnected();
        }
    }

    protected void onDisconnected() {
        for (BluetoothEventHandler handler : eventHandlerList) {
            handler.onDisconnected();
        }
    }

    protected void onMessageReceived(final ChatMessage message) {
        for (BluetoothEventHandler handler : eventHandlerList) {
            handler.onMessageReceived(message);
        }
    }

    protected void onError(String errorMsg) {
        for (BluetoothEventHandler handler : eventHandlerList) {
            handler.onError(errorMsg);
        }
    }

    public Device getConnectedDevice() {
        return connectedDevice;
    }

    public abstract boolean isBluetoothEnabled();

    protected Thread.UncaughtExceptionHandler createExceptionHandler() {
        return new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable ex) {
                onError(ex.getMessage());
            }
        };
    }
}
