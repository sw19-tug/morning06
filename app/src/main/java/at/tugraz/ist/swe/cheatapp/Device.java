package at.tugraz.ist.swe.cheatapp;

public abstract class Device {
    protected String deviceName;
    protected long deviceId; // MAC address used as unique identifier

    public String getDeviceName() {
        return this.deviceName;
    }
    public long getDeviceId() {
        return this.deviceId;
    }

    public static long idStringToLong(String idString) {
        idString = idString.replace(":", "");
        return Long.valueOf(idString, 16);
    }
}
