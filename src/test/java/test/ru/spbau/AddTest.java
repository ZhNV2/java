package test.ru.spbau;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.spbau.Init;
import ru.spbau.zhidkov.FileSystem;
import ru.spbau.Vcs;
import ru.spbau.VcsObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FileSystem.class, VcsObject.class, Init.class})
public class AddTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void addCorrectnessTest() throws IOException, Vcs.VcsIllegalStateException {
        PowerMockito.mockStatic(FileSystem.class);
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<byte[]> byteCaptor = ArgumentCaptor.forClass(byte[].class);
        when(FileSystem.exists(anyString())).thenReturn(true);
        List<String> files = Arrays.asList("a.txt", "b.txt", "c\\d\\e.png");
        Vcs.add(files);
        PowerMockito.verifyStatic();
        FileSystem.appendToFile(stringCaptor.capture(), byteCaptor.capture());
        String toAdd = new String(byteCaptor.getValue(), StandardCharsets.UTF_8);
        String realToAdd = "";
        for (String file : files) {
            realToAdd += file + System.lineSeparator();
        }
        assertEquals(realToAdd, toAdd);
    }

    @Test(expected = FileNotFoundException.class)
    public void addFileDoesNotExistTest() throws IOException, Vcs.VcsIllegalStateException {
        PowerMockito.mockStatic(FileSystem.class);
        PowerMockito.mockStatic(Init.class);

        when(FileSystem.exists(eq("a.txt"))).thenReturn(true);
        when(FileSystem.exists(eq("b.txt"))).thenReturn(false);
        when(Init.hasInitialized()).thenReturn(true);
        List<String> files = Arrays.asList("a.txt", "b.txt");
        Vcs.add(files);
    }
}
