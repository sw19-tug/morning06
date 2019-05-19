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


    public BluetoothThread(final RealBluetoothProvider provider) {
        this.provider = provider;
        this.messageQueue = new LinkedList<>();
        this.running = true;
        this.socket = null;
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
        finally {
            shutdownCommunication();
            provider.onDisconnected();
        }

    }

    private void shutdownCommunication() {

        try {
            inputReader.close();
            outputWriter.close();
            socket.close();
        } catch (IOException ignore) {
            Log.d("BluetoothThread",
                    "Got IOException while shutting down communication, continuing as if nothing happened");
        }

    }

    private void handleMessages() throws IOException, JSONException, InterruptedException {
        boolean disconnected = false;
        boolean runLoop = true;

        while (runLoop) {
            synchronized (this) {
                if (inputReader.ready()) {
                    String receivedMessage = inputReader.readLine();
                    final BluetoothMessage bluetoothMessage = BluetoothMessage.fromJSONString(receivedMessage);

                    provider.handleBluetoothMessage(bluetoothMessage);
                } else {
                    BluetoothMessage message;
                    message = messageQueue.poll();


                    if (message != null) {
                        outputWriter.println(message.toJSONString());
                        outputWriter.flush();
                    }
                }

                runLoop = !messageQueue.isEmpty() || running;
            }
            Thread.sleep(100);
        }
    }

    private void initializeConnection() throws IOException {
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
                    RealDevice dev = provider.getDevice();
                    if (dev != null) {
                        Log.d("BluetoothThread", "Connection as client requested");
                        try {
                            socket = dev.getDevice().createRfcommSocketToServiceRecord(BLUETOOTH_UUID);
                            if (socket != null) {
                                socket.connect();
                                Log.d("BluetoothThread", "Connected as client");

                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            throw new RuntimeException(ex.getMessage());
                        }

                        loop = running;
                    }
                }
            }
        }

        serverSocket.close();
    }
}