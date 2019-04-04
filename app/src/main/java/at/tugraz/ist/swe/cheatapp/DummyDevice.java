package at.tugraz.ist.swe.cheatapp;

public class DummyDevice implements Device {
    String id;
    private String message;
    ChatFragment chatFragment;

    public DummyDevice(String id) {
        this.id = id;
        this.chatFragment = null;
    }

    // This constructor is only for testing and can be removed if bluetooth communication works
    public DummyDevice(String id, ChatFragment chatFragment) {
        this.id = id;
        this.chatFragment = chatFragment;
    }

    @Override
    public String getID() {
        return this.id;
    }

    public String getMessage() { return this.message; }

    public void sendMessage(String message) {
        chatFragment.onMessageReceived(message);
        this.message = message;
    }
}
