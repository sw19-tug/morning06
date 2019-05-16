package at.tugraz.ist.swe.cheatapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

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
                } catch (IOException ignore) {
                    // Timeout
                    synchronized (this) {
                        if (this.device != null) {
                            System.out.println("ConnectThread: Connection as client requested");
                            try {
                                this.socket = this.device.getAndroidDevice().createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
                                if(this.socket != null)
                                {
                                    this.socket.connect();
                                    this.interrupt();
                                }
                            } catch (IOException ex) {
                                this.interrupt();
                                throw new RuntimeException(ex.getMessage()); // TODO show error Toast
                            }
                        }
                    }
                }
            }

            synchronized (this) {
                this.connected = true;
                this.notify();
            }

        } catch (IOException ex) {
            throw new RuntimeException(ex);
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