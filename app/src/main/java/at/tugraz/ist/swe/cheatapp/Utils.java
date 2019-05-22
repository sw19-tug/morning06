package at.tugraz.ist.swe.cheatapp;


public class Utils {
    public static String sanitizeMessage(String messageText){
        if(messageText.trim().length() > 0)
            return messageText.trim();

        return null;
    }
}
