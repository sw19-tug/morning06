package at.tugraz.ist.swe.cheatapp;

public interface BluetoothEventHandler {
    void onMessageReceived(Message message);

    void onConnected();

    void onDisconnected();

    void onError(String errorMsg);
}
