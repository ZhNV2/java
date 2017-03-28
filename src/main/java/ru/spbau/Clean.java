package ru.spbau;


import ru.spbau.zhidkov.VcsCommit;
import ru.spbau.zhidkov.vcs.FileSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.spbau.Commit.getCommit;
import static ru.spbau.Init.hasInitialized;

public class Clean {

    public static void clean() throws IOException, Vcs.VcsIncorrectUsageException {
        if (!hasInitialized()) throw new Vcs.VcsIncorrectUsageException(Vcs.getUninitializedRepoMessage());
        if (!FileSystem.getFirstLine(Vcs.getAddList()).equals("")) {
            throw new Vcs.VcsIncorrectUsageException("You have several files were added, but haven't committed yet");
        }
        if (!FileSystem.getFirstLine(Vcs.getRmList()).equals("")) {
            throw new Vcs.VcsIncorrectUsageException("You have several files were removed, but haven't committed yet");
        }
        List<String> repFiles = Commit.getAllActiveFilesInCurrentRevision();
        for (String fileName : FileSystem.readAllFiles(Vcs.getCurrentFolder()).stream()
                .sorted(FileSystem.compByLengthRev)
                .map(Path::toString)
                .collect(Collectors.toList())) {
            if (!fileName.startsWith(Vcs.getRootDir()) && !fileName.startsWith(Vcs.getWorkingCopy()) &&
                    !FileSystem.fileNameEquals(fileName, Vcs.getCurrentFolder()) &&
                            !repFiles.contains(fileName)) {
                if (FileSystem.isDirectory(fileName)) {
                    try {
                        FileSystem.deleteFolder(fileName);
                    } catch (DirectoryNotEmptyException e) {
                        // It's ok
                    }
                } else {
                    FileSystem.deleteIfExists(fileName);
                }
            }
        }
    }



}
