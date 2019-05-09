package at.tugraz.ist.swe.cheatapp;

public class DummyDevice extends Device {
    ChatFragment chatFragment;
    private String message;

    public DummyDevice(String name, String id) {
        this.deviceName = name;
        this.deviceId = idStringToLong(id);
        this.chatFragment = null;
    }

    // This constructor is only for testing and can be removed if bluetooth communication works
    public DummyDevice(String name, String id, ChatFragment chatFragment) {
        this.deviceName = name;
        this.deviceId = idStringToLong(id);
        this.chatFragment = chatFragment;
    }


    public String getMessage() {
        return this.message;
    }

    public void sendMessage(String message) {
        chatFragment.onMessageReceived(message);
        this.message = message;
    }
}
