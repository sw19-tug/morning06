package at.tugraz.ist.swe.cheatapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observer;
import java.util.Queue;
import java.util.Set;

public class RealBluetoothProvider extends BluetoothProvider {

    private BluetoothAdapter adapter;
    private ServerConnectThread serverConnectThread;

    private Thread communicationThread;
    private Queue<Message> messageQueue;

    public RealBluetoothProvider() throws  BluetoothException {
        adapter = BluetoothAdapter.getDefaultAdapter();
        messageQueue = new LinkedList<>();

        if (adapter == null) {
            throw new BluetoothException("No bluetooth adapter available");
        }

        this.serverConnectThread = new ServerConnectThread(adapter);
        this.serverConnectThread.start();

        System.out.println("RealBluetoothProvider Start Communication Thread");
        communicationThread = new Thread(new Runnable() {
            BluetoothSocket socket;
            @Override
            public  void run() {
                try {
                    synchronized (RealBluetoothProvider.this.serverConnectThread) {
                        RealBluetoothProvider.this.serverConnectThread.wait();
                    }
                    socket = RealBluetoothProvider.this.serverConnectThread.getSocket();

                    BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    OutputStreamWriter outputWriter = new OutputStreamWriter(socket.getOutputStream());

                    while(true) {
                        // TODO: IO Loop
                    }
                } catch (IOException e) {
                    // TODO
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
        System.out.println("Request connection as client;");
        serverConnectThread.requestConnection((RealDevice) device);
    }
}
