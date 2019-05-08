package at.tugraz.ist.swe.cheatapp;

import android.bluetooth.BluetoothDevice;

public class RealDevice implements Device {
    private BluetoothDevice device;
    private long deviceId;

    public RealDevice(BluetoothDevice device) {
        this.device = device;
    }

    @Override
    public String getDeviceName() {
        return device.getName();
    }

    @Override
    public long getDeviceId() {
        return this.deviceId;
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
