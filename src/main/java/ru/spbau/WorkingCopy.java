package ru.spbau;

import ru.spbau.zhidkov.vcs.FileSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Нико on 27.03.2017.
 */
public class WorkingCopy {
    /**
     * Saves all files that are not relating to vcs in temporary storage.
     *
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void saveWorkingCopy() throws IOException {
        List<Path> files = FileSystem.readAllFiles(Vcs.getCurrentFolder()).stream()
                .filter(v -> !v.startsWith(Vcs.getRootDir()))
                .collect(Collectors.toList());
        FileSystem.copyFilesToDir(Vcs.getCurrentFolder(), files, Vcs.getWorkingCopy());
    }

    /**
     * Returns folder files to their original state.
     *
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void restoreWorkingCopy() throws IOException {
        System.out.println("Restoring working copy");
        List<Path> filesInRevOrd = FileSystem.readAllFiles(Vcs.getCurrentFolder()).stream()
                .sorted(FileSystem.compByLengthRev)
                .collect(Collectors.toList());
        for (Path fileName : filesInRevOrd) {
            if (!fileName.startsWith(Vcs.getRootDir()) && !fileName.startsWith(Vcs.getWorkingCopy())) {
                if (!FileSystem.fileNameEquals(fileName.toString(), Vcs.getCurrentFolder()))
                    FileSystem.deleteIfExists(fileName.toString());
            }
        }
        FileSystem.copyFilesToDir(Vcs.getWorkingCopy(), FileSystem.readAllFiles(Vcs.getWorkingCopy()), Vcs.getCurrentFolder());
        clearWorkingCopy();
    }

    /**
     * Deletes temporary storage.
     *
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void clearWorkingCopy() throws IOException {
        FileSystem.deleteFolder(Vcs.getWorkingCopy());
    }
}
