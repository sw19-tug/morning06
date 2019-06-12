package at.tugraz.ist.swe.cheatapp;

public abstract class Device {
    protected String deviceName;
    protected long deviceId; // MAC address used as unique identifier
    protected String nickname;
    protected String profilePicture;

    public String getDeviceName() {
        return this.deviceName;
    }
    public long getDeviceId() {
        return this.deviceId;
    }
    public String getNickname() { return this.nickname; }
    public String getProfilePicture() { return this.profilePicture; }

    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public static long idStringToLong(String idString) {
        idString = idString.replace(":", "");
        return Long.valueOf(idString, 16);
    }
}
