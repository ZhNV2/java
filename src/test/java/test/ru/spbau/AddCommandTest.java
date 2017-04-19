package test.ru.spbau;

import static org.mockito.Mockito.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.spbau.AddCommand;
import ru.spbau.Vcs;
import ru.spbau.zhidkov.ExternalFileHandler;
import ru.spbau.zhidkov.VcsFileHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class AddCommandTest {

    private ExternalFileHandler externalFileHandler = mock(ExternalFileHandler.class);
    private VcsFileHandler vcsFileHandler = mock(VcsFileHandler.class);

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void addCorrectnessTest() throws IOException, Vcs.VcsIncorrectUsageException {
        Path a = Paths.get("a/b/c.txt");
        Path b = Paths.get("b/d/e/t.t.txt");
        Path c = Paths.get("x");
        when(externalFileHandler.exists(any())).thenReturn(true);
        when(externalFileHandler.normalize((List<Path>) any())).thenAnswer(invocation -> invocation.getArguments()[0]);
        List<Path> files = Arrays.asList(a, b, c);
        AddCommand addCommand = new AddCommand(externalFileHandler, vcsFileHandler);
        addCommand.add(files);
        verify(vcsFileHandler).addToList(eq(VcsFileHandler.ListWithFiles.ADD_LIST), eq(files));
        verify(vcsFileHandler).removeFromList(eq(VcsFileHandler.ListWithFiles.RM_LIST), eq(files));
    }

    @Test(expected = FileNotFoundException.class)
    public void addFileDoesNotExistTest() throws IOException, Vcs.VcsIncorrectUsageException {
        Path a = Paths.get("a.txt");
        Path b = Paths.get("b.txt");
        when(externalFileHandler.exists(eq(a))).thenReturn(true);
        when(externalFileHandler.exists(eq(b))).thenReturn(false);
        when(externalFileHandler.normalize((List<Path>) any())).thenAnswer(invocation -> invocation.getArguments()[0]);
        List<Path> files = Arrays.asList(a, b);
        AddCommand addCommand = new AddCommand(externalFileHandler, vcsFileHandler);
        addCommand.add(files);
        verify(vcsFileHandler, times(0)).addToList(any(), any());
        verify(vcsFileHandler, times(0)).removeFromList(any(), any());
    }
}
