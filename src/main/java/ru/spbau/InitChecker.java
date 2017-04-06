package ru.spbau;

import ru.spbau.zhidkov.VcsFileHandler;

import java.io.IOException;

/**
 * Created by Нико on 05.04.2017.
 */
public class InitChecker {

    private VcsFileHandler vcsFileHandler;

    public InitChecker(VcsFileHandler vcsFileHandler) {
        this.vcsFileHandler = vcsFileHandler;
    }

    /**
     * Checks if repo has been already initialized or not.
     *
     * @return whether repo has been already initialized or not
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public boolean hasInitialized() throws IOException {
        return vcsFileHandler.repoExists();
        //return FileSystem.exists(Vcs.getRootDir());
    }
}
