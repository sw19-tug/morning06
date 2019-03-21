package at.tugraz.ist.swe.cheatapp;

import java.util.List;

public interface BluetoothProvider {
    void startDiscoverability();
    void startDiscover();
    List<Device> getDevices();
    void connectToDevice(Device device);
}
