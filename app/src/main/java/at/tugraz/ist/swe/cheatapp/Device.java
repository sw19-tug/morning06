package at.tugraz.ist.swe.cheatapp;

public interface Device {
    String getID();
    String getMessage();
    void sendMessage(String message);
}
