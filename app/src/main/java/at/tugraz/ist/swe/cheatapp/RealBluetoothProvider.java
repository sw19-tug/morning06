package at.tugraz.ist.swe.cheatapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import static at.tugraz.ist.swe.cheatapp.Constants.BLUETOOTH_SERVICE_RECORD;
import static at.tugraz.ist.swe.cheatapp.Constants.BLUETOOTH_UUID;

public class RealBluetoothProvider extends BluetoothProvider {
    private BluetoothAdapter adapter;
    private BluetoothThread bluetoothThread;

    private RealDevice device;

    public RealBluetoothProvider() throws BluetoothException {
        initialize();
    }

    public void initialize() throws BluetoothException {
        adapter = BluetoothAdapter.getDefaultAdapter();
        device = null;


        if (adapter == null) {
            throw new BluetoothException("No bluetooth adapter available");
        }

        Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable ex) {
                ex.printStackTrace();
                onError(ex.getMessage());
            }
        };

        Log.d("RealBluetoothProvider", "Starting bluetooth thread");

        bluetoothThread = new BluetoothThread(this);
        bluetoothThread.setUncaughtExceptionHandler(exceptionHandler);
        bluetoothThread.start();
    }

    public void closeConnection() {
        if (bluetoothThread == null)
            return;

        bluetoothThread.setRunning(false);

        try {
            bluetoothThread.join();
        } catch (InterruptedException ex) {
            // TODO: Do we need to do something here???
            ex.printStackTrace();
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
    public synchronized void connectToDevice(Device device) {
        // TODO -> Synchronized??
        Log.d("RealBluetoothProvider", "Requesting connection as client");
        setDevice((RealDevice) device);
    }

    @Override
    public void sendMessage(final Message message) {
        final BluetoothMessage btMessage = new BluetoothMessage(message);
        bluetoothThread.sendBluetoothMessage(btMessage);
    }

    @Override
    public void disconnect() {
        final BluetoothMessage btMessage = new BluetoothMessage(new DisconnectMessage());
        bluetoothThread.sendBluetoothMessage(btMessage);
        closeConnection();
    }

    @Override
    protected void onMessageReceived(final Message message) {
        super.onMessageReceived(message);
    }

    @Override
    protected void onDisconnected() {
        try {
            initialize();
        } catch (BluetoothException ex) {
            ex.printStackTrace();
            onError(ex.getMessage());
        }

        super.onDisconnected();
    }

    public RealDevice getDevice() {
        return device;
    }

    public void setDevice(RealDevice device) {
        this.device = device;
    }

    public BluetoothAdapter getAdapter() {
        return adapter;
    }
}
