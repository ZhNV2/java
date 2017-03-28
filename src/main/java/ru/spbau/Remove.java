package ru.spbau;

import ru.spbau.zhidkov.vcs.FileSystem;

import java.io.File;
import java.io.IOException;

import static ru.spbau.Init.hasInitialized;

public class Remove {

    public static void remove(String file) throws IOException, Vcs.VcsIncorrectUsageException {
        if (!hasInitialized()) throw new Vcs.VcsIncorrectUsageException(Vcs.getUninitializedRepoMessage());
        file = FileSystem.normalize(Vcs.getCurrentFolder() + File.separator + file);
        if (!Commit.isFileInCurrentRevision(file)) {
            throw new Vcs.VcsIncorrectUsageException("Specified file has never occur in repository");
        }
        FileSystem.removeFromFileLine(Vcs.getAddList(), file);
        FileSystem.appendToFile(Vcs.getRmList(), (file + System.lineSeparator()).getBytes());
    }

}
