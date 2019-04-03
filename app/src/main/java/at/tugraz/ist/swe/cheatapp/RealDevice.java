package at.tugraz.ist.swe.cheatapp;

import android.bluetooth.BluetoothDevice;

public class RealDevice implements Device {
    private BluetoothDevice device;

    public RealDevice(BluetoothDevice device) {
        this.device = device;
    }

    @Override
    public String getID() {
        return  device.getName();
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void sendMessage(String message) {

    }
}
