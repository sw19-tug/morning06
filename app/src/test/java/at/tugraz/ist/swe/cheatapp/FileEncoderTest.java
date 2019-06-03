package at.tugraz.ist.swe.cheatapp;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import static junit.framework.TestCase.assertEquals;

public class FileEncoderTest {
    @Test
    public void testEncoding() throws IOException {
        File testImage = new File("sampledata/test_img_1.png");
        assert(testImage.exists());
        byte[] fileContent = Files.readAllBytes(testImage.toPath());

        FileEncoder encoder = new FileEncoder();
        String encodedFile = encoder.encodeBase64(testImage);

        String encodedFileCheck = Base64.getEncoder().encodeToString(fileContent);
        assertEquals(encodedFile, encodedFileCheck);
    }
}
