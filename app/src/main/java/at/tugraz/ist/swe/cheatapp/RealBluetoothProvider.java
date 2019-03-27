package at.tugraz.ist.swe.cheatapp;

import java.util.ArrayList;
import java.util.List;

public class RealBluetoothProvider implements BluetoothProvider {
    @Override
    public List<Device> getPairedDevices() {

        List<Device> devices = new ArrayList<>();
        devices.add(new RealDevice());

        return devices;
    }

    @Override
    public void connectToDevice(Device device) {

    }
}
