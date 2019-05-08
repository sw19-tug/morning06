package at.tugraz.ist.swe.cheatapp;

public class DummyDevice implements Device {
    long deviceId;
    String deviceName;
    ChatFragment chatFragment;
    private String message;

    public DummyDevice(String name) {
        this.deviceName = name;
        this.chatFragment = null;
    }

    // This constructor is only for testing and can be removed if bluetooth communication works
    public DummyDevice(String name, ChatFragment chatFragment) {
        this.deviceName = name;
        this.chatFragment = chatFragment;
    }

    @Override
    public String getDeviceName() {
        return this.deviceName;
    }

    @Override
    public long getDeviceId() {
        return this.deviceId;
    }

    public String getMessage() {
        return this.message;
    }

    public void sendMessage(String message) {
        chatFragment.onMessageReceived(message);
        this.message = message;
    }
}
