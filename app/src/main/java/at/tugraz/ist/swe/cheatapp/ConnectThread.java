package at.tugraz.ist.swe.cheatapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

import static at.tugraz.ist.swe.cheatapp.Constants.BLUETOOTH_SERVICE_RECORD;
import static at.tugraz.ist.swe.cheatapp.Constants.BLUETOOTH_UUID;

public class ConnectThread extends Thread {
    private BluetoothAdapter adapter;
    private BluetoothSocket socket;
    private RealDevice device;
    private boolean connected;


    public ConnectThread(BluetoothAdapter adapter) {
        this.adapter = adapter;
        this.socket = null;
        this.device = null;
        this.connected = false;
    }

    @Override
    public void run() {
        try {
            final BluetoothServerSocket serverSocket = adapter.listenUsingRfcommWithServiceRecord(BLUETOOTH_SERVICE_RECORD, BLUETOOTH_UUID);

            while (!this.isInterrupted()) {
                try {
                    this.socket = serverSocket.accept(200);
                    this.interrupt();
                    System.out.println("ConnectThread: Connected as server.");
                }
                catch (IOException ignore) {
                    // Timeout

                    synchronized (this) {
                        if (this.device != null) {
                            System.out.println("ConnectThread: Connection as client requested");
                            // TODO: Refactor

                            try {
                                this.socket =  this.device.getDevice().createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
                                this.socket.connect();
                                boolean connected = this.socket.isConnected();
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

            synchronized (this) {
                this.connected = true;
                this.notify();
            }

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void requestConnection(RealDevice device) {
        this.device = device;
    }


    public synchronized BluetoothSocket getSocket() throws InterruptedException {
        if (!this.connected) {
            this.wait();
        }
        return socket;
    }
}