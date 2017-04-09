package test.ru.spbau;

import com.google.common.collect.ImmutableMap;
import com.sun.istack.internal.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import ru.spbau.CheckoutCommand;
import ru.spbau.Vcs;
import ru.spbau.zhidkov.BranchHandler;
import ru.spbau.zhidkov.CommitHandler;
import ru.spbau.zhidkov.ExternalFileHandler;
import ru.spbau.zhidkov.VcsFileHandler;
import ru.spbau.zhidkov.vcs.VcsBlob;
import ru.spbau.zhidkov.vcs.VcsCommit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

/**
 * Created by Нико on 07.04.2017.
 */
@RunWith(Parameterized.class)
public class CheckoutCommandTest extends ParametriziedThreeCommitsTest {

    private BranchHandler branchHandler =  mock(BranchHandler.class);
    private CommitHandler commitHandler = mock(CommitHandler.class);
    private VcsFileHandler vcsFileHandler = mock(VcsFileHandler.class);
    private ExternalFileHandler externalFileHandler = mock(ExternalFileHandler.class);


    private List<Path> ansDelete;
    private List<Path> ansWrite;


    public CheckoutCommandTest(Map<Path, String> fcommit1Add, List<Path> fcommit1Rm, Map<Path, String> fcommit2Add, List<Path> fcommit2Rm, Map<Path, String> fcommit3Add, List<Path> fcommit3Rm, List<Path> ansDelete, List<Path> ansWrite) {
        super(fcommit1Add, fcommit1Rm, fcommit2Add, fcommit2Rm, fcommit3Add, fcommit3Rm);
        this.ansDelete = ansDelete;
        this.ansWrite = ansWrite;
    }


    @Test
    public void test() throws IOException, Vcs.VcsBranchNotFoundException, Vcs.VcsIncorrectUsageException {
        VcsCommit branchCommit = mock(VcsCommit.class);
        when(branchCommit.getHash()).thenReturn("branch commit hash");
        when(branchHandler.getBranchCommit(anyString())).thenReturn(branchCommit);
        when(branchHandler.getHeadLastCommitHash()).thenReturn("master hash");
        initCommits();
        when(vcsFileHandler.getCommit(anyString())).thenReturn(commit1, commit2, commit3, commit2, commit3);
        VcsBlob blob = mock(VcsBlob.class);
        when(vcsFileHandler.getBlob(anyString())).thenReturn(blob);

        CheckoutCommand checkoutCommand = new CheckoutCommand(branchHandler, commitHandler, vcsFileHandler, externalFileHandler);
        checkoutCommand.checkoutBranch("branch");
        ArgumentCaptor<Path> argumentCaptorDelete = ArgumentCaptor.forClass(Path.class);
        ArgumentCaptor<Path> argumentCaptorWrite = ArgumentCaptor.forClass(Path.class);

        verify(externalFileHandler, atLeast(0)).writeBytesToFile(argumentCaptorWrite.capture(), any());
        verify(externalFileHandler, atLeast(0)).deleteIfExists(argumentCaptorDelete.capture());

        assertTrue(CollectionUtils.isEqualCollection(argumentCaptorDelete.getAllValues(), ansDelete));
        assertTrue(CollectionUtils.isEqualCollection(argumentCaptorWrite.getAllValues(), ansWrite));
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

                        Arrays.asList(Paths.get("a"), Paths.get("b"), Paths.get("c"), Paths.get("d")),
                        Arrays.asList(Paths.get("a"), Paths.get("c"), Paths.get("d")),

                },
                {
                        Collections.emptyMap(),
                        Arrays.asList(Paths.get("c"), Paths.get("a")),

                        ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("c"), "hash.c"),
                        Collections.singletonList(Paths.get("d")),

                        ImmutableMap.of(Paths.get("d"), "hash.d", Paths.get("c"), "hash.c"),
                        Collections.emptyList(),

                        Collections.emptyList(),
                        Arrays.asList(Paths.get("a"), Paths.get("c")),

                },
                {
                        Collections.emptyMap(),
                        Collections.singletonList(Paths.get("b")),

                        ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("b"), "hash.b"),
                        Collections.singletonList(Paths.get("c")),

                        ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("b"), "hash.b", Paths.get("c"), "hash.c"),
                        Collections.emptyList(),

                        Collections.singletonList(Paths.get("a")),
                        Arrays.asList(Paths.get("a"), Paths.get("b")),

                }
        });
    }
}
