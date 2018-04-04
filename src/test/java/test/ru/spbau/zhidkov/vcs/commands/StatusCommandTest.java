package test.ru.spbau.zhidkov.vcs.commands;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.spbau.zhidkov.vcs.commands.StatusCommand;
import ru.spbau.zhidkov.vcs.handlers.BranchHandler;
import ru.spbau.zhidkov.vcs.handlers.ExternalFileHandler;
import ru.spbau.zhidkov.vcs.handlers.VcsFileHandler;
import ru.spbau.zhidkov.vcs.vcsObjects.VcsBlob;
import ru.spbau.zhidkov.vcs.file.FileSystem;
import ru.spbau.zhidkov.vcs.file.ObjectSerializer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(Parameterized.class)
public class StatusCommandTest extends ParametriziedThreeCommitsTest {

    private VcsFileHandler vcsFileHandler = mock(VcsFileHandler.class);
    private ExternalFileHandler externalFileHandler = mock(ExternalFileHandler.class);
    private BranchHandler branchHandler = mock(BranchHandler.class);

    public StatusCommandTest(Map<Path, String> fcommit1Add, List<Path> fcommit1Rm, Map<Path, String> fcommit2Add,
                             List<Path> fcommit2Rm, Map<Path, String> fcommit3Add, List<Path> fcommit3Rm,
                             List<Path> addList, List<Path> rmList, List<Path> externalFiles,
                             List<Path> modifiedFilesAns, List<Path> foreignFilesAns) {
        super(fcommit1Add, fcommit1Rm, fcommit2Add, fcommit2Rm, fcommit3Add, fcommit3Rm);
        this.addList = addList;
        this.rmList = rmList;
        this.externalFiles = externalFiles;
        this.modifiedFilesAns = modifiedFilesAns;
        this.foreignFilesAns = foreignFilesAns;
    }

    private List<Path> addList;
    private List<Path> rmList;
    private List<Path> externalFiles;

    private List<Path> modifiedFilesAns;
    private List<Path> foreignFilesAns;

    @Test
    public void test() throws IOException {
        initCommits();
        when(vcsFileHandler.getList(eq(VcsFileHandler.ListWithFiles.RM_LIST))).thenReturn(rmList);
        when(vcsFileHandler.getList(eq(VcsFileHandler.ListWithFiles.ADD_LIST))).thenReturn(addList);
        when(externalFileHandler.readAllExternalFiles()).thenReturn(externalFiles);
        when(externalFileHandler.isDirectory(any())).thenReturn(false);
        when(vcsFileHandler.getCommit(any())).thenReturn(commit1, commit2, commit3);
        byte[] left = "abc".getBytes();
        byte[] right = "def".getBytes();
        VcsBlob rightBlob = new VcsBlob(mock(FileSystem.class), mock(ObjectSerializer.class), right);
        when(vcsFileHandler.getBlob(any())).thenReturn(rightBlob);
        when(externalFileHandler.readAllBytes(any())).thenAnswer(invocation -> {
            Path arg = (Path) invocation.getArguments()[0];
            return modifiedFilesAns.contains(arg) ? left : right;
        });

        StatusCommand command = new StatusCommand(vcsFileHandler, externalFileHandler, branchHandler);
        StatusCommand.StatusHolder statusHolder = command.status();

        assertTrue(CollectionUtils.isEqualCollection(addList, statusHolder.addedFiles));
        assertTrue(CollectionUtils.isEqualCollection(rmList, statusHolder.removedFiles));
        assertTrue(CollectionUtils.isEqualCollection(modifiedFilesAns, statusHolder.modifiedFiles));
        assertTrue(CollectionUtils.isEqualCollection(foreignFilesAns, statusHolder.foreignFiles));
    }

    @NotNull
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("b"), "hash.b"),
                        Collections.emptyList(),

                        ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("c"), "hash.c"),
                        Collections.emptyList(),

                        ImmutableMap.of(Paths.get("d"), "hash.d", Paths.get("c"), "hash.c"),
                        Collections.emptyList(),

                        Collections.emptyList(),
                        Collections.emptyList(),
                        Arrays.asList(Paths.get("a"), Paths.get("b"), Paths.get("c"), Paths.get("d")),
                        Arrays.asList(Paths.get("a"), Paths.get("b"), Paths.get("c"), Paths.get("d")),
                        Collections.emptyList(),

                },
                {
                        Collections.emptyMap(),
                        Arrays.asList(Paths.get("c"), Paths.get("a")),

                        ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("c"), "hash.c"),
                        Collections.singletonList(Paths.get("d")),

                        ImmutableMap.of(Paths.get("d"), "hash.d", Paths.get("c"), "hash.c"),
                        Collections.emptyList(),

                        Arrays.asList(Paths.get("a"), Paths.get("b")),
                        Collections.emptyList(),
                        Arrays.asList(Paths.get("a"), Paths.get("b"), Paths.get("c"), Paths.get("d")),
                        Collections.emptyList(),
                        Arrays.asList(Paths.get("c"), Paths.get("d")),
                },
                {
                        Collections.emptyMap(),
                        Collections.emptyList(),

                        ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("b"), "hash.b"),
                        Collections.singletonList(Paths.get("c")),

                        ImmutableMap.of(Paths.get("a"), "hash.a", Paths.get("b"),
                                "hash.b", Paths.get("c"), "hash.c"),
                        Collections.emptyList(),

                        Collections.emptyList(),
                        Collections.singletonList(Paths.get("b")),
                        Arrays.asList(Paths.get("a"), Paths.get("b"), Paths.get("c"), Paths.get("d")),
                        Collections.singletonList(Paths.get("a")),
                        Arrays.asList(Paths.get("c"), Paths.get("d")),
                }
        });
    }
}
