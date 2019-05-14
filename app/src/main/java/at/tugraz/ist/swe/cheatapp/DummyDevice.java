package at.tugraz.ist.swe.cheatapp;

public class DummyDevice implements Device {
    String id;
    ChatFragment chatFragment;
    private Message message;

    public DummyDevice(String id) {
        this.id = id;
        this.chatFragment = null;
    }

    @Override
    public String getID() {
        return this.id;
    }

    public Message getMessage() {
        return this.message;
    }

    public void sendMessage(final Message message) {
        chatFragment.onMessageReceived(message);
        this.message = message;
    }
}
