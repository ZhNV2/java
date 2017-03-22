package test.ru.spbau;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.spbau.FileSystem;
import ru.spbau.Vcs;
import ru.spbau.zhidkov.VcsCommit;
import ru.spbau.zhidkov.VcsObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static test.TestsStaticData.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest({FileSystem.class, VcsObject.class})
public class CommitTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void commitCorrectnessTest() throws Exception {
        File workFolder = folder.newFolder();
        Vcs.setCurrentFolder(workFolder.getPath());

        PowerMockito.mockStatic(FileSystem.class);

        List<String> files = Arrays.asList("a.txt", "b.txt", "c\\d\\e.png");
        String prevCommitHash = "dafja;lsdfja;";
        String message = "hello!";

        when(FileSystem.getFirstLine(eq(Vcs.getAuthorName()))).thenReturn(AUTHOR_NAME);
        when(FileSystem.readAllLines(eq(Vcs.getAddList()))).thenReturn(files);
        when(FileSystem.getFirstLine(eq(Vcs.getHEAD()))).thenReturn("master");
        when(FileSystem.getFirstLine(eq(Vcs.getBranchesDir() + File.separator + "master"))).thenReturn(prevCommitHash);
        for (String file : files) {
            when(FileSystem.readAllBytes(eq(file))).thenReturn(file.getBytes());
        }

        PowerMockito.doCallRealMethod().when(FileSystem.class, "writeStringToFile", anyString(), anyString());
        PowerMockito.doCallRealMethod().when(FileSystem.class, "createEmptyFile", anyString());
        PowerMockito.doCallRealMethod().when(FileSystem.class, "createDirectory", anyString());
        PowerMockito.doCallRealMethod().when(FileSystem.class, "writeToFile",
                Matchers.any(VcsObject.class), anyString());

        basicVcsFolderSetup(folder, workFolder);

        Vcs.commit(message);

        String hash = Files.readAllLines(Paths.get(workFolder.getPath() + MASTER)).get(0);
        VcsCommit vcsCommit = (VcsCommit) VcsObject.readFromJson(workFolder.getPath() + OBJECTS + File.separator + hash, VcsCommit.class);
        assertEquals(vcsCommit.getPrevCommitHash(), prevCommitHash);
        assertEquals(vcsCommit.getMessage(), message);
        assertEquals(vcsCommit.getAuthor(), AUTHOR_NAME);
        assertTrue(vcsCommit.getChildren().containsKey("a.txt"));
        assertTrue(vcsCommit.getChildren().containsKey("b.txt"));
    }

}
