package at.tugraz.ist.swe.cheatapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RealBluetoothProvider extends BluetoothProvider {

    private BluetoothAdapter adapter;

    private ServerConnectThread serverConnectThread;

    private Thread communicationThread;

    public RealBluetoothProvider() throws  BluetoothException {
        adapter = BluetoothAdapter.getDefaultAdapter();

        if (adapter == null) {
            throw new BluetoothException("No bluetooth adapter available");
        }

        this.serverConnectThread = new ServerConnectThread(adapter);
        this.serverConnectThread.start();

        communicationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                BluetoothSocket socket = RealBluetoothProvider.this.serverConnectThread.getSocket();
            }
        });
        communicationThread.start();
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
