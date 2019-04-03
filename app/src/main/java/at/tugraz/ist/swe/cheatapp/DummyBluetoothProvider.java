package at.tugraz.ist.swe.cheatapp;

import java.util.ArrayList;
import java.util.List;

public class DummyBluetoothProvider extends BluetoothProvider {
    private List<Device> devices;
    private Device connectedDevice;
    private boolean connected;


    public DummyBluetoothProvider() {
        this.devices = new ArrayList<>();
    }

    @Override
    public List<Device> getPairedDevices() {
        return this.devices;
    }

    @Override
    public void connectToDevice(Device device) {
        connectedDevice = device;
        connected = true;
    }

    @Override
    protected void onMessageReceived(String message) {

    }

    @Override
    protected void onDisconnected() {

    }

    @Override
    protected void onConnected() {

    }

    @Override
    protected void onException(Exception exception) {
        
    }

    public void enableDummyDevices(int count) {
        this.devices.clear();

        for (int i = 0; i < count; i++) {
            this.devices.add(new DummyDevice(Integer.toString(i)));
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public Device getConnectedDevice() {
        return connectedDevice;
    }
}
