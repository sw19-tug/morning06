package at.tugraz.ist.swe.cheatapp;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

import static at.tugraz.ist.swe.cheatapp.Constants.BLUETOOTH_SERVICE_RECORD;
import static at.tugraz.ist.swe.cheatapp.Constants.BLUETOOTH_UUID;

public class BluetoothThread extends Thread {
    private final static String TAG = "BluetoothThread";

    private final RealBluetoothProvider provider;
    private final Queue<BluetoothMessage> messageQueue;
    private boolean running;
    private BluetoothSocket socket;
    private BufferedReader inputReader;
    private PrintWriter outputWriter;
    private RealDevice connectedDevice;
    private BluetoothServerSocket serverSocket;


    public BluetoothThread(final RealBluetoothProvider provider) {
        this.provider = provider;
        this.messageQueue = new LinkedList<>();
        this.running = true;
        this.socket = null;
        this.inputReader = null;
        this.outputWriter = null;
        this.connectedDevice = null;
        this.serverSocket = null;
    }

    public synchronized void sendBluetoothMessage(final BluetoothMessage bluetoothMessage) {
        messageQueue.add(bluetoothMessage);
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        try {
            createBluetoothSocket();
            initializeConnection();
            handleMessages();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        } finally {
            shutdownCommunication();
            provider.onDisconnected();
        }
    }

    private void shutdownCommunication() {

        try {
            if (inputReader != null)
                inputReader.close();
        } catch (IOException ignore) {
            Log.d(TAG,
                    "Got IOException while closing Input Reader, continuing as if nothing happened");
        }

        if (outputWriter != null)
            outputWriter.close();

        try {
            if (socket != null)
                socket.close();
        } catch (IOException ignore) {
            Log.d(TAG, "Got IOException while closing socket, continuing as if nothing happened");
        }

        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException ignore) {
            Log.d(TAG, "Got IOException while closing Server Socket, continuing as if nothing happened");
        }
    }

    private void handleMessages() throws Exception {
        boolean loop = true;
        boolean handshakeOkay = false;
        int timeoutCounter = Constants.LOOPS_UNTIL_TIMEOUT;

        while (loop) {
            BluetoothMessage bluetoothMessage = null;

            synchronized (this) {
                if (inputReader.ready()) {
                    String receivedMessage = inputReader.readLine();
                    bluetoothMessage = BluetoothMessage.fromJSONString(receivedMessage);


                } else {
                    BluetoothMessage message;
                    message = messageQueue.poll();


                    if (message != null) {
                        outputWriter.println(message.toJSONString());
                        outputWriter.flush();
                    }
                }

                loop = !messageQueue.isEmpty() || running;
            }


            // ChatMessage handling must not be done when holding the lock, otherwise we potentially
            // get a deadlock
            if (bluetoothMessage != null) {
                if (bluetoothMessage.getMessageType() == BluetoothMessage.Type.CONNECT) {
                    handshakeOkay = true;
                }
                provider.handleBluetoothMessage(bluetoothMessage);
            }
            Thread.sleep(100);

            if (!handshakeOkay && timeoutCounter <= 0) {
                throw new BluetoothException("Connection timeout! Handshake failed.");
            } else {
                timeoutCounter--;
            }
        }
    }

    private void initializeConnection() throws IOException {
        connectedDevice = new RealDevice(socket.getRemoteDevice());
        inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outputWriter = new PrintWriter(socket.getOutputStream());

        synchronized (this) {
            final BluetoothMessage connectMessage = new BluetoothMessage(
                    new ConnectMessage(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME,
                            provider.getOwnNickname(), provider.getOwnProfilePicture()));
            messageQueue.add(connectMessage);
        }
    }

    private void createBluetoothSocket() throws IOException {
        serverSocket = provider.getAdapter().listenUsingRfcommWithServiceRecord(BLUETOOTH_SERVICE_RECORD, BLUETOOTH_UUID);
        boolean loop = true;

        while (loop && socket == null) {

            try {
                socket = serverSocket.accept(200);
                Log.d(TAG, "Connected as server");
            } catch (IOException timeout) {
                synchronized (this) {
                    if (connectedDevice != null) {
                        Log.d(TAG, String.format("Connection as client requested for device %s", connectedDevice.getDeviceName()));

                        socket = connectedDevice.getAndroidDevice().createRfcommSocketToServiceRecord(BLUETOOTH_UUID);

                        if (socket != null) {
                            try {
                                socket.connect();
                            } catch (IOException e) {
                                throw new IOException("Connection failed!");
                            }
                            Log.d(TAG, "Try connection as client");
                        }
                    }
                    loop = running;
                }
            }
        }

        serverSocket.close();
        serverSocket = null;
    }

    public synchronized void connectToDevice(Device device) {
        Log.d(TAG, "Requesting connection as client");
        this.connectedDevice = (RealDevice) device;
    }

    public synchronized RealDevice getConnectedDevice() {
        return connectedDevice;
    }
}