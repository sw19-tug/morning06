package at.tugraz.ist.swe.cheatapp;

public interface BluetoothEventHandler {
    void onMessageReceived(ChatMessage message);

    void onConnected();

    void onDisconnected();

    void onError(String errorMsg);
}
