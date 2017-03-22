package test.ru.spbau;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.spbau.FileSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;

public class FileSystemTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testCopyFilesToDir() throws IOException {
        File sourceDir = folder.newFolder();
        File targetDir = folder.newFolder();
        String files[] = {"a.txt", "a\\b\\c", "a\\b\\x.md", "a\\b\\y.png", "a", "a\\b", "a\\hy.txt", ""};
        List<Path> newFiles = new ArrayList<>();
        for (String fileName : files) {
            String newFileName = sourceDir + File.separator + fileName;
            createFileWithRandContent(newFileName);
            newFiles.add(Paths.get(newFileName));
        }
        FileSystem.copyFilesToDir(sourceDir.getPath(), newFiles, targetDir.getPath());
        for (String fileName : files) {
            if (!isDir(fileName)) {
                String oldFile = sourceDir + File.separator + fileName;
                String newFile = targetDir + File.separator + fileName;
                byte[] oldContent = Files.readAllBytes(Paths.get(oldFile));
                byte[] newContent = Files.readAllBytes(Paths.get(newFile));
                assertArrayEquals(oldContent, newContent);
            }
        }

    }

    private void createFileWithRandContent(String fileName) throws IOException {
        if (!isDir(fileName)) {
            byte[] content = new byte[20];
            new Random().nextBytes(content);
            Files.createFile(Paths.get(fileName));
            Files.write(Paths.get(fileName), content);
        } else {
            Files.createDirectories(Paths.get(fileName));
        }
    }

    private boolean isDir(String fileName) {
        return !Paths.get(fileName).getFileName().toString().contains(".");
    }
}
