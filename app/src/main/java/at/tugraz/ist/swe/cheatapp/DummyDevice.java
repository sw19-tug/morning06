package at.tugraz.ist.swe.cheatapp;

public class DummyDevice extends Device {
    ChatFragment chatFragment;

    public DummyDevice(String name, String id) {
        this.deviceName = name;
        this.deviceId = idStringToLong(id);
        this.chatFragment = null;
    }

    public DummyDevice(String name, String id, ChatFragment chatFragment) {
        this.deviceName = name;
        this.deviceId = idStringToLong(id);
        this.chatFragment = chatFragment;
    }
}
