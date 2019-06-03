package at.tugraz.ist.swe.cheatapp;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(android.util.Base64.class)

public class FileEncoderTest {
    @Test
    public void testEncoding() throws IOException {
        PowerMockito.mockStatic(android.util.Base64.class);
//        when(android.util.Base64.encodeToString(any<Byte[]>(), anyInt())).thenAnswer(invocation -> java.util.Base64.getEncoder().encode((byte[]) invocation.getArguments()[0]));
//        when(Base64.decode(anyString(), anyInt())).thenAnswer(invocation -> java.util.Base64.getMimeDecoder().decode((String) invocation.getArguments()[0]));

        File testImage = new File("sampledata/test_img_1.png");
        assert(testImage.exists());
        byte[] fileContent = Files.readAllBytes(testImage.toPath());

        String encodedFileCheck = Base64.getEncoder().encodeToString(fileContent);

        FileEncoder encoder = new FileEncoder();
        when(android.util.Base64.encodeToString(any(byte[].class), anyInt())).thenReturn(encodedFileCheck);

        String encodedFile = encoder.encodeBase64(testImage);

        assertEquals(encodedFile, encodedFileCheck);
    }
}
