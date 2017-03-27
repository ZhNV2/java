package test.ru.spbau;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.spbau.zhidkov.vcs.FileSystem;
import ru.spbau.Vcs;
import ru.spbau.zhidkov.VcsBlob;
import ru.spbau.zhidkov.VcsCommit;
import ru.spbau.zhidkov.VcsObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static test.TestsStaticData.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FileSystem.class, VcsObject.class})
public class MergeTest {

    @Before
    public void mergeSetup() throws IOException {
        basicSetup();
    }

    @Test
    public void mergeBranchBasicCorrectnessTest() throws IOException, Vcs.VcsBranchActionForbiddenException, Vcs.VcsConflictException, Vcs.VcsBranchNotFoundException, Vcs.VcsIllegalStateException {
        VcsCommit initialCommit = new VcsCommit(Vcs.getInitialCommitMessage(), new Date(),
                AUTHOR_NAME, Vcs.getInitialCommitPrevHash(), new HashMap<>());
        HashMap<String, String> firstCommitChildren = new LinkedHashMap<>();
        firstCommitChildren.put("a.txt", "");
        firstCommitChildren.put("c.txt", "");
        firstCommitChildren.put("d.txt", "");
        VcsBlob firstDBlob = new VcsBlob("fd".getBytes());
        VcsCommit firstCommit = new VcsCommit("first commit", new Date(), AUTHOR_NAME, "", firstCommitChildren);
        HashMap<String, String> secondCommitChildren = new LinkedHashMap<>();
        VcsCommit secondCommit = new VcsCommit("second commit", new Date(), AUTHOR_NAME, "firstCommitHash", secondCommitChildren);
        secondCommitChildren.put("a.txt", "");
        secondCommitChildren.put("b.txt", "");
        VcsBlob secondABlob = new VcsBlob("sa".getBytes());
        VcsBlob secondBBlob = new VcsBlob("sb".getBytes());
        HashMap<String, String> thirdCommitChildren = new LinkedHashMap<>();
        VcsCommit thirdCommit = new VcsCommit("third commit", new Date(), AUTHOR_NAME, "secondCommitHash", thirdCommitChildren);
        thirdCommitChildren.put("c.txt", "");
        thirdCommitChildren.put("folder/d.txt", "");
        VcsBlob thirdCBlob = new VcsBlob("tc".getBytes());
        VcsBlob thirdDBlob = new VcsBlob("td".getBytes());

        when(FileSystem.exists(BRANCHES + File.separator + "branch")).thenReturn(true);
        when(FileSystem.exists("c.txt")).thenReturn(false);
        when(FileSystem.exists("folder/d.txt")).thenReturn(false);
        when(FileSystem.exists("a.txt")).thenReturn(true);
        when(FileSystem.exists("b.txt")).thenReturn(true);
        when(FileSystem.exists("d.txt")).thenReturn(false);

        when(FileSystem.readAllBytes("a.txt")).thenReturn("sa".getBytes());
        when(FileSystem.readAllBytes("b.txt")).thenReturn("sb".getBytes());

        when(VcsObject.readFromJson(anyString(), any()))
                .thenReturn(thirdCommit).thenReturn(thirdCBlob).thenReturn(thirdDBlob)
                .thenReturn(secondCommit).thenReturn(secondABlob).thenReturn(secondBBlob)
                .thenReturn(firstCommit).thenReturn(firstDBlob)
                .thenReturn(initialCommit);

        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<byte[]> byteCaptor = ArgumentCaptor.forClass(byte[].class);

        Vcs.merge("branch");

        PowerMockito.verifyStatic(times(3));
        FileSystem.writeBytesToFile(stringCaptor.capture(), byteCaptor.capture());

        List<String> filesToMerge = Arrays.asList("c.txt", "folder/d.txt", "d.txt");
        List<String> contentsToMerge = Arrays.asList("tc", "td", "fd");
        Map<String, String> fileContent = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            fileContent.put(filesToMerge.get(i), contentsToMerge.get(i));
        }
        for (int i = 0; i < 3; i++) {
            String fileToMerge = stringCaptor.getAllValues().get(i);
            String content = new String(byteCaptor.getAllValues().get(i), StandardCharsets.UTF_8);
            assertEquals(content, fileContent.get(fileToMerge));
        }

        ArgumentCaptor<VcsCommit> commitCaptor = ArgumentCaptor.forClass(VcsCommit.class);
        ArgumentCaptor<String> foldCaptor = ArgumentCaptor.forClass(String.class);
        PowerMockito.verifyStatic(times(1));
        FileSystem.writeToFile(foldCaptor.capture(), commitCaptor.capture());
        assertEquals(foldCaptor.getValue(), OBJECTS);
        VcsCommit commit = commitCaptor.getValue();
        assertEquals(commit.getAuthor(), AUTHOR_NAME);
        assertEquals(commit.getMessage(), "Merged with branch branch");
        assertEquals(commit.getPrevCommitHash(), "masterHash");
        assertEquals(commit.getChildren().size(), 3);
        for (String file : filesToMerge)
            assertTrue(commit.getChildren().containsKey(file));

    }

    @Test(expected = Vcs.VcsConflictException.class)
    public void mergeBranchConflictTest() throws IOException, Vcs.VcsBranchActionForbiddenException, Vcs.VcsConflictException, Vcs.VcsBranchNotFoundException, Vcs.VcsIllegalStateException {
        VcsCommit initialCommit = new VcsCommit(Vcs.getInitialCommitMessage(), new Date(),
                AUTHOR_NAME, Vcs.getInitialCommitPrevHash(), new HashMap<>());
        HashMap<String, String> firstCommitChildren = new LinkedHashMap<>();
        firstCommitChildren.put("a.txt", "");
        firstCommitChildren.put("c.txt", "");
        firstCommitChildren.put("d.txt", "");
        VcsBlob firstDBlob = new VcsBlob("fd".getBytes());
        VcsCommit firstCommit = new VcsCommit("first commit", new Date(), AUTHOR_NAME, "", firstCommitChildren);
        HashMap<String, String> secondCommitChildren = new LinkedHashMap<>();
        VcsCommit secondCommit = new VcsCommit("second commit", new Date(), AUTHOR_NAME, "firstCommitHash", secondCommitChildren);
        secondCommitChildren.put("a.txt", "");
        secondCommitChildren.put("b.txt", "");
        VcsBlob secondABlob = new VcsBlob("sa".getBytes());
        VcsBlob secondBBlob = new VcsBlob("sb".getBytes());
        HashMap<String, String> thirdCommitChildren = new LinkedHashMap<>();
        VcsCommit thirdCommit = new VcsCommit("third commit", new Date(), AUTHOR_NAME, "secondCommitHash", thirdCommitChildren);
        thirdCommitChildren.put("c.txt", "");
        thirdCommitChildren.put("folder/d.txt", "");
        VcsBlob thirdCBlob = new VcsBlob("tc".getBytes());
        VcsBlob thirdDBlob = new VcsBlob("td".getBytes());

        when(FileSystem.exists(BRANCHES + File.separator + "branch")).thenReturn(true);
        when(FileSystem.exists("c.txt")).thenReturn(true);
        when(FileSystem.exists("folder/d.txt")).thenReturn(true);
        when(FileSystem.exists("a.txt")).thenReturn(false);
        when(FileSystem.exists("b.txt")).thenReturn(false);
        when(FileSystem.exists("d.txt")).thenReturn(true);

        when(FileSystem.readAllBytes("c.txt")).thenReturn("tc".getBytes());
        when(FileSystem.readAllBytes("folder/d.txt")).thenReturn("td".getBytes());
        when(FileSystem.readAllBytes("d.txt")).thenReturn("d".getBytes());

        when(VcsObject.readFromJson(anyString(), any()))
                .thenReturn(thirdCommit).thenReturn(thirdCBlob).thenReturn(thirdDBlob)
                .thenReturn(secondCommit).thenReturn(secondABlob).thenReturn(secondBBlob)
                .thenReturn(firstCommit).thenReturn(firstDBlob)
                .thenReturn(initialCommit);

        Vcs.merge("branch");
    }

    @Test(expected = Vcs.VcsBranchActionForbiddenException.class)
    public void mergeSameBranch() throws IOException, Vcs.VcsIllegalStateException, Vcs.VcsBranchActionForbiddenException, Vcs.VcsBranchNotFoundException, Vcs.VcsConflictException {
        when(FileSystem.getFirstLine(anyString())).thenReturn("branch");
        Vcs.merge("branch");
    }

    @Test(expected = Vcs.VcsIllegalStateException.class)
    public void uncommittedChanges() throws Vcs.VcsConflictException, Vcs.VcsBranchActionForbiddenException, Vcs.VcsBranchNotFoundException, Vcs.VcsIllegalStateException, IOException {
        when(FileSystem.getFirstLine(anyString())).thenReturn("master").thenReturn("a.txt");
        Vcs.merge("branch");
    }

}
