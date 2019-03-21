package at.tugraz.ist.swe.cheatapp;

import java.util.ArrayList;
import java.util.List;

public class DummyBluetoothProvider implements BluetoothProvider {
    private List<Device> devices;
    private boolean discoverable;


    public DummyBluetoothProvider() {
        this.devices = new ArrayList<>();
    }

    @Override
    public void startDiscoverability() {
        this.discoverable = true;
    }

    @Override
    public void startDiscover() {

    }

    @Override
    public List<Device> getDevices() {
        return this.devices;
    }

    @Override
    public void connectToDevice(Device device) {

    }

    public void enableDummyDevices(int count) {
        this.devices.clear();

        for (int i = 0; i < count; i++) {
            this.devices.add(new DummyDevice(Integer.toString(i)));
        }
    }

    public boolean isDiscoverable() {
        return discoverable;
    }
}
