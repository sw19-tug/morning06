package at.tugraz.ist.swe.cheatapp;

public interface Device {
    String getDeviceName();
    long getDeviceId();
    String getMessage();
    void sendMessage(String message);
}
