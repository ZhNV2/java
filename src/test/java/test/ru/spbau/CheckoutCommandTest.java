package test.ru.spbau;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.spbau.Vcs;
import ru.spbau.zhidkov.vcs.FileSystem;
import ru.spbau.zhidkov.vcs.VcsBlob;
import ru.spbau.zhidkov.vcs.VcsCommit;
import ru.spbau.zhidkov.vcs.VcsObject;

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
public class CheckoutCommandTest {

//    @Before
//    public void checkoutTestSetup() throws IOException {
//        basicSetup();
//    }
//
//    @Test
//    public void checkoutBranchBasicCorrectnessTest() throws IOException, Vcs.VcsBranchNotFoundException, Vcs.VcsIncorrectUsageException {
//        VcsCommit initialCommit = new VcsCommit(Vcs.getInitialCommitMessage(), new Date(),
//                AUTHOR_NAME, Vcs.getInitialCommitPrevHash(), new HashMap<>(), new ArrayList<>());
//        HashMap<String, String> firstCommitChildren = new LinkedHashMap<>();
//        firstCommitChildren.put("a.txt", "");
//        firstCommitChildren.put("c.txt", "");
//        firstCommitChildren.put("d.txt", "");
//        VcsBlob firstDBlob = new VcsBlob("fd".getBytes());
//        VcsCommit firstCommit = new VcsCommit("first commit", new Date(), AUTHOR_NAME, "", firstCommitChildren, new ArrayList<>());
//        HashMap<String, String> secondCommitChildren = new LinkedHashMap<>();
//        VcsCommit secondCommit = new VcsCommit("second commit", new Date(), AUTHOR_NAME, "firstCommitHash", secondCommitChildren, new ArrayList<>());
//        secondCommitChildren.put("a.txt", "");
//        secondCommitChildren.put("b.txt", "");
//        VcsBlob secondABlob = new VcsBlob("sa".getBytes());
//        VcsBlob secondBBlob = new VcsBlob("sb".getBytes());
//        HashMap<String, String> thirdCommitChildren = new LinkedHashMap<>();
//        VcsCommit thirdCommit = new VcsCommit("third commit", new Date(), AUTHOR_NAME, "secondCommitHash", thirdCommitChildren, new ArrayList<>());
//        thirdCommitChildren.put("c.txt", "");
//        thirdCommitChildren.put("folder/d.txt", "");
//        VcsBlob thirdCBlob = new VcsBlob("tc".getBytes());
//        VcsBlob thirdDBlob = new VcsBlob("td".getBytes());
//        when(VcsObject.readFromJson(anyString(), any()))
//                .thenReturn(initialCommit)
//                .thenReturn(thirdCommit).thenReturn(thirdCBlob).thenReturn(thirdDBlob)
//                .thenReturn(secondCommit).thenReturn(secondABlob).thenReturn(secondBBlob)
//                .thenReturn(firstCommit).thenReturn(firstDBlob)
//                .thenReturn(initialCommit);
//
//        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<byte[]> byteCaptor = ArgumentCaptor.forClass(byte[].class);
//
//        Vcs.checkoutBranch("branch");
//
//        PowerMockito.verifyStatic(times(5));
//        FileSystem.writeBytesToFile(stringCaptor.capture(), byteCaptor.capture());
//
//        List<String> filesToCheckout = Arrays.asList("c.txt", "folder/d.txt", "a.txt", "b.txt", "d.txt");
//        List<String> contentsToCheckout = Arrays.asList("tc", "td", "sa", "sb", "fd");
//        Map<String, String> fileContent = new HashMap<>();
//        for (int i = 0; i < 5; i++) {
//            fileContent.put(filesToCheckout.get(i), contentsToCheckout.get(i));
//        }
//        for (int i = 0; i < 5; i++) {
//            String fileToCheckout = stringCaptor.getAllValues().get(i);
//            String content = new String(byteCaptor.getAllValues().get(i), StandardCharsets.UTF_8);
//            assertEquals(content, fileContent.get(fileToCheckout));
//        }
//        ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
//        PowerMockito.verifyStatic(times(1));
//        FileSystem.writeStringToFile(fileNameCaptor.capture(), textCaptor.capture());
//        assertEquals(fileNameCaptor.getValue(), HEAD);
//        assertEquals(textCaptor.getValue(), "branch");
//    }
//
//    @Test
//    public void checkoutRevisionDeletingNecessaryFilesTest() throws IOException, Vcs.VcsIncorrectUsageException, Vcs.VcsRevisionNotFoundException {
//        VcsCommit initialCommit = new VcsCommit(Vcs.getInitialCommitMessage(), new Date(),
//                AUTHOR_NAME, Vcs.getInitialCommitPrevHash(), new HashMap<>(), new ArrayList<>());
//        HashMap<String, String> firstCommitChildren = new HashMap<>();
//        firstCommitChildren.put("ff/sf/a.txt", "");
//        VcsCommit firstCommit = new VcsCommit("first commit", new Date(), AUTHOR_NAME, "", firstCommitChildren, new ArrayList<>());
//        HashMap<String, String> secondCommitChildren = new HashMap<>();
//        VcsCommit secondCommit = new VcsCommit("second commit", new Date(), AUTHOR_NAME, "firstCommitHash", secondCommitChildren, new ArrayList<>());
//        secondCommitChildren.put("ff/sf/a.txt", "");
//        secondCommitChildren.put("b.txt", "");
//        when(VcsObject.readFromJson(anyString(), any()))
//                .thenReturn(secondCommit)
//                .thenReturn(firstCommit)
//                .thenReturn(initialCommit)
//                .thenReturn(initialCommit);
//
//        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
//
//        Vcs.checkoutRevision("revision");
//
//        PowerMockito.verifyStatic(times(3));
//        FileSystem.deleteIfExists(stringCaptor.capture());
//
//        assertEquals(new HashSet<>(stringCaptor.getAllValues()).size(), 2);
//        assertTrue(stringCaptor.getAllValues().contains("ff/sf/a.txt"));
//        assertTrue(stringCaptor.getAllValues().contains("b.txt"));
//
//        ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
//        PowerMockito.verifyStatic(times(1));
//        FileSystem.writeStringToFile(fileNameCaptor.capture(), textCaptor.capture());
//        assertEquals(fileNameCaptor.getValue(), MASTER);
//        assertEquals(textCaptor.getValue(), "revision");
//    }
//
//    @Test(expected = Vcs.VcsBranchNotFoundException.class)
//    public void branchDoesNotExistTest() throws IOException, Vcs.VcsBranchNotFoundException, Vcs.VcsIncorrectUsageException {
//        when(FileSystem.exists(anyString())).thenReturn(false);
//        when(FileSystem.exists(Vcs.getRootDir())).thenReturn(true);
//        Vcs.checkoutBranch("branch");
//    }
//
//    @Test(expected = Vcs.VcsRevisionNotFoundException.class)
//    public void revisionDoesNotExistTest() throws IOException, Vcs.VcsIncorrectUsageException, Vcs.VcsRevisionNotFoundException {
//        when(FileSystem.exists(anyString())).thenReturn(false);
//        when(FileSystem.exists(Vcs.getRootDir())).thenReturn(true);
//        Vcs.checkoutRevision("revision");
//    }
//
//    @Test(expected = Vcs.VcsIncorrectUsageException.class)
//    public void uncommittedChanges() throws IOException, Vcs.VcsBranchNotFoundException, Vcs.VcsIncorrectUsageException {
//        when(FileSystem.exists(anyString())).thenReturn(true);
//        when(FileSystem.getFirstLine(anyString())).thenReturn("branch").thenReturn("master")
//                .thenReturn("hash").thenReturn("a.txt");
//        Vcs.checkoutBranch("branch");
//
//    }
}
