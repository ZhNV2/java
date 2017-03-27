package ru.spbau;

import ru.spbau.zhidkov.FileSystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static ru.spbau.Init.hasInitialized;

/**
 * Created by Нико on 27.03.2017.
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
    public static void add(List<String> fileNames) throws IOException, Vcs.VcsIllegalStateException {
        if (!hasInitialized()) throw new Vcs.VcsIllegalStateException("There is no repository found in the current folder."
                + System.lineSeparator() + "Use init command to initialize repository");
        for (String fileName : fileNames) {
            if (!FileSystem.exists(fileName)) {
                throw new FileNotFoundException(fileName);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String fileName : fileNames) {
            stringBuilder.append(fileName);
            stringBuilder.append(System.lineSeparator());
        }

        FileSystem.appendToFile(Vcs.getAddList(), stringBuilder.toString().getBytes());
    }

}
