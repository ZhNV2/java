package test.ru.spbau;

import com.google.common.collect.ImmutableMap;
import com.sun.istack.internal.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import ru.spbau.ResetCommand;
import ru.spbau.Vcs;
import ru.spbau.zhidkov.BranchHandler;
import ru.spbau.zhidkov.ExternalFileHandler;
import ru.spbau.zhidkov.VcsFileHandler;
import ru.spbau.zhidkov.vcs.VcsBlob;
import ru.spbau.zhidkov.vcs.file.FileSystem;
import ru.spbau.zhidkov.vcs.file.ObjectSerializer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.Assert.*;

/**
 * Created by Нико on 08.04.2017.
 */
@RunWith(Parameterized.class)
public class ResetCommandTest extends ParametriziedThreeCommitsTest {

    private VcsFileHandler vcsFileHandler = mock(VcsFileHandler.class);
    private ExternalFileHandler externalFileHandler = mock(ExternalFileHandler.class);
    private BranchHandler branchHandler = mock(BranchHandler.class);

    private Path fileToReset;
    private String fileToResetHash;
    private Class<?> expectedExceptionClass;

    public ResetCommandTest(Map<Path, String> fcommit1Add, List<Path> fcommit1Rm, Map<Path, String> fcommit2Add, List<Path> fcommit2Rm, Map<Path, String> fcommit3Add, List<Path> fcommit3Rm, Path fileToReset, String fileToResetHash, Class<?> expectedExceptionClass) {
        super(fcommit1Add, fcommit1Rm, fcommit2Add, fcommit2Rm, fcommit3Add, fcommit3Rm);
        this.fileToReset = fileToReset;
        this.fileToResetHash = fileToResetHash;
        this.expectedExceptionClass = expectedExceptionClass;
    }

    @Test
    public void test() throws IOException, Vcs.VcsIncorrectUsageException {
        try {
            initCommits();
            when(externalFileHandler.normalize((Path) any())).thenAnswer(invocation -> invocation.getArguments()[0]);
            when(vcsFileHandler.getCommit(any())).thenReturn(commit1, commit2, commit3);
            byte[] data = "abc".getBytes();
            VcsBlob leftVcsBlob = new VcsBlob(mock(FileSystem.class), mock(ObjectSerializer.class), data);
            when(vcsFileHandler.getBlob(any())).thenReturn(leftVcsBlob);
            byte[] data1 = "def".getBytes();
            VcsBlob rightVcsBlob = new VcsBlob(mock(FileSystem.class), mock(ObjectSerializer.class), data1);
            when(vcsFileHandler.getBlob(eq(fileToResetHash))).thenReturn(rightVcsBlob);

            ResetCommand resetCommand = new ResetCommand(vcsFileHandler, externalFileHandler, branchHandler);
            resetCommand.reset(fileToReset);
            verify(vcsFileHandler).getBlob(fileToResetHash);
            ArgumentCaptor<Path> argumentCaptorPath = ArgumentCaptor.forClass(Path.class);
            ArgumentCaptor<byte[]> argumentCaptorByte = ArgumentCaptor.forClass(byte[].class);

            verify(externalFileHandler).writeBytesToFile(argumentCaptorPath.capture(), argumentCaptorByte.capture());
            assertEquals(argumentCaptorPath.getValue(), fileToReset);
            assertArrayEquals(argumentCaptorByte.getValue(), "def".getBytes());
        } catch (Throwable t) {
            if (expectedExceptionClass != null && !expectedExceptionClass.isInstance(t)) {
                throw t;
            }
        }
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

                    Paths.get("a"),
                    "hash",
                    null,

                },
                {
                    Collections.emptyMap(),
                    Arrays.asList(Paths.get("c"), Paths.get("a")),

                    ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("c"), "hash.c"),
                    Collections.singletonList(Paths.get("d")),

                    ImmutableMap.of(Paths.get("d"), "hash.d", Paths.get("c"), "hash.c"),
                    Collections.emptyList(),

                    Paths.get("a"),
                    "hash",
                    Vcs.VcsIncorrectUsageException.class,
                },
                {
                    Collections.emptyMap(),
                    Collections.singletonList(Paths.get("b")),

                    ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("b"), "hash.b"),
                    Collections.singletonList(Paths.get("c")),

                    ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("b"), "hash.b", Paths.get("c"), "hash.c"),
                    Collections.emptyList(),

                    Paths.get("f"),
                    "hash",
                    Vcs.VcsIncorrectUsageException.class,
                }
        });
    }

}
