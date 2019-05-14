package at.tugraz.ist.swe.cheatapp;

public interface Device {
    String getID();

    // TODO: Is this still necessary
    Message getMessage();

    void sendMessage(Message message);
}
