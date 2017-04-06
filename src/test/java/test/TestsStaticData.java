package test;

import org.junit.rules.TemporaryFolder;
import org.powermock.api.mockito.PowerMockito;
import ru.spbau.zhidkov.vcs.FileSystem;
import ru.spbau.Vcs;
import ru.spbau.zhidkov.vcs.VcsObject;

import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
public class TestsStaticData {
//    public static final String AUTHOR_NAME = "Stalevar";
//    public static final String VCS = File.separator + ".vcs";
//    public static final String OBJECTS = File.separator + ".vcs" + File.separator + "objects";
//    public static final String BRANCHES = File.separator + ".vcs" + File.separator + "branches";
//    public static final String MASTER = File.separator + ".vcs" + File.separator + "branches" + File.separator + "master";
//    public static final String HEAD = File.separator + ".vcs" + File.separator + "one_lines_vars" + File.separator + "HEAD";
//    public static final String AUTHOR_FILE = File.separator + ".vcs" + File.separator + "one_lines_vars" + File.separator + "AUTHOR_NAME";
//
//    public static void basicVcsFolderSetup(TemporaryFolder folder, File workFolder) throws IOException {
//        folder.newFolder(workFolder.getName() + VCS);
//        folder.newFolder(workFolder.getName() + OBJECTS);
//        folder.newFolder(workFolder.getName() + BRANCHES);
//        folder.newFile(workFolder.getName() + MASTER);
//    }
//
//    public static void basicSetup() throws IOException {
//        PowerMockito.mockStatic(FileSystem.class);
//        PowerMockito.mockStatic(VcsObject.class);
//        Vcs.setCurrentFolder("");
//        when(FileSystem.exists(anyString())).thenReturn(true);
//        when(FileSystem.getFirstLine(eq(BRANCHES + File.separator + "branch"))).thenReturn("branchHash");
//        when(FileSystem.getFirstLine(eq(HEAD))).thenReturn("master");
//        when(FileSystem.getFirstLine(eq(BRANCHES + File.separator + "master"))).thenReturn("masterHash");
//        when(FileSystem.getFirstLine(eq(Vcs.getAddList()))).thenReturn("");
//        when(FileSystem.getFirstLine(eq(Vcs.getRmList()))).thenReturn("");
//        when(FileSystem.getFirstLine(eq(AUTHOR_FILE))).thenReturn(AUTHOR_NAME);
//    }
}
