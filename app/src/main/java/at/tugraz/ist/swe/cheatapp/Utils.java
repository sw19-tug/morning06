package at.tugraz.ist.swe.cheatapp;


public class Utils {
    public static String sanitizeMessage(String messageText) {
        String trimmed = messageText.trim();
        if (trimmed.isEmpty())
            return null;
        return trimmed;
    }
}
