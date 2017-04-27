package ru.spbau.zhidkov.utils;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.spbau.zhidkov.IO.FileSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.junit.Assert.assertTrue;

/** Class testing (de)serialization of <tt>FilesList</tt> class */
public class FilesListTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void serializeDeserialize() throws IOException {
        FilesList filesList = new FilesList(ImmutableMap.of(Paths.get("a"), true, Paths.get("b\\c\\d"),
                false, Paths.get("d\\g\\q"), true));
        File file = folder.newFile();
        filesList.writeToFile(file.toPath(), new FileSystem(Paths.get(folder.getRoot().getAbsolutePath())));
        FilesList filesList1 = FilesList.buildFromFile(file.toPath(), new FileSystem(Paths.get(folder.getRoot()
                .getAbsolutePath())));
        assertTrue(CollectionUtils.isEqualCollection(filesList.getFiles().entrySet(), filesList1.getFiles().entrySet()));
    }

    @Test
    public void deserializeSerialize() throws IOException {
        File file1 = folder.newFile();
        File file2 = folder.newFile();
        Files.write(file1.toPath(), "abctrue\n".getBytes(), StandardOpenOption.APPEND);
        Files.write(file1.toPath(), "e\\f\\wqerfalse\n".getBytes(), StandardOpenOption.APPEND);
        Files.write(file1.toPath(), "ketfalse\n".getBytes(), StandardOpenOption.APPEND);
        FilesList filesList = FilesList.buildFromFile(file1.toPath(), new FileSystem(Paths.get(folder.getRoot()
                .getAbsolutePath())));
        filesList.writeToFile(file2.toPath(), new FileSystem(Paths.get(folder.getRoot().getAbsolutePath())));
        List<String> file1Strings = Files.readAllLines(file1.toPath());
        List<String> file2Strings = Files.readAllLines(file1.toPath());
        assertTrue(CollectionUtils.isEqualCollection(file1Strings, file2Strings));
    }
}
