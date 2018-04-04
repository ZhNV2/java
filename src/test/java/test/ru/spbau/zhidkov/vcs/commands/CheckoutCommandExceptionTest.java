package test.ru.spbau.zhidkov.vcs.commands;

import org.junit.Test;
import ru.spbau.zhidkov.vcs.commands.CheckoutCommand;
import ru.spbau.zhidkov.vcs.commands.Vcs;
import ru.spbau.zhidkov.vcs.handlers.BranchHandler;
import ru.spbau.zhidkov.vcs.handlers.CommitHandler;
import ru.spbau.zhidkov.vcs.handlers.ExternalFileHandler;
import ru.spbau.zhidkov.vcs.handlers.VcsFileHandler;
import ru.spbau.zhidkov.vcs.vcsObjects.VcsCommit;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


public class CheckoutCommandExceptionTest {

    private BranchHandler branchHandler = mock(BranchHandler.class);
    private CommitHandler commitHandler = mock(CommitHandler.class);
    private VcsFileHandler vcsFileHandler = mock(VcsFileHandler.class);
    private ExternalFileHandler externalFileHandler = mock(ExternalFileHandler.class);

    @Test(expected = Vcs.VcsBranchNotFoundException.class)
    public void branchDoesNotExistTest() throws IOException, Vcs.VcsBranchNotFoundException,
            Vcs.VcsIncorrectUsageException {
        doThrow(new Vcs.VcsBranchNotFoundException("")).when(branchHandler).assertBranchExists(anyString());
        CheckoutCommand checkoutCommand = new CheckoutCommand(branchHandler, commitHandler, vcsFileHandler,
                externalFileHandler);
        checkoutCommand.checkoutBranch("branch");
    }

    @Test(expected = Vcs.VcsRevisionNotFoundException.class)
    public void revisionDoesNotExistTest() throws IOException, Vcs.VcsIncorrectUsageException,
            Vcs.VcsRevisionNotFoundException {
        doThrow(new Vcs.VcsRevisionNotFoundException("")).when(commitHandler).assertRevisionExists(anyString());
        CheckoutCommand checkoutCommand = new CheckoutCommand(branchHandler, commitHandler, vcsFileHandler,
                externalFileHandler);
        checkoutCommand.checkoutRevision("rev");
    }

    @Test(expected = Vcs.VcsIncorrectUsageException.class)
    public void uncommittedChanges() throws IOException, Vcs.VcsIncorrectUsageException, Vcs.VcsBranchNotFoundException {
        VcsCommit vcsCommit = mock(VcsCommit.class);
        when(branchHandler.getBranchCommit(anyString())).thenReturn(vcsCommit);
        when(branchHandler.getHeadLastCommitHash()).thenReturn("a");
        doNothing().when(vcsFileHandler).assertListEmpty(VcsFileHandler.ListWithFiles.ADD_LIST);
        doThrow(new Vcs.VcsIncorrectUsageException(""))
                .when(vcsFileHandler)
                .assertListEmpty(VcsFileHandler.ListWithFiles.RM_LIST);
        CheckoutCommand checkoutCommand = new CheckoutCommand(branchHandler, commitHandler, vcsFileHandler,
                externalFileHandler);
        checkoutCommand.checkoutBranch("branch");
    }
}
