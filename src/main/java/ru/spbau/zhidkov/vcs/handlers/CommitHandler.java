package ru.spbau.zhidkov.vcs.handlers;

import org.jetbrains.annotations.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.zhidkov.vcs.commands.Vcs;
import ru.spbau.zhidkov.vcs.vcsObjects.VcsCommit;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**Handler for operations with commits */
public class CommitHandler {

    private static final Logger logger = LogManager.getLogger(CommitHandler.class);
    private static final String INITIAL_COMMIT_MESSAGE = "Initial commit.";
    private static final String INITIAL_COMMIT_PREV_HASH = "";

    private VcsFileHandler vcsFileHandler;

    public static String getInitialCommitMessage() {
        return INITIAL_COMMIT_MESSAGE;
    }

    public static String getInitialCommitPrevHash() {
        return INITIAL_COMMIT_PREV_HASH;
    }

    /**
     * Builds instance with <tt>VcsFileHandler</tt>
     *
     * @param vcsFileHandler vcsFileHandler
     */
    public CommitHandler(VcsFileHandler vcsFileHandler) {
        this.vcsFileHandler = vcsFileHandler;
    }

    /**
     * Checks if pointed revision exist. Throws an exception
     * if so
     *
     * @param commitHash revision to check
     * @throws Vcs.VcsRevisionNotFoundException if revision
     *                                          does not exist
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void assertRevisionExists(String commitHash) throws Vcs.VcsRevisionNotFoundException, IOException {
        if (!vcsFileHandler.commitExists(commitHash)) {
            logger.error("revision {} doesn't exist", commitHash);
            throw new Vcs.VcsRevisionNotFoundException("Provided revision doesn't exist");
        }
    }

    /**
     * Return <tt>List</tt> of all files than been storing
     * in pointed revision
     *
     * @param hash revision hash
     * @return <tt>List</tt> of active revision files
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public
    @NotNull
    List<Path> getAllActiveFilesInRevision(String hash) throws IOException {
        List<Path> repFiles = new ArrayList<>();
        getAllActiveFilesInCurrentRevision(hash,
                new TreeSet<>(), repFiles);
        return repFiles;
    }

    private void getAllActiveFilesInCurrentRevision(String commitHash, @NotNull Collection<Path> checked,
                                                    @NotNull List<Path> repFiles) throws IOException {
        VcsCommit commitHandler = vcsFileHandler.getCommit(commitHash);
        for (Map.Entry<Path, String> entry : commitHandler.getChildrenAdd().entrySet()) {
            if (checked.contains(entry.getKey())) {
                continue;
            }
            repFiles.add(entry.getKey());
            checked.add(entry.getKey());
        }
        for (Path file : commitHandler.getChildrenRm()) {
            checked.add(file);
        }
        if (!commitHandler.getPrevCommitHash().equals(INITIAL_COMMIT_PREV_HASH)) {
            getAllActiveFilesInCurrentRevision(commitHandler.getPrevCommitHash(), checked, repFiles);
        }
    }
}
