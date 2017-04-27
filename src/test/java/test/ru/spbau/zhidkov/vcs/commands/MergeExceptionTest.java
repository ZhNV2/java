package test.ru.spbau.zhidkov.vcs.commands;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import ru.spbau.zhidkov.vcs.commands.MergeCommand;
import ru.spbau.zhidkov.vcs.commands.Vcs;
import ru.spbau.zhidkov.vcs.handlers.BranchHandler;
import ru.spbau.zhidkov.vcs.handlers.CommitHandler;
import ru.spbau.zhidkov.vcs.handlers.ExternalFileHandler;
import ru.spbau.zhidkov.vcs.handlers.VcsFileHandler;
import ru.spbau.zhidkov.vcs.vcsObjects.VcsBlob;
import ru.spbau.zhidkov.vcs.vcsObjects.VcsCommit;
import ru.spbau.zhidkov.vcs.file.FileSystem;
import ru.spbau.zhidkov.vcs.file.ObjectSerializer;

import java.io.IOException;
import java.nio.file.Paths;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.spbau.zhidkov.vcs.handlers.VcsFileHandler.ListWithFiles.RM_LIST;


public class MergeExceptionTest {

    private BranchHandler branchHandler = mock(BranchHandler.class);
    private VcsFileHandler vcsFileHandler = mock(VcsFileHandler.class);
    private ExternalFileHandler externalFileHandler = mock(ExternalFileHandler.class);

    @Test(expected = Vcs.VcsBranchActionForbiddenException.class)
    public void sameBranchMergeTest() throws IOException, Vcs.VcsBranchActionForbiddenException,
            Vcs.VcsBranchNotFoundException, Vcs.VcsIncorrectUsageException, Vcs.VcsConflictException {
        when(branchHandler.getHeadName()).thenReturn("master");
        MergeCommand mergeCommand = new MergeCommand(branchHandler, vcsFileHandler, externalFileHandler);
        mergeCommand.merge("master");
    }

    @Test(expected = Vcs.VcsIncorrectUsageException.class)
    public void uncommittedChanges() throws IOException, Vcs.VcsIncorrectUsageException,
            Vcs.VcsConflictException, Vcs.VcsBranchNotFoundException, Vcs.VcsBranchActionForbiddenException {
        doThrow(new Vcs.VcsIncorrectUsageException("")).when(vcsFileHandler).assertListEmpty(RM_LIST);
        MergeCommand mergeCommand = new MergeCommand(branchHandler, vcsFileHandler, externalFileHandler);
        mergeCommand.merge("branch");
    }

    @Test(expected = Vcs.VcsConflictException.class)
    public void conflictTest() throws IOException, Vcs.VcsBranchActionForbiddenException,
            Vcs.VcsBranchNotFoundException, Vcs.VcsIncorrectUsageException, Vcs.VcsConflictException {
        when(branchHandler.getHeadName()).thenReturn("bb");
        VcsCommit newCommit = mock(VcsCommit.class);
        when(vcsFileHandler.buildCommit(any(), any(), any(), any(), any(), any())).thenReturn(newCommit);
        VcsCommit branchToMergeCommit = mock(VcsCommit.class);
        when(branchToMergeCommit.getHash()).thenReturn("hash");
        when(branchHandler.getBranchCommit(eq("master"))).thenReturn(branchToMergeCommit);

        VcsCommit commit = mock(VcsCommit.class);
        when(commit.getChildrenAdd()).thenReturn(ImmutableMap.of(Paths.get("a"), "b"));
        when(commit.getPrevCommitHash()).thenReturn(CommitHandler.getInitialCommitPrevHash());
        when(vcsFileHandler.getCommit(any())).thenReturn(commit);

        ObjectSerializer objectSerializer = mock(ObjectSerializer.class);
        when(objectSerializer.serialize(any())).thenReturn("a");
        VcsBlob blob = new VcsBlob(mock(FileSystem.class), objectSerializer, "new".getBytes());
        when(blob.getHash()).thenReturn("blob's hash");

        when(externalFileHandler.exists(any())).thenReturn(true);
        when(vcsFileHandler.getBlob(any())).thenReturn(blob);
        when(externalFileHandler.readAllBytes(any())).thenReturn("news".getBytes());

        MergeCommand mergeCommand = new MergeCommand(branchHandler, vcsFileHandler, externalFileHandler);
        mergeCommand.merge("master");

    }
}
