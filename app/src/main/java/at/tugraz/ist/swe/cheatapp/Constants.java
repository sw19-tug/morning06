package at.tugraz.ist.swe.cheatapp;

import java.util.UUID;

public class Constants {
    public static final String BLUETOOTH_SERVICE_RECORD = "cheatapp";
    public static final UUID BLUETOOTH_UUID = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
    public static final String ON_CONNECTED_MESSAGE = "You connected to device %s.";
    public static final int LOOPS_UNTIL_TIMEOUT = 20;
}
