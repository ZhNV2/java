package ru.spbau.zhidkov;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.spbau.zhidkov.client.Client;
import ru.spbau.zhidkov.server.Server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public void preparation() throws IOException {
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
        server = Server.buildServer("localhost", 1234, Paths.get(temporaryFolderServer.getRoot().getAbsolutePath()));
        client = Client.buildClient("localhost", 1234, Paths.get(temporaryFolderClient.getRoot().getAbsolutePath()));

        new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        client.connect();
    }

    @Test(timeout = 5000)
    public void performList() throws IOException {
        Map<Path, Boolean> res = client.executeList(Paths.get("."));
        assertTrue(CollectionUtils.isEqualCollection(res.entrySet(), dirs.entrySet()));
    }

    @Test(timeout = 5000)
    public void performGet() throws IOException {
        Files.write(Paths.get(temporaryFolderServer.getRoot().getAbsolutePath()).resolve(Paths.get("./dir1/c")),
                "hello!".getBytes());
        client.executeGet(Paths.get("./dir1/c"));
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
