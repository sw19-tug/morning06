package at.tugraz.ist.swe.cheatapp;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(android.util.Base64.class)

public class FileEncoderTest {
    private FileEncoder encoder;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(android.util.Base64.class);
        when(android.util.Base64.encodeToString(any(byte[].class), anyInt())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return java.util.Base64.getEncoder().encodeToString((byte[]) invocation.getArgument(0));
            }
        });
        when(android.util.Base64.decode(any(String.class), anyInt())).thenAnswer(new Answer<byte[]>() {
            @Override
            public byte[] answer(InvocationOnMock invocation) throws Throwable {
                return java.util.Base64.getDecoder().decode((String) invocation.getArgument(0));
            }
        });

        encoder = new FileEncoder();
    }

    @Test
    public void testEncodeBase64() throws IOException {
        File testImage = new File("sampledata/test_img_1.png");
        assert(testImage.exists());
        byte[] fileContent = Files.readAllBytes(testImage.toPath());

        String encodedFile = encoder.encodeBase64(testImage);
        String encodedFileCheck = Base64.getEncoder().encodeToString(fileContent);
        assertEquals(encodedFile, encodedFileCheck);
    }

    @Test
    public void testDecodeBase64() throws IOException {
        File testImageEncoded = new File("sampledata/test_img_1_encoded.txt");
        assert(testImageEncoded.exists());
        byte[] encodedBytes = Files.readAllBytes(testImageEncoded.toPath());
        String encodedString = new String(encodedBytes);

        byte[] decodedFile = encoder.decodeBase64(encodedString);
        byte[] decodedFileCheck = Base64.getDecoder().decode(encodedString);
        assert(Arrays.equals(decodedFile, decodedFileCheck));
    }


    @Test
    public void testEncodeDecode() throws IOException {
        byte[] originalByte = {'a', 'b', 'c', 'd', 'e'};
        String originalString = "abcde";
        assertEquals(new String(originalByte), originalString);

        File originalFile = new File ("sampledata/test_encode_decode.txt");
        assert(originalFile.exists());
        PrintWriter writer = new PrintWriter(originalFile.getPath());
        writer.print(originalString);
        writer.close();

        String encoderTest = encoder.encodeBase64(originalFile);
        byte[] decoderTest = encoder.decodeBase64(encoderTest);

        assert(Arrays.equals(decoderTest, originalByte));
        assertEquals(new String(decoderTest), originalString);
    }

    @Test
    public void testEncodeDecodeWithTestImage() throws IOException {
        File testImage = new File("sampledata/test_img_1.png");
        assert(testImage.exists());
        byte[] originalByte = Files.readAllBytes(testImage.toPath());

        String encoderTest = encoder.encodeBase64(testImage);
        byte[] decoderTest = encoder.decodeBase64(encoderTest);

        assert(Arrays.equals(decoderTest, originalByte));
    }

}