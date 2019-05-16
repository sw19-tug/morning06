package at.tugraz.ist.swe.cheatapp;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import static at.tugraz.ist.swe.cheatapp.Constants.BLUETOOTH_SERVICE_RECORD;
import static at.tugraz.ist.swe.cheatapp.Constants.BLUETOOTH_UUID;

public class BluetoothThread extends Thread {
    private RealBluetoothProvider provider;

    public BluetoothThread(final RealBluetoothProvider provider) {
        this.provider = provider;
    }

    @Override
    public void run() {
        try {
            BluetoothSocket socket = null;
            final BluetoothServerSocket serverSocket = provider.getAdapter().listenUsingRfcommWithServiceRecord(BLUETOOTH_SERVICE_RECORD, BLUETOOTH_UUID);

            boolean runLoop = true;

            // TODO: Maybe put this part into own method
            ////////////////////////////////////////////////////////////////////////////////////
            while (runLoop && socket == null) {

                try {
                    socket = serverSocket.accept(200);
                    System.out.println("CommunicationThread: Connected as server.");
                } catch (IOException timeout) {
                    // Timeout
                    synchronized (provider) {
                        RealDevice dev = provider.getDevice();
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

                        runLoop = provider.isRunning();
                    }
                }
            }

            serverSocket.close();

            // TODO: Maybe put this part into own method
            ////////////////////////////////////////////////////////////////////////////////////

            BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputWriter = new PrintWriter(socket.getOutputStream());

            synchronized (provider) {
                provider.getMessageQueue().add(new BluetoothMessage(new ConnectMessage(BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME)));
            }
            boolean disconnected = false;

            while (runLoop) {
                synchronized (provider) {
                    if (inputReader.ready()) {
                        String receivedMessage = inputReader.readLine();
                        final BluetoothMessage btMessage = BluetoothMessage.fromJSONString(receivedMessage);

                        switch (btMessage.getMessageType()) {
                            case CHAT:
                                provider.onMessageReceived(btMessage.getMessage());
                                break;
                            case CONNECT:
                                provider.onConnected();
                                break;
                            case DISCONNECT:
                                disconnected = true;
                                break;

                        }
                    } else {
                        BluetoothMessage message;
                        message = provider.getMessageQueue().poll();


                        if (message != null) {
                            outputWriter.println(message.toJSONString());
                            outputWriter.flush();
                        }
                    }

                    runLoop = (!provider.getMessageQueue().isEmpty() || provider.isRunning()) && !disconnected;
                }
                Thread.sleep(100);
            }

            inputReader.close();
            outputWriter.close();
            socket.close();

            provider.onDisconnected();

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