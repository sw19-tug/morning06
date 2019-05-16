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

    private Queue<BluetoothMessage> messageQueue;
    private BluetoothAdapter adapter;
    private Thread communicationThread;
    private RealDevice device;
    private boolean running = true;

    public RealBluetoothProvider() throws BluetoothException {
        initialize();
    }

    public void initialize() throws BluetoothException {
        adapter = BluetoothAdapter.getDefaultAdapter();
        messageQueue = new LinkedList<>();
        device = null;
        running = true;


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

        System.out.println("RealBluetoothProvider Start Communication Thread");

        communicationThread = new Thread() {
            @Override
            public void run() {

                try {
                    BluetoothSocket socket = null;
                    final BluetoothServerSocket serverSocket = adapter.listenUsingRfcommWithServiceRecord(BLUETOOTH_SERVICE_RECORD, BLUETOOTH_UUID);

                    boolean runLoop = true;

                    // TODO: Maybe put this part into own method
                    ////////////////////////////////////////////////////////////////////////////////////
                    while (runLoop && socket == null) {

                        try {
                            socket = serverSocket.accept(200);
                            System.out.println("CommunicationThread: Connected as server.");
                        } catch (IOException timeout) {
                            // Timeout
                            synchronized (RealBluetoothProvider.this) {
                                RealDevice dev = RealBluetoothProvider.this.device;
                                if (dev != null) {
                                    System.out.println("CommunicationThread: Connection as client requested");
                                    try {
                                        socket = dev.getDevice().createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
                                        if (socket != null) {
                                            Log.d("RealBluetoothProvider", "before connect");
                                            socket.connect();
                                        }
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                        throw new RuntimeException(ex.getMessage());
                                    }
                                }

                                runLoop = RealBluetoothProvider.this.running;
                            }
                        }
                    }

                    serverSocket.close();

                    // TODO: Maybe put this part into own method
                    ////////////////////////////////////////////////////////////////////////////////////

                    BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter outputWriter = new PrintWriter(socket.getOutputStream());

                    synchronized (RealBluetoothProvider.this) {
                        messageQueue.add(new BluetoothMessage(new ConnectMessage(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME)));
                    }
                    boolean disconnected = false;

                    while (runLoop) {
                        synchronized (RealBluetoothProvider.this) {
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
                                        disconnected = true;
                                        break;

                                }
                            } else {
                                BluetoothMessage message;
                                message = messageQueue.poll();


                                if (message != null) {
                                    outputWriter.println(message.toJSONString());
                                    outputWriter.flush();
                                }
                            }

                            runLoop = (!messageQueue.isEmpty() || RealBluetoothProvider.this.running) && !disconnected;
                        }
                        Thread.sleep(100);
                    }

                    inputReader.close();
                    outputWriter.close();
                    socket.close();

                    onDisconnected();

                } catch (IOException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    throw new RuntimeException(ex);
                }
            }

        };
        communicationThread.setUncaughtExceptionHandler(exceptionHandler);
        communicationThread.start();
    }

    public void closeConnection() {
        if (communicationThread == null)
            return;

        synchronized (this) {
            running = false;
        }

        try {
            communicationThread.join();
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
        System.out.println("RealBluetoothProvider Request connection as client;");
        // TODO: Synchronisation
        this.device = (RealDevice) device;
    }

    @Override
    public synchronized void sendMessage(final Message message) {
        final BluetoothMessage btMessage = new BluetoothMessage(message);
        messageQueue.add(btMessage);
    }

    @Override
    public void disconnect() {
        final BluetoothMessage btMessage = new BluetoothMessage(new DisconnectMessage());

        synchronized (this) {
            messageQueue.add(btMessage);
        }

        closeConnection();
    }

    @Override
    protected void onMessageReceived(final Message message) {
        System.out.format("RealBluetoothProvider Message received %s%n", message);
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
}
