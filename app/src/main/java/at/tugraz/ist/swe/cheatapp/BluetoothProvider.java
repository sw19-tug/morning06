package at.tugraz.ist.swe.cheatapp;

import java.util.List;
import java.util.Observable;

public abstract class BluetoothProvider  extends Observable {
    abstract List<Device> getPairedDevices();

    abstract void connectToDevice(Device device);
}
