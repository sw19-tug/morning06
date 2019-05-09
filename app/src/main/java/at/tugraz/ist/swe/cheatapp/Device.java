package at.tugraz.ist.swe.cheatapp;

public abstract class Device {
    protected String deviceName;
    protected long deviceId; // MAC address used as unique identifier

    public abstract String getMessage();
    public abstract void sendMessage(String message);

    public String getDeviceName() {
        return this.deviceName;
    }
    public long getDeviceId() {
        return this.deviceId;
    }

    public long idStringToLong(String idString) {
        idString = idString.replace(":", "");
        return Long.valueOf(idString, 16);
    }


}
