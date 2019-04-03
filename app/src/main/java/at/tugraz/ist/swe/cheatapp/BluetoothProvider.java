package at.tugraz.ist.swe.cheatapp;

import java.util.List;

public abstract class BluetoothProvider {
    public abstract List<Device> getPairedDevices();
    public abstract void connectToDevice(Device device);

    public void registerHandler(BluetoothEventHandler handler) {

    }

    public void unregisterHandler(BluetoothEventHandler handler) {

    }
}
