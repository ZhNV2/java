package test.ru.spbau.zhidkov.vcs;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.spbau.zhidkov.vcs.file.FileSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class FileSystemTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testCopyFilesToDir() throws IOException {
        File sourceDir = folder.newFolder();
        File targetDir = folder.newFolder();
        Path files[] = {Paths.get("a.txt"), Paths.get("a\\b\\c"), Paths.get("a\\b\\x.md"),
                Paths.get("a\\b\\y.png"), Paths.get("a"), Paths.get("a\\b"),
                Paths.get("a\\hy.txt"), Paths.get("")};
        List<Path> newFiles = new ArrayList<>();
        for (Path fileName : files) {
            Path newFileName = Paths.get(sourceDir.getAbsolutePath()).resolve(fileName);
            createFileWithRandContent(newFileName);
            newFiles.add(newFileName);
        }
        FileSystem fileSystem = new FileSystem(Paths.get(folder.getRoot().getAbsolutePath()));
        fileSystem.copyFilesToDir(sourceDir.toPath(), newFiles, targetDir.toPath());
        for (Path fileName : files) {
            if (!isDir(fileName)) {
                Path oldFile = sourceDir.toPath().resolve(fileName);
                Path newFile = targetDir.toPath().resolve(fileName);
                byte[] oldContent = Files.readAllBytes(oldFile);
                byte[] newContent = Files.readAllBytes(newFile);
                assertArrayEquals(oldContent, newContent);
            }
        }
    }

    @Test
    public void deleteDirTest() throws IOException {
        Path tmp = Paths.get("tmp");
        Path files[] = {tmp, tmp.resolve("a.txt"), tmp.resolve("bbadfsa.tx"), tmp.resolve("c"), tmp.resolve("d"),
                tmp.resolve("c").resolve("a.doc"), tmp.resolve("d").resolve("a.ppx")};
        for (Path file : files) {
            if (!isDir(file)) {
                folder.newFile(file.toString());
            } else {
                folder.newFolder(file.toString());
            }
        }
        FileSystem fileSystem = new FileSystem(Paths.get(folder.getRoot().getAbsolutePath()));
        fileSystem.deleteFolder(tmp);
        assertEquals(folder.getRoot().list().length, 0);
    }

    private void createFileWithRandContent(Path fileName) throws IOException {
        if (!isDir(fileName)) {
            byte[] content = new byte[20];
            new Random().nextBytes(content);
            Files.createFile(fileName);
            Files.write(fileName, content);
        } else {
            Files.createDirectories(fileName);
        }
    }

    private boolean isDir(Path fileName) {
        return !fileName.getFileName().toString().contains(".");
    }
}
