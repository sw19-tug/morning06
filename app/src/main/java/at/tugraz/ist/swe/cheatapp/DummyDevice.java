package at.tugraz.ist.swe.cheatapp;

public class DummyDevice implements Device {
    String id;
    private String message;

    public DummyDevice(String id) {
        this.id = id;
    }

    @Override
    public String getID() {
        return this.id;
    }

    public String getMessage() { return this.message; }

    public void sendMessage(String message) { this.message = message; }

}
