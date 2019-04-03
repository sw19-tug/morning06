package at.tugraz.ist.swe.cheatapp;

import java.util.ArrayList;
import java.util.List;

public abstract class BluetoothProvider {
    protected List<BluetoothEventHandler> eventHandlerList;

    public BluetoothProvider() {
        this.eventHandlerList = new ArrayList<>();
    }

    public abstract List<Device> getPairedDevices();

    public abstract void connectToDevice(Device device);

    public abstract void send(String message);

    public abstract void disconnect();

    public void registerHandler(BluetoothEventHandler handler) {
        eventHandlerList.add(handler);
    }

    public void unregisterHandler(BluetoothEventHandler handler) {
        eventHandlerList.remove(handler);
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

    protected void onMessageReceived(String message) {
        for (BluetoothEventHandler handler : eventHandlerList) {
            handler.onMessageReceived(message);
        }
    }
}
