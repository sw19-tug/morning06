package at.tugraz.ist.swe.cheatapp;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

import static at.tugraz.ist.swe.cheatapp.Constants.BLUETOOTH_SERVICE_RECORD;
import static at.tugraz.ist.swe.cheatapp.Constants.BLUETOOTH_UUID;

public class BluetoothThread extends Thread {
    private RealBluetoothProvider provider;
    private Queue<BluetoothMessage> messageQueue;
    private boolean running;
    private BluetoothSocket socket;
    private BufferedReader inputReader;
    private PrintWriter outputWriter;
    private RealDevice connectedDevice;


    public BluetoothThread(final RealBluetoothProvider provider) {
        this.provider = provider;
        this.messageQueue = new LinkedList<>();
        this.running = true;
        this.socket = null;
        this.inputReader = null;
        this.outputWriter = null;
        this.connectedDevice = null;
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
            Log.d("BluetoothThread", "Create Socket");
            createBluetoothSocket();
            Log.d("BluetoothThread", "Init Connection");
            initializeConnection();
            Log.d("BluetoothThread", "Handle Message");
            handleMessages();

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } catch (JSONException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            Log.d("BluetoothThread", "Shutdown");
            shutdownCommunication();
            provider.onDisconnected();
        }
    }

    private void shutdownCommunication() {

        try {
            if(inputReader != null)
                inputReader.close();
            if(outputWriter != null)
                outputWriter.close();
            if(socket != null)
                socket.close();
        } catch (IOException ignore) {
            Log.d("BluetoothThread",
                    "Got IOException while shutting down communication, continuing as if nothing happened");
        }

    }

    private void handleMessages() throws Exception {
        boolean loop = true;
        boolean handshake_okay = false;
        int timeout_counter = Constants.LOOPS_UNTIL_TIMEOUT;

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


            // Message handling must not be done when holding the lock, otherwise we potentially
            // get a deadlock
            if (bluetoothMessage != null) {
                if(bluetoothMessage.getMessageType() == BluetoothMessage.Type.CONNECT)
                {
                    handshake_okay = true;
                }
                provider.handleBluetoothMessage(bluetoothMessage);
            }
            Thread.sleep(100);

            if(!handshake_okay && timeout_counter == 0)
            {
                throw new Exception("Connection timeout! Handshake failed.");
            }
            else
            {
                timeout_counter--;
            }
        }
    }

    private void initializeConnection() throws IOException {
        connectedDevice = new RealDevice(socket.getRemoteDevice());
        inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outputWriter = new PrintWriter(socket.getOutputStream());

        synchronized (this) {
            final BluetoothMessage connectMessage = new BluetoothMessage(new ConnectMessage(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME));
            messageQueue.add(connectMessage);
        }
    }

    private void createBluetoothSocket() throws IOException {
        final BluetoothServerSocket serverSocket = provider.getAdapter().listenUsingRfcommWithServiceRecord(BLUETOOTH_SERVICE_RECORD, BLUETOOTH_UUID);

        boolean loop = true;

        while (loop && socket == null) {

            try {
                socket = serverSocket.accept(200);
                Log.d("BluetoothThread", "Connected as server");
            } catch (IOException timeout) {
                // Timeout
                synchronized (this) {
                    if (connectedDevice != null) {
                        Log.d("BluetoothThread", String.format("Connection as client requested for device %s", connectedDevice.getDeviceName()));

                        socket = connectedDevice.getAndroidDevice().createRfcommSocketToServiceRecord(BLUETOOTH_UUID);

                        if (socket != null) {
                            socket.connect();
                             Log.d("BluetoothThread", "Try connection as client");
                        }
                    }
                }
            }
        }

        serverSocket.close();
    }

    public synchronized void connectToDevice(Device device) {
        Log.d("BluetoothThread", "Requesting connection as client");
        this.connectedDevice = (RealDevice) device;
    }

    public synchronized RealDevice getConnectedDevice() {
        return connectedDevice;
    }
}