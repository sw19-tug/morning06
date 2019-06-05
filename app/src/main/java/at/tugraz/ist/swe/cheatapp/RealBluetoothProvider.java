package at.tugraz.ist.swe.cheatapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RealBluetoothProvider extends BluetoothProvider {
    private BluetoothAdapter adapter;
    private BluetoothThread bluetoothThread;
    private Thread.UncaughtExceptionHandler exceptionHandler;

    public RealBluetoothProvider() throws BluetoothException {
        initialize();
    }

    public void initialize() throws BluetoothException {
        // TODO: Should the bluetooth adapter be a member of BluetoothThread?
        adapter = BluetoothAdapter.getDefaultAdapter();

        if (adapter == null) {
            throw new BluetoothException("Hardware has no bluetooth support");
        }

        exceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable ex) {
                onError(ex.getMessage());
            }
        };

        Log.d("RealBluetoothProvider", "Starting bluetooth thread");

        initBluetoothThread();
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
        connectedDevice = device;
        bluetoothThread.connectToDevice(device);
    }

    @Override
    public void sendMessage(final ChatMessage message) {
        final BluetoothMessage btMessage = new BluetoothMessage(message);
        bluetoothThread.sendBluetoothMessage(btMessage);
    }

    @Override
    public void disconnect() {
        final BluetoothMessage btMessage = new BluetoothMessage(new DisconnectMessage());
        bluetoothThread.sendBluetoothMessage(btMessage);
        bluetoothThread.setRunning(false);
    }

    @Override
    protected void onMessageReceived(final ChatMessage message) {
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
        connectedDevice = null;
        super.onDisconnected();
    }

    public void handleBluetoothMessage(final BluetoothMessage bluetoothMessage) {
        switch (bluetoothMessage.getMessageType()) {
            case CHAT:
                onMessageReceived(bluetoothMessage.getMessage());
                break;
            case CONNECT:
                String nickname = bluetoothMessage.getConnectMessage().getNickname();
                if(nickname.isEmpty()) {
                    nickname = this.bluetoothThread.getConnectedDevice().getDeviceName();
                }
                this.bluetoothThread.getConnectedDevice().setNickname(nickname);

                String profilePicture = bluetoothMessage.getConnectMessage().getProfilePicture();
                if(profilePicture.isEmpty()) {
                    profilePicture = Constants.EMPTY_PROFILE_PICTURE;
                }
                this.bluetoothThread.getConnectedDevice().setProfilePicture(profilePicture);

                onConnected();
                break;
            case DISCONNECT:
                bluetoothThread.setRunning(false);
                break;
        }
    }

    @Override
    public Device getConnectedDevice() {
        return this.bluetoothThread.getConnectedDevice();
    }

    public BluetoothAdapter getAdapter() {
        return adapter;
    }

    @Override
    public boolean isBluetoothEnabled() {
        if (adapter.isEnabled()) {
            return true;
        }

        return false;
    }

    @Override
    public Device getDeviceByID(long deviceID) {
        Set<BluetoothDevice> btDevices = adapter.getBondedDevices();

        for (BluetoothDevice device : btDevices) {
            if (Device.idStringToLong(device.getAddress()) == deviceID) {
                return new RealDevice(device);
            }
        }

        return null;
    }

    private void initBluetoothThread() {
        bluetoothThread = new BluetoothThread(this);
        bluetoothThread.setUncaughtExceptionHandler(exceptionHandler);
        bluetoothThread.start();
    }
}
