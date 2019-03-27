package at.tugraz.ist.swe.cheatapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

import static at.tugraz.ist.swe.cheatapp.Constants.BLUETOOTH_SERVICE_RECORD;
import static at.tugraz.ist.swe.cheatapp.Constants.BLUETOOTH_UUID;

public class ServerConnectThread extends Thread {
    private BluetoothAdapter adapter;
    private BluetoothSocket socket;


    public ServerConnectThread(BluetoothAdapter adapter) {
        this.adapter = adapter;
        this.socket = null;
    }

    @Override
    public void run() {
        try {
            final BluetoothServerSocket serverSocket = adapter.listenUsingRfcommWithServiceRecord(BLUETOOTH_SERVICE_RECORD, BLUETOOTH_UUID);

            while (!this.isInterrupted()) {
                try {
                    this.socket = serverSocket.accept(1000);
                    this.interrupt();
                }
                catch (IOException ignore) {
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public BluetoothSocket getSocket() {
        return socket;
    }
}