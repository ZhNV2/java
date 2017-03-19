package test;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.spbau.zhidkov.VcsCommit;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class VcsObjectTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void serializeDeserializeTest() throws IOException {
        File tmpFile = folder.newFile();
        Map<String, String> children = new HashMap<>();
        children.put("a", "b");
        children.put("c", "d");
        VcsCommit vcsCommit = new VcsCommit("Hello!", new Date(), "me", "skfdjasd", children );
        System.out.println(tmpFile.getPath());
        vcsCommit.writeAsJson(tmpFile.getPath());
        VcsCommit copyCommit = (VcsCommit) VcsCommit.readFromJson(tmpFile.getPath(), VcsCommit.class);
        assertEquals(vcsCommit, copyCommit);

    }

    @Test
    public void deserializeSerializeTest() throws IOException {
        File tmpFile = folder.newFile();
        String JSON = "{\"message\":\"ab\",\"date\":\"Mar 19, 2017 10:22:35 PM\",\"author\":\"me\",\"prevCommitHash\":\"ebed6da2325de3d914c75c3f22b60e8567cc84c6\",\"children\":{\".\\\\b.txt\":\"8d38afaefeab15b52147ca37bcbd9b9ea4626057\",\".\\\\a.txt\":\"b3ae771e8e1676aa6f9fbea2425ffa89fedf3b24\"}}";
        Files.write(Paths.get(tmpFile.getPath()), JSON.getBytes());
        VcsCommit vcsCommit = (VcsCommit) VcsCommit.readFromJson(tmpFile.getPath(), VcsCommit.class);
        File tmpFile2 = folder.newFile();
        vcsCommit.writeAsJson(tmpFile2.getPath());
        byte[] JSON2bytes = Files.readAllBytes(Paths.get(tmpFile2.getPath()));
        String JSON2 = new String(JSON2bytes, StandardCharsets.UTF_8);
        System.out.println(JSON);
        System.out.println(JSON2);
        assertEquals(JSON, JSON2);
    }

    @After
    public void deleteTemporaryFolder() {
        folder.delete();
    }


}
