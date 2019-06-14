package at.tugraz.ist.swe.cheatapp;

public class DummyDevice extends Device {
    public DummyDevice(String name, String id) {
        this.deviceName = name;
        this.deviceId = Utils.idStringToLong(id);
    }
}
