package ru.spbau;

import ru.spbau.zhidkov.vcs.FileSystem;

import java.io.IOException;

import static ru.spbau.Commit.commit;

/**
 * Class implementing init command.
 */
public class Init {

    /**
     * Initializes repo in the current folder.
     *
     * @param authorName author name
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void init(String authorName) throws IOException, Vcs.VcsIncorrectUsageException {
        if (hasInitialized()) throw new Vcs.VcsIncorrectUsageException("Repository is been already initialized in the current folder");
        FileSystem.createDirectory(Vcs.getRootDir());
        FileSystem.createDirectory(Vcs.getObjectsDir());
        FileSystem.createDirectory(Vcs.getBranchesDir());
        FileSystem.createDirectory(Vcs.getOneLineVarsDir());
        FileSystem.createEmptyFile(Vcs.getAddList());
        FileSystem.createEmptyFile(Vcs.getRmList());
        FileSystem.createEmptyFile(Vcs.getHEAD());
        FileSystem.createEmptyFile(Vcs.getAuthorName());
        FileSystem.writeStringToFile(Vcs.getAuthorName(), authorName);
        FileSystem.writeStringToFile(Vcs.getHEAD(), Vcs.getMASTER());
        commit(Vcs.getInitialCommitMessage());
    }

    /**
     * Checks if repo has been already initialized or not.
     *
     * @return whether repo has been already initialized or not
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static boolean hasInitialized() throws IOException {
        return FileSystem.exists(Vcs.getRootDir());
    }
}
