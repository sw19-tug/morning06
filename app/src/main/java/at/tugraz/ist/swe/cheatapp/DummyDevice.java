package at.tugraz.ist.swe.cheatapp;

public class DummyDevice implements Device {
    String id;
    private String message;
    MainActivity mainActivity;

    public DummyDevice(String id) {
        this.id = id;
        this.mainActivity = null;
    }

    // This constructor is only for testing and can be removed if bluetooth communication works
    public DummyDevice(String id, MainActivity mainActivity) {
        this.id = id;
        this.mainActivity = mainActivity;
    }

    @Override
    public String getID() {
        return this.id;
    }

    public String getMessage() { return this.message; }

    public void sendMessage(String message) {
        mainActivity.onMessageReceived(message);
        this.message = message;
    }
}
