package at.tugraz.ist.swe.cheatapp;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Base64;


public class FileEncoderTest {
    @Test
    public void testEncoding() throws IOException {

    }

    @Test
    public void testDecoding() throws IOException {
        File testImage = new File("sampledata/test_img_1.png");
        assert(testImage.exists());
        byte[] fileContent = Files.readAllBytes(testImage.toPath());

        File testEncodedFile = new File("sampledata/test_img_1_encoded.txt");
        assert(testEncodedFile.exists());
        byte[] fileContentEncoded = Files.readAllBytes(testEncodedFile.toPath());
        byte[] fileContentDecoded = Base64.getDecoder().decode(fileContentEncoded);

        assertEquals(fileContent, fileContentDecoded);
    }
}
