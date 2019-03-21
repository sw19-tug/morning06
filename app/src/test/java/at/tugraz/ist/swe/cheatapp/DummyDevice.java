package at.tugraz.ist.swe.cheatapp;

public class DummyDevice implements Device {
    String id;

    public DummyDevice(String id) {
        this.id = id;
    }

    @Override
    public String getID() {
        return this.id;
    }
}
