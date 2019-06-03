package at.tugraz.ist.swe.cheatapp;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Base64;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(android.util.Base64.class)

public class FileEncoderTest {
    @Test
    public void testEncodeBase64() throws IOException {
        PowerMockito.mockStatic(android.util.Base64.class);
        when(android.util.Base64.encodeToString(any(byte[].class), anyInt())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return java.util.Base64.getEncoder().encodeToString((byte[]) invocation.getArgument(0));
            }
        });
        File testImage = new File("sampledata/test_img_1.png");
        assert(testImage.exists());
        byte[] fileContent = Files.readAllBytes(testImage.toPath());

        /*
        File testEncodedFile = new File("sampledata/test_img_1_encoded.txt");
        assert (testEncodedFile.exists());
        byte[] fileContentEncoded = Files.readAllBytes(testEncodedFile.toPath());
        byte[] fileContentDecoded = Base64.getDecoder().decode(fileContentEncoded);

        assertEquals(fileContent, fileContentDecoded); */

        FileEncoder encoder = new FileEncoder();
        String encodedFile = encoder.encodeBase64(testImage);
        String encodedFileCheck = Base64.getEncoder().encodeToString(fileContent);
        assertEquals(encodedFile, encodedFileCheck);
    }

    @Test
    public void testDecodeBase64() throws IOException {
        PowerMockito.mockStatic(android.util.Base64.class);
        when(android.util.Base64.decode(any(String.class), anyInt())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return java.util.Base64.getDecoder().decode((String.class) invocation.getArgument(0));
            }
        });

        File testImageEncoded = new File("sampledata/test_img_1_encoded.txt");
        assert(testImageEncoded.exists());
        byte[] encodedBytes = Files.readAllBytes(testImageEncoded.toPath());
        String encodedString = new String(encodedBytes);

        FileEncoder encoder = new FileEncoder();
        byte[] decodedFile = encoder.decodeBase64(testImageEncoded);
        byte[] decodedFileCheck = Base64.getDecoder().decode(encodedString);
        assertEquals(decodedFile, decodedFileCheck);
    }

}