package test.ru.spbau;

import com.google.common.collect.ImmutableMap;
import com.sun.istack.internal.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import ru.spbau.MergeCommand;
import ru.spbau.Vcs;
import ru.spbau.zhidkov.BranchHandler;
import ru.spbau.zhidkov.ExternalFileHandler;
import ru.spbau.zhidkov.VcsFileHandler;
import ru.spbau.zhidkov.vcs.VcsBlob;
import ru.spbau.zhidkov.vcs.VcsCommit;
import ru.spbau.zhidkov.vcs.file.FileSystem;
import ru.spbau.zhidkov.vcs.file.ObjectSerializer;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class MergeCommandTest extends ParametriziedThreeCommitsTest {

    private BranchHandler branchHandler = mock(BranchHandler.class);
    private VcsFileHandler vcsFileHandler = mock(VcsFileHandler.class);
    private ExternalFileHandler externalFileHandler = mock(ExternalFileHandler.class);

    private List<Path> revFiles;
    private List<Path> newFiles;
    public MergeCommandTest(Map<Path, String> fcommit1Add, List<Path> fcommit1Rm, Map<Path, String> fcommit2Add, List<Path> fcommit2Rm, Map<Path, String> fcommit3Add, List<Path> fcommit3Rm, List<Path> revFiles, List<Path> newFiles) {
        super(fcommit1Add, fcommit1Rm, fcommit2Add, fcommit2Rm, fcommit3Add, fcommit3Rm);
        this.revFiles = revFiles;
        this.newFiles = newFiles;
    }


    @Test
    public void test() throws IOException, Vcs.VcsBranchActionForbiddenException, Vcs.VcsBranchNotFoundException, Vcs.VcsIncorrectUsageException, Vcs.VcsConflictException {
        initCommits();
        VcsCommit newCommit = mock(VcsCommit.class);
        when(vcsFileHandler.buildCommit(any(), any(), any(), any(), any(), any())).thenReturn(newCommit);
        VcsCommit branchToMergeCommit = mock(VcsCommit.class);
        when(branchToMergeCommit.getHash()).thenReturn("hash");
        when(branchHandler.getBranchCommit(eq("master"))).thenReturn(branchToMergeCommit);
        when(vcsFileHandler.getCommit(any())).thenReturn(commit1, commit2, commit3);
        when(externalFileHandler.exists(any())).thenAnswer(invocation -> {
            Path arg = (Path) invocation.getArguments()[0];
            return revFiles.contains(arg);
        });
        ObjectSerializer objectSerializer = mock(ObjectSerializer.class);
        when(objectSerializer.serialize(any())).thenReturn("a");
        VcsBlob blob = new VcsBlob(mock(FileSystem.class), objectSerializer, "new".getBytes());
        when(blob.getHash()).thenReturn("blob's hash");
        when(vcsFileHandler.getBlob(any())).thenReturn(blob);
        when(externalFileHandler.readAllBytes(any())).thenReturn("new".getBytes());

        MergeCommand mergeCommand = new MergeCommand(branchHandler, vcsFileHandler, externalFileHandler);
        mergeCommand.merge("master");

        ArgumentCaptor<Path> argumentCaptorAddFile = ArgumentCaptor.forClass(Path.class);
        verify(newCommit, atLeast(0)).addToChildrenAdd(argumentCaptorAddFile.capture(), any());

        assertTrue(CollectionUtils.isEqualCollection(argumentCaptorAddFile.getAllValues(), newFiles));
    }

    @NotNull
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {
                ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("b"), "hash.b"),
                Collections.emptyList(),

                ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("c"), "hash.c"),
                Collections.emptyList(),

                ImmutableMap.of(Paths.get("d"), "hash.d", Paths.get("c"), "hash.c"),
                Collections.emptyList(),

                Collections.emptyList(),
                Arrays.asList(Paths.get("a"), Paths.get("b"), Paths.get("c"), Paths.get("d")),
            },

            {
                ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("b"), "hash.b"),
                Collections.emptyList(),

                ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("c"), "hash.c"),
                Collections.emptyList(),

                ImmutableMap.of(Paths.get("d"), "hash.d", Paths.get("c"), "hash.c"),
                Collections.emptyList(),

                Arrays.asList(Paths.get("a"), Paths.get("c")),
                Arrays.asList(Paths.get("b"), Paths.get("d")),
            },
        });
    }


}
