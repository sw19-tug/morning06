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
    private Device device;


    public ServerConnectThread(BluetoothAdapter adapter) {
        this.adapter = adapter;
        this.socket = null;
        this.device = null;
    }

    @Override
    public void run() {
        try {
            final BluetoothServerSocket serverSocket = adapter.listenUsingRfcommWithServiceRecord(BLUETOOTH_SERVICE_RECORD, BLUETOOTH_UUID);

            while (!this.isInterrupted()) {
                try {
                    this.socket = serverSocket.accept(200);
                    this.interrupt();
                    System.out.println("ServerConnectThread: Connected as server.");
                }
                catch (IOException ignore) {
                    // Timeout
                    if (this.device != null) {
                        System.out.println("ServerConnectThread: Connection as client requested");
                        // TODO: Refactor

                        try {
                            this.socket = ((RealDevice) this.device).getDevice().createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
                            this.socket.connect();
                            this.interrupt();
                        }
                        catch (IOException ex) {
                            ex.printStackTrace();
                            // TODO what to to here?
                        }

                    }
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void requestConnection(Device device) {
        this.device = device;
    }

    public BluetoothSocket getSocket() {
        return socket;
    }
}