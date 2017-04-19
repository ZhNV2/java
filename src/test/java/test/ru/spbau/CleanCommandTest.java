package test.ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import ru.spbau.CleanCommand;
import ru.spbau.Vcs;
import ru.spbau.zhidkov.BranchHandler;
import ru.spbau.zhidkov.CommitHandler;
import ru.spbau.zhidkov.ExternalFileHandler;
import ru.spbau.zhidkov.VcsFileHandler;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class CleanCommandTest {

    private List<Path> externalFiles;
    private List<Path> revisionFiles;
    private Map<Path, List<Path>> listOfFiles;

    private List<Path> filesToDelete;
    private List<Path> dirsToDelete;

    private VcsFileHandler vcsFileHandler = mock(VcsFileHandler.class);
    private BranchHandler branchHandler = mock(BranchHandler.class);
    private ExternalFileHandler externalFileHandler = mock(ExternalFileHandler.class);
    private CommitHandler commitHandler = mock(CommitHandler.class);


    public CleanCommandTest(List<Path> externalFiles, List<Path> revisionFiles, Map<Path, List<Path>> listOfFiles,
                            List<Path> filesToDelete, List<Path> dirsToDelete) {
        this.externalFiles = externalFiles;
        this.revisionFiles = revisionFiles;
        this.listOfFiles = listOfFiles;
        this.filesToDelete = filesToDelete;
        this.dirsToDelete = dirsToDelete;
    }

    @Test
    public void test() throws IOException, Vcs.VcsIncorrectUsageException {
        when(externalFileHandler.readAllExternalFiles()).thenReturn(externalFiles);
        when(commitHandler.getAllActiveFilesInRevision(anyString())).thenReturn(revisionFiles);
        doAnswer(invocation -> {
            Path arg = (Path) invocation.getArguments()[0];
            for (Map.Entry<Path, List<Path>> entry : listOfFiles.entrySet()) {
                entry.getValue().remove(arg);
            }
            return "";
        }).when(externalFileHandler).deleteIfExists(any());

        doAnswer(invocation -> {
            Path arg = (Path) invocation.getArguments()[0];
            for (Map.Entry<Path, List<Path>> entry : listOfFiles.entrySet()) {
                entry.getValue().remove(arg);
            }
            return "";
        }).when(externalFileHandler).deleteFolder(any());

        doAnswer(invocation -> {
            Path arg = (Path) invocation.getArguments()[0];
            return listOfFiles.get(arg);
        }).when(externalFileHandler).readAllFiles(any());

        when(externalFileHandler.isDirectory(any())).thenAnswer(invocation -> {
            Path arg = (Path) invocation.getArguments()[0];
            return !arg.toString().contains(".");
        });


        CleanCommand cleanCommand = new CleanCommand(vcsFileHandler, branchHandler, externalFileHandler, commitHandler);
        cleanCommand.clean();

        ArgumentCaptor<Path> argumentCaptorDeleteFile = ArgumentCaptor.forClass(Path.class);
        ArgumentCaptor<Path> argumentCaptorDeleteFolder = ArgumentCaptor.forClass(Path.class);

        verify(externalFileHandler, atLeast(0)).deleteFolder(argumentCaptorDeleteFolder.capture());
        verify(externalFileHandler, atLeast(0)).deleteIfExists(argumentCaptorDeleteFile.capture());

        assertTrue(CollectionUtils.isEqualCollection(argumentCaptorDeleteFile.getAllValues(), filesToDelete));
        assertTrue(CollectionUtils.isEqualCollection(argumentCaptorDeleteFolder.getAllValues(), dirsToDelete));

    }


    @NotNull
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {
                        Arrays.asList(Paths.get("a.txt"), Paths.get("dir"), Paths.get("dir/b.txt")),
                        Collections.singletonList(Paths.get("b")),

                        new HashMap<Path, List<Path>>() {
                            {
                                put(Paths.get("dir"), new ArrayList<Path>() {{
                                    add(Paths.get("dir/b.txt"));
                                    add(Paths.get("dir"));
                                }});
                            }
                        },

                        Arrays.asList(Paths.get("a.txt"), Paths.get("dir/b.txt")),
                        Collections.singletonList(Paths.get("dir")),
                },
                {
                        Arrays.asList(Paths.get("a.txt"), Paths.get("dir"), Paths.get("dir/b.txt"),
                                Paths.get("dir/dir2"), Paths.get("dir/dir2/c.txt")),
                        Collections.singletonList(Paths.get("dir/dir2/c.txt")),

                        new HashMap<Path, List<Path>>() {
                            {
                                put(Paths.get("dir"), new ArrayList<Path>() {{
                                    add(Paths.get("dir/b.txt"));
                                    add(Paths.get("dir"));
                                    add(Paths.get("dir/dir2"));
                                    add(Paths.get("dir/dir2/c.txt"));
                                }});

                                put(Paths.get("dir/dir2"), new ArrayList<Path>() {{
                                    add(Paths.get("dir/dir2"));
                                    add(Paths.get("dir/dir2/c.txt"));
                                }});
                            }
                        },

                        Arrays.asList(Paths.get("a.txt"), Paths.get("dir/b.txt")),
                        Collections.emptyList()
                },
                {
                        Arrays.asList(Paths.get("a.txt"), Paths.get("dir"), Paths.get("dir/b.txt"),
                                Paths.get("dir/dir2"), Paths.get("dir/dir2/c.txt")),
                        Arrays.asList(Paths.get("dir/b.txt"), Paths.get("a.txt")),

                        new HashMap<Path, List<Path>>() {
                            {
                                put(Paths.get("dir"), new ArrayList<Path>() {{
                                    add(Paths.get("dir/b.txt"));
                                    add(Paths.get("dir"));
                                    add(Paths.get("dir/dir2"));
                                    add(Paths.get("dir/dir2/c.txt"));
                                }});

                                put(Paths.get("dir/dir2"), new ArrayList<Path>() {{
                                    add(Paths.get("dir/dir2"));
                                    add(Paths.get("dir/dir2/c.txt"));
                                }});
                            }
                        },

                        Collections.singletonList(Paths.get("dir/dir2/c.txt")),
                        Collections.singletonList(Paths.get("dir/dir2"))
                },

                {
                        Arrays.asList(Paths.get("dir"), Paths.get("dir/b.txt")),
                        Collections.emptyList(),

                        new HashMap<Path, List<Path>>() {
                            {
                                put(Paths.get("dir"), new ArrayList<Path>() {{
                                    add(Paths.get("dir/b.txt"));
                                    add(Paths.get("dir"));
                                }});
                            }
                        },

                        Collections.singletonList(Paths.get("dir/b.txt")),
                        Collections.singletonList(Paths.get("dir"))
                },

        });
    }
}
