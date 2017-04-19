package test.ru.spbau;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import ru.spbau.RemoveCommand;
import ru.spbau.Vcs;
import ru.spbau.zhidkov.BranchHandler;
import ru.spbau.zhidkov.CommitHandler;
import ru.spbau.zhidkov.ExternalFileHandler;
import ru.spbau.zhidkov.VcsFileHandler;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class RemoveCommandTest {

    private ExternalFileHandler externalFileHandler = mock(ExternalFileHandler.class);
    private VcsFileHandler vcsFileHandler = mock(VcsFileHandler.class);
    private CommitHandler commitHandler = mock(CommitHandler.class);
    private BranchHandler branchHandler = mock(BranchHandler.class);

    @Test
    public void testCorrectness() throws IOException, Vcs.VcsIncorrectUsageException {
        runScenario(Arrays.asList(Paths.get("a"), Paths.get("b")),
                Arrays.asList(Paths.get("a"), Paths.get("b"), Paths.get("c")));
    }

    @Test(expected = Vcs.VcsIncorrectUsageException.class)
    public void rmFileNotFromRevTest() throws IOException, Vcs.VcsIncorrectUsageException {
        runScenario(Arrays.asList(Paths.get("a"), Paths.get("b")),
                Arrays.asList(Paths.get("a"), Paths.get("d"), Paths.get("c")));
    }


    private void runScenario(List<Path> filesToRm, List<Path> revFiles) throws IOException, Vcs.VcsIncorrectUsageException {
        when(externalFileHandler.normalize((List<Path>) any())).thenAnswer(invocation -> invocation.getArguments()[0]);

        when(branchHandler.getHeadLastCommitHash()).thenReturn("hash");
        when(commitHandler.getAllActiveFilesInRevision(eq("hash"))).thenReturn(revFiles);
        ArgumentCaptor<Path> argumentCaptorDeletedFiles = ArgumentCaptor.forClass(Path.class);
        RemoveCommand removeCommand = new RemoveCommand(externalFileHandler, vcsFileHandler, commitHandler, branchHandler);

        removeCommand.remove(filesToRm);
        verify(externalFileHandler, atLeast(0)).deleteIfExists(argumentCaptorDeletedFiles.capture());

        verify(vcsFileHandler).removeFromList(eq(VcsFileHandler.ListWithFiles.ADD_LIST), any());
        verify(vcsFileHandler, times(0)).removeFromList(eq(VcsFileHandler.ListWithFiles.RM_LIST),
                any());

        verify(vcsFileHandler).addToList(eq(VcsFileHandler.ListWithFiles.RM_LIST), any());
        verify(vcsFileHandler, times(0)).addToList(eq(VcsFileHandler.ListWithFiles.ADD_LIST), any());

        assertTrue(CollectionUtils.isEqualCollection(filesToRm, argumentCaptorDeletedFiles.getAllValues()));
    }


}
