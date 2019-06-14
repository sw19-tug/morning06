package at.tugraz.ist.swe.cheatapp;

import android.bluetooth.BluetoothDevice;

public class RealDevice extends Device {
    private BluetoothDevice androidDevice;

    public RealDevice(BluetoothDevice device) {
        this.androidDevice = device;
        this.deviceName = device.getName();
        this.deviceId = Utils.idStringToLong(device.getAddress());
    }

    public BluetoothDevice getAndroidDevice() {
        return this.androidDevice;
    }
}
