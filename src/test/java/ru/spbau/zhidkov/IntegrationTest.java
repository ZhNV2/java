package ru.spbau.zhidkov;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.spbau.zhidkov.client.Client;
import ru.spbau.zhidkov.server.Server;
import ru.spbau.zhidkov.utils.FilesList;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Integration tests */
public class IntegrationTest {

    private Server server;
    private Client client;

    @Rule
    public TemporaryFolder temporaryFolderServer = new TemporaryFolder();

    @Rule
    public TemporaryFolder temporaryFolderClient = new TemporaryFolder();

    private Map<Path, Boolean> dirs = ImmutableMap.of(Paths.get("./a"), false, Paths.get("./dir1"), true,
            Paths.get("./dir1/c"), false, Paths.get("./dir1/dir2"), true, Paths.get("."), true);

    @Before
    public void preparation() throws IOException, InterruptedException {
        for (Map.Entry<Path, Boolean> entry : dirs.entrySet()) {
            if (entry.getKey().equals(Paths.get("."))) {
                continue;
            }
            if (entry.getValue()) {
                temporaryFolderServer.newFolder(entry.getKey().toString());
            } else {
                temporaryFolderServer.newFile(entry.getKey().toString());
            }
        }
        server = Server.buildServer(12345, Paths.get(temporaryFolderServer.getRoot().getAbsolutePath()));
        client = Client.buildClient("localhost", 12345, Paths.get(temporaryFolderClient.getRoot().getAbsolutePath()));

        new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(1000);
        client.connect();
    }

    @Test(timeout = 5000)
    public void test() throws IOException {
        performList();
        performGet();

    }

    private void performList() throws IOException {
        Map<Path, FilesList.FileType> res = client.executeList(Paths.get("."));
        Map<Path, FilesList.FileType> realRes = new HashMap<>();
        realRes.put(Paths.get("a"), FilesList.FileType.FILE);
        realRes.put(Paths.get("dir1"), FilesList.FileType.FOLDER);
        realRes.put(Paths.get(".."), FilesList.FileType.FOLDER);
        assertTrue(CollectionUtils.isEqualCollection(res.entrySet(), realRes.entrySet()));
    }

    private void performGet() throws IOException {
        Files.write(Paths.get(temporaryFolderServer.getRoot().getAbsolutePath()).resolve(Paths.get("./dir1/c")),
                "hello!".getBytes());
        client.executeGet(Paths.get("./dir1/c"), Paths.get(temporaryFolderClient.getRoot().getAbsolutePath())
                .resolve(Paths.get("./dir1/c")));
        byte[] bytes = Files.readAllBytes(Paths.get(temporaryFolderClient.getRoot().getAbsolutePath())
                .resolve(Paths.get("./dir1/c")));
        String res = new String(bytes, StandardCharsets.UTF_8);
        assertEquals("hello!", res);
    }

    @After
    public void finish() throws IOException {
        client.disconnect();
        server.stop();
    }

}
