package ru.spbau;

import ru.spbau.zhidkov.vcs.FileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static ru.spbau.Init.hasInitialized;

/**
 * Class implementing add command.
 */
public class Add {
    /**
     * Adds files to repo (adds them to temporary list of
     * files to add).
     *
     * @param fileNames list of files to add.
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void add(List<String> fileNames) throws IOException, Vcs.VcsIncorrectUsageException {
        if (!hasInitialized()) throw new Vcs.VcsIncorrectUsageException(Vcs.getUninitializedRepoMessage());
        fileNames = fileNames.stream()
                .map(s->Vcs.getCurrentFolder() + File.separator + s)
                .map(FileSystem::normalize)
                .collect(Collectors.toList());

        for (String fileName : fileNames) {
            if (!FileSystem.exists(fileName)) {
                throw new FileNotFoundException(fileName);
            }
            if (FileSystem.isDirectory(fileName)) {
                throw new Vcs.VcsIncorrectUsageException("You may add only files");
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String fileName : fileNames) {
            stringBuilder.append(fileName);
            stringBuilder.append(System.lineSeparator());
            FileSystem.removeFromFileLine(Vcs.getRmList(), fileName);
        }

        FileSystem.appendToFile(Vcs.getAddList(), stringBuilder.toString().getBytes());
    }

}
