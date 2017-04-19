package test.ru.spbau;

import org.junit.Test;
import ru.spbau.BranchCommand;
import ru.spbau.Vcs;
import ru.spbau.zhidkov.BranchHandler;
import ru.spbau.zhidkov.VcsFileHandler;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class BranchCommandExceptionTest {

    private BranchHandler branchHandler = mock(BranchHandler.class);
    private VcsFileHandler vcsFileHandler = mock(VcsFileHandler.class);

    @Test(expected = Vcs.VcsBranchActionForbiddenException.class)
    public void branchDoesNotExistTest() throws IOException, Vcs.VcsIncorrectUsageException,
            Vcs.VcsBranchActionForbiddenException {
        when(branchHandler.exists(anyString())).thenReturn(true);
        BranchCommand branchCommand = new BranchCommand(branchHandler, vcsFileHandler);
        branchCommand.createBranch("branch");
    }

    @Test(expected = Vcs.VcsIncorrectUsageException.class)
    public void uncommittedChangesTest() throws Vcs.VcsConflictException, Vcs.VcsBranchActionForbiddenException,
            Vcs.VcsBranchNotFoundException, Vcs.VcsIncorrectUsageException, IOException {
        when(branchHandler.exists(anyString())).thenReturn(false);
        doThrow(new Vcs.VcsIncorrectUsageException("")).when(vcsFileHandler).assertListEmpty(any());
        BranchCommand branchCommand = new BranchCommand(branchHandler, vcsFileHandler);
        branchCommand.createBranch("branch");
    }

    @Test
    public void basicCorrectnessCreateBranchTest() throws IOException, Vcs.VcsIncorrectUsageException,
            Vcs.VcsBranchActionForbiddenException {
        when(branchHandler.exists(anyString())).thenReturn(false);
        when(branchHandler.getHeadLastCommitHash()).thenReturn("hash");
        BranchCommand branchCommand = new BranchCommand(branchHandler, vcsFileHandler);
        branchCommand.createBranch("branch");
        verify(branchHandler).setCommitHash(eq("branch"), eq("hash"));
    }

    @Test(expected = Vcs.VcsBranchActionForbiddenException.class)
    public void sameBranchDeleteTest() throws IOException, Vcs.VcsBranchNotFoundException,
            Vcs.VcsBranchActionForbiddenException {
        when(branchHandler.getHeadName()).thenReturn("master");
        BranchCommand branchCommand = new BranchCommand(branchHandler, vcsFileHandler);
        branchCommand.deleteBranch("master");
    }


}
