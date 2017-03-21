package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.beust.jcommander.ParameterException;
import com.sun.deploy.util.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.spbau.FileSystem;
import ru.spbau.Vcs;
import ru.spbau.zhidkov.VcsCommit;
import ru.spbau.zhidkov.VcsObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


@RunWith(PowerMockRunner.class)
@PrepareForTest(FileSystem.class)
public class VcsTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void addCorrectnessTest() throws IOException {
        PowerMockito.mockStatic(FileSystem.class);
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<byte[]> byteCaptor = ArgumentCaptor.forClass(byte[].class);
        when(FileSystem.exists(anyString())).thenReturn(true);
        List<String> files = Arrays.asList("a.txt", "b.txt", "c\\d\\e.png");
        Vcs.add(files);
        PowerMockito.verifyStatic();
        FileSystem.appendToFile(stringCaptor.capture(), byteCaptor.capture());
        String toAdd = new String(byteCaptor.getValue(), StandardCharsets.UTF_8);
        String realToAdd = "";
        for (String file : files) {
            realToAdd += file + System.lineSeparator();
        }
        assertEquals(realToAdd, toAdd);
    }


    @Test(expected = FileNotFoundException.class)
    public void addFileDoesNotExistTest() throws IOException {
        PowerMockito.mockStatic(FileSystem.class);
        when(FileSystem.exists(eq("a.txt"))).thenReturn(true);
        when(FileSystem.exists(eq("b.txt"))).thenReturn(false);
        List<String> files = Arrays.asList("a.txt", "b.txt");
        Vcs.add(files);
    }

    @Test
    public void commitCorrectnessTest() throws Exception {
        File workFolder = folder.newFolder();
        Vcs.setCurrentFolder(workFolder.getPath());

        PowerMockito.mockStatic(FileSystem.class);

        List<String> files = Arrays.asList("a.txt", "b.txt", "c\\d\\e.png");
        String prevCommitHash = "dafja;lsdfja;";
        String author = "me";
        String message = "hello!";

        when(FileSystem.getFirstLine(eq(Vcs.getAuthorName()))).thenReturn(author);
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

        folder.newFolder(workFolder.getName() + VCS);
        folder.newFolder(workFolder.getName() + OBJECTS);
        folder.newFolder(workFolder.getName() + BRANCHES);
        folder.newFile(workFolder.getName() + MASTER);

        Vcs.commit(message);

        String hash = Files.readAllLines(Paths.get(workFolder.getPath() + MASTER)).get(0);
        VcsCommit vcsCommit = (VcsCommit) VcsObject.readFromJson(workFolder.getPath() + OBJECTS + File.separator + hash, VcsCommit.class);
        assertEquals(vcsCommit.getPrevCommitHash(), prevCommitHash);
        assertEquals(vcsCommit.getMessage(), message);
        assertEquals(vcsCommit.getAuthor(), author);
        assertTrue(vcsCommit.getChildren().containsKey("a.txt"));
        assertTrue(vcsCommit.getChildren().containsKey("b.txt"));
    }


    private static final String VCS = File.separator + ".vcs";
    private static final String OBJECTS = File.separator + ".vcs" + File.separator + "objects";
    private static final String BRANCHES = File.separator + ".vcs" + File.separator + "branches";
    private static final String MASTER = File.separator + ".vcs" + File.separator + "branches" + File.separator + "master";






}
