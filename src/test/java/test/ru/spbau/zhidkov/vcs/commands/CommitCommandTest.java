package test.ru.spbau.zhidkov.vcs.commands;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import ru.spbau.zhidkov.vcs.commands.CommitCommand;
import ru.spbau.zhidkov.vcs.handlers.BranchHandler;
import ru.spbau.zhidkov.vcs.handlers.ExternalFileHandler;
import ru.spbau.zhidkov.vcs.handlers.VcsFileHandler;
import ru.spbau.zhidkov.vcs.vcsObjects.VcsBlob;
import ru.spbau.zhidkov.vcs.vcsObjects.VcsCommit;
import ru.spbau.zhidkov.vcs.file.FileSystem;
import ru.spbau.zhidkov.vcs.file.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class CommitCommandTest {

    private List<Path> addFiles;
    private List<Path> rmFiles;
    private Map<Path, String> addFilesHashes;

    private VcsFileHandler vcsFileHandler = mock(VcsFileHandler.class);
    private BranchHandler branchHandler = mock(BranchHandler.class);
    private ExternalFileHandler externalFileHandler = mock(ExternalFileHandler.class);


    public CommitCommandTest(List<Path> addFiles, List<Path> rmFiles, Map<Path, String> addFilesHashes) {
        this.addFiles = addFiles;
        this.rmFiles = rmFiles;
        this.addFilesHashes = addFilesHashes;
    }

    @Test
    public void test() throws IOException {


        when(vcsFileHandler.getAuthorName()).thenReturn("me");
        when(branchHandler.getHeadLastCommitHash()).thenReturn("last commit hash");
        when(externalFileHandler.readAllBytes(any())).thenReturn(new byte[0]);
        when(vcsFileHandler.getList(VcsFileHandler.ListWithFiles.ADD_LIST)).thenReturn(addFiles);
        when(vcsFileHandler.getList(VcsFileHandler.ListWithFiles.RM_LIST)).thenReturn(rmFiles);
        when(branchHandler.getHeadName()).thenReturn("master");
        Map<Path, VcsBlob> blobs = new HashMap<>();
        for (Map.Entry<Path, String> entry : addFilesHashes.entrySet()) {
            VcsBlob blob = mock(VcsBlob.class);
            when(blob.getHash()).thenReturn(entry.getValue());
            blobs.put(entry.getKey(), blob);
        }
        when(vcsFileHandler.buildBlob(any())).thenAnswer(invocation -> {
            Path arg = (Path) invocation.getArguments()[0];
            assertNotNull(blobs.get(arg));
            return blobs.get(arg);
        });

        when(vcsFileHandler.buildCommit(any(), any(), any(), any(), any(), any())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ObjectSerializer objectSerializer = mock(ObjectSerializer.class);
            when(objectSerializer.serialize(any())).thenReturn("abc");
            Constructor<VcsCommit> commitConstructor = VcsCommit.class.getDeclaredConstructor(FileSystem.class,
                    ObjectSerializer.class, String.class, Date.class, String.class, String.class, Map.class, List.class);
            commitConstructor.setAccessible(true);
            return commitConstructor.newInstance(mock(FileSystem.class), objectSerializer, args[0], args[1],
                    args[2], args[3], args[4], args[5]);
        });

        CommitCommand commitCommand = new CommitCommand(vcsFileHandler, branchHandler);
        commitCommand.commit("hello, world");

        verify(vcsFileHandler).clearList(VcsFileHandler.ListWithFiles.ADD_LIST);
        verify(vcsFileHandler).clearList(VcsFileHandler.ListWithFiles.RM_LIST);

        ArgumentCaptor<VcsCommit> argumentCaptorDeleteFile = ArgumentCaptor.forClass(VcsCommit.class);
        verify(vcsFileHandler).writeCommit(argumentCaptorDeleteFile.capture());

        VcsCommit commit = argumentCaptorDeleteFile.getValue();
        assertTrue(CollectionUtils.isEqualCollection(commit.getChildrenAdd().entrySet(), addFilesHashes.entrySet()));
        assertTrue(CollectionUtils.isEqualCollection(commit.getChildrenRm(), rmFiles));

        assertEquals(commit.getMessage(), "hello, world");
        assertEquals(commit.getAuthor(), "me");

        verify(branchHandler).setCommitHash(eq("master"), any());
    }

    @NotNull
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        Arrays.asList(Paths.get("a"), Paths.get("b"), Paths.get("c")),
                        Arrays.asList(Paths.get("d"), Paths.get("e"), Paths.get("f")),
                        ImmutableMap.of(Paths.get("a"), "a", Paths.get("b"), "b", Paths.get("c"), "c"),
                },
                {
                        Arrays.asList(Paths.get("a/b/c"), Paths.get("qwerty")),
                        Arrays.asList(Paths.get("d/a/b"), Paths.get("r/i/p"), Paths.get("l/o/l")),
                        ImmutableMap.of(Paths.get("a/b/c"), "abc", Paths.get("qwerty"), "qwer"),
                },
        });
    }
}
