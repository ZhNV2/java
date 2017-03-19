package test;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.spbau.FileSystem;
import ru.spbau.Vcs;

import java.io.IOException;


@RunWith(PowerMockRunner.class)
@PrepareForTest(FileSystem.class)
public class VcsTest {
    @Test
    public void test() throws IOException {
        PowerMockito.mockStatic(FileSystem.class);

        when(FileSystem.getFirstLine(anyString())).thenReturn("");

        System.out.println(Vcs.log());

    }


}
