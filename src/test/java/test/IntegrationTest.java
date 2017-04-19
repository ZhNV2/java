package test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.Main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(System.class)
public class IntegrationTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void prepareSystemGetProperties() {
        System.setProperty("user.dir", folder.getRoot().getAbsolutePath());
    }

    @Test
    public void scenario1Test() throws IOException {
        Main.main(new String[]{"init", "--author", "me"});
        Main.main(new String[]{"branch", "-n", "bb"});
        folder.newFile("a");
        folder.newFile("b");
        Main.main(new String[]{"add", getPath("a"), getPath("b")});
        Main.main(new String[]{"commit", "-m", "a, b"});
        Main.main(new String[]{"checkout", "-b", "bb"});
        assertEquals(folder.getRoot().list().length, 1);
        Main.main(new String[]{"checkout", "-b", "master"});
        assertEquals(folder.getRoot().list().length, 3);
        Files.write(Paths.get(getPath("a")), "a".getBytes());
        Main.main(new String[]{"reset", "-f", getPath("a")});
        assertEquals(Files.readAllBytes(Paths.get(getPath("a"))).length, 0);
        folder.newFolder("c");
        folder.newFile("c/d");
        Main.main(new String[]{"clean"});
        assertEquals(folder.getRoot().list().length, 3);
        Main.main(new String[]{"log"});
        Main.main(new String[]{"status"});
        Main.main(new String[]{"branch", "-n", "cc"});
        Main.main(new String[]{"rm", getPath("a")});
        assertEquals(folder.getRoot().list().length, 2);
        Main.main(new String[]{"commit", "-m", "rm a"});
        Main.main(new String[]{"checkout", "-b", "cc"});
        assertEquals(folder.getRoot().list().length, 3);
        Main.main(new String[]{"checkout", "-b", "master"});
        assertEquals(folder.getRoot().list().length, 2);
        Main.main(new String[]{"merge", "-b", "cc"});
        assertEquals(folder.getRoot().list().length, 3);
        assertEquals(Files.readAllBytes(Paths.get(getPath("a"))).length, 0);
        Main.main(new String[]{"checkout", "-b", "bb"});
        assertEquals(folder.getRoot().list().length, 1);
    }

    @Test
    public void scenario2Test() throws IOException {
        Main.main(new String[]{"init", "--author", "me"});
        folder.newFolder("dir");
        folder.newFile("dir/a");
        Main.main(new String[]{"add", getPath("dir/a")});
        Main.main(new String[]{"commit", "-m", "a"});
        Main.main(new String[]{"branch", "-n", "x"});
        Main.main(new String[]{"branch", "-d", "x"});
        Main.main(new String[]{"branch", "-n", "x"});
        folder.newFolder("dir/dir2");
        folder.newFile("dir/dir2/c");
        Main.main(new String[]{"add", getPath("dir/dir2/c")});
        Main.main(new String[]{"commit", "-m", "b"});
        Main.main(new String[]{"clean"});
        assertEquals(folder.getRoot().list().length, 2);
        Main.main(new String[]{"checkout", "-b", "x"});
        assertEquals(folder.getRoot().list().length, 2);
        Main.main(new String[]{"merge", "-b", "master"});
        assertEquals(folder.getRoot().list().length, 2);
        Main.main(new String[]{"rm", getPath("dir/a")});
        assertEquals(folder.getRoot().list().length, 2);
        Main.main(new String[]{"commit", "-m", "c"});
        Main.main(new String[]{"rm", getPath("dir/dir2/c")});
        Main.main(new String[]{"commit", "-m", "d"});
        assertEquals(folder.getRoot().list().length, 2);
        Main.main(new String[]{"clean"});
        assertEquals(folder.getRoot().list().length, 1);
        Main.main(new String[]{"checkout", "-b", "master"});
        assertEquals(folder.getRoot().list().length, 2);
        Main.main(new String[]{"merge", "-b", "x"});
        assertEquals(folder.getRoot().list().length, 2);
    }


    private String getPath(String path) {
        return Paths.get(folder.getRoot().getAbsolutePath()).resolve(path).toString();
    }
}
