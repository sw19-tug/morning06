package at.tugraz.ist.swe.cheatapp;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileEncoder {

    private byte[] readBytesFromFile(final File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fileInputStream.read(data);
            return data;
        }
    }

    public String encodeBase64(final File file) throws IOException {
        byte[] data = readBytesFromFile(file);
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public byte[] decodeBase64(final String data) {
        return Base64.decode(data, Base64.DEFAULT);
    }
}
