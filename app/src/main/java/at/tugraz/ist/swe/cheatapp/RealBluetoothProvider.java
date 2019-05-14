package at.tugraz.ist.swe.cheatapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import static at.tugraz.ist.swe.cheatapp.Constants.ON_CONNECTED_MESSAGE;

public class RealBluetoothProvider extends BluetoothProvider {

    private final Queue<BluetoothMessage> messageQueue;
    private final ConnectThread connectThread;
    private BluetoothAdapter adapter;
    private Thread communicationThread;

    public RealBluetoothProvider() throws BluetoothException {
        adapter = BluetoothAdapter.getDefaultAdapter();
        messageQueue = new LinkedList<>();

        if (adapter == null) {
            throw new BluetoothException("No bluetooth adapter available");
        }

        Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable ex) {
                onError(ex.getMessage());
            }
        };

        this.connectThread = new ConnectThread(adapter);
        this.connectThread.setUncaughtExceptionHandler(exceptionHandler);
        this.connectThread.start();

        System.out.println("RealBluetoothProvider Start Communication Thread");

        this.communicationThread = new Thread(new Runnable() {
            BluetoothSocket socket;

            @Override
            public void run() {
                try {
                    synchronized (RealBluetoothProvider.this.connectThread) {
                        RealBluetoothProvider.this.connectThread.wait();
                    }

                    socket = RealBluetoothProvider.this.connectThread.getSocket();

                    BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter outputWriter = new PrintWriter(socket.getOutputStream());

                    synchronized (messageQueue)
                    {
                        messageQueue.add(new BluetoothMessage(new ConnectMessage(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME)));
                    }

                    while (true) {
                        if (inputReader.ready()) {
                            String receivedMessage = inputReader.readLine();
                            final BluetoothMessage btMessage = BluetoothMessage.fromJSONString(receivedMessage);

                            switch (btMessage.getMessageType()) {
                                case CHAT:
                                    onMessageReceived(btMessage.getMessage());
                                    break;
                                case CONNECT:
                                    onConnected();
                                    break;
                                case DISCONNECT:
                                    break;

                            }
                        } else {
                            BluetoothMessage message;

                            synchronized (messageQueue) {
                                message = messageQueue.poll();
                            }

                            if (message != null) {
                                outputWriter.println(message.toJSONString());
                                outputWriter.flush();
                            }
                        }
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    onError(e.getMessage());
                } catch (IOException e) {
                    onDisconnected();
                } catch (JSONException e) {
                    onError(e.getMessage());
                }
            }

        });
        this.communicationThread.start();
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
        System.out.println("RealBluetoothProvider Request connection as client;");
        connectThread.requestConnection((RealDevice) device);
    }

    @Override
    public void sendMessage(final Message message) {
        synchronized (messageQueue) {
            final BluetoothMessage btMessage = new BluetoothMessage(message);
            messageQueue.add(btMessage);
        }
    }

    @Override
    public void disconnect() {

    }

    @Override
    protected void onMessageReceived(final Message message) {
        System.out.format("RealBluetoothProvider Message received %s%n", message);
        super.onMessageReceived(message);
    }

    @Override
    protected void onDisconnected() {

    }
}
