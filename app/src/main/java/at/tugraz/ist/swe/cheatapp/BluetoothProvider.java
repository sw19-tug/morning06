package at.tugraz.ist.swe.cheatapp;

import java.util.List;
import java.util.Observable;

public abstract class BluetoothProvider  extends Observable {
    abstract List<Device> getPairedDevices();

    public abstract void connectToDevice(Device device);
    protected abstract void onMessageReceived(String message);
    protected abstract void onDisconnected();
    protected abstract void onConnected();

    public abstract void sendMessage(String message);
    public abstract void disconnect();
}
