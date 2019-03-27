package at.tugraz.ist.swe.cheatapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RealBluetoothProvider implements BluetoothProvider {

    private BluetoothAdapter adapter;

    public RealBluetoothProvider() throws  BluetoothException {
        adapter = BluetoothAdapter.getDefaultAdapter();

        if (adapter == null) {
            throw new BluetoothException("No bluetooth adapter available");
        }
    }

    @Override
    public List<Device> getPairedDevices() {
        Set<BluetoothDevice> btDevices = adapter.getBondedDevices();

        List<Device> devices = new ArrayList<>();

        for (BluetoothDevice btDevice : btDevices) {
            devices.add(new RealDevice(btDevice));
        }

        return devices;
    }

    @Override
    public void connectToDevice(Device device) {

    }
}
