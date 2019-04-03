package at.tugraz.ist.swe.cheatapp;

import java.util.ArrayList;
import java.util.List;

public abstract class BluetoothProvider {
    protected List<BluetoothEventHandler> eventHandlerList;

    public abstract List<Device> getPairedDevices();
    public abstract void connectToDevice(Device device);

    public BluetoothProvider() {
        this.eventHandlerList = new ArrayList<>();
    }

    public void registerHandler(BluetoothEventHandler handler) {
        eventHandlerList.add(handler);
    }

    public void unregisterHandler(BluetoothEventHandler handler) {
        eventHandlerList.remove(handler);
    }
}
