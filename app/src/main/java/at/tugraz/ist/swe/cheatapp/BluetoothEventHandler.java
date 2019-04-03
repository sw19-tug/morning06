package at.tugraz.ist.swe.cheatapp;

public interface BluetoothEventHandler {
    void onMessageReceived(String message);

    void onConnected();

    void onDisconnected();
}
