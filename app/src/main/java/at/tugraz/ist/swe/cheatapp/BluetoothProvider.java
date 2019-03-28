package at.tugraz.ist.swe.cheatapp;

import java.util.List;

public interface BluetoothProvider {
    List<Device> getPairedDevices();

    void connectToDevice(Device device);
}
