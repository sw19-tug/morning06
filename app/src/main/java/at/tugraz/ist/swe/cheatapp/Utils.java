package at.tugraz.ist.swe.cheatapp;


public class Utils {
    private static boolean testing = false;



    public static String sanitizeMessage(String messageText) {
        String trimmed = messageText.trim();
        if (trimmed.isEmpty())
            return null;
        return trimmed;
    }

    public static boolean isTesting() {
        return testing;
    }

    public static void setTesting(boolean testing) {
        Utils.testing = testing;
    }
}
