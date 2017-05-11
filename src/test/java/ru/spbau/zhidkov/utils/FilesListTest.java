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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/** Class testing (de)serialization of <tt>FilesList</tt> class */
public class FilesListTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void serializeDeserialize() throws IOException {
        FilesList filesList = new FilesList(ImmutableMap.of(Paths.get("a"), FilesList.FileType.FILE, Paths.get("b\\c\\d"),
                FilesList.FileType.FOLDER, Paths.get("d\\g\\q"), FilesList.FileType.FILE));

        FilesList filesList1 = FilesList.formByteArray(filesList.toByteArray());
        assertTrue(CollectionUtils.isEqualCollection(filesList.getFiles().entrySet(), filesList1.getFiles().entrySet()));
    }

    @Test
    public void deserializeSerialize() throws IOException {
        FilesList filesList = new FilesList(ImmutableMap.of(Paths.get("a"), FilesList.FileType.FILE, Paths.get("b\\c\\d"),
                FilesList.FileType.FOLDER, Paths.get("d\\g\\q"), FilesList.FileType.FILE));
        byte[] byteList = filesList.toByteArray();
        assertArrayEquals(byteList, FilesList.formByteArray(byteList).toByteArray());
    }
}
