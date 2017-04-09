package ru.spbau.zhidkov;

import com.sun.istack.internal.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.ResetCommand;
import ru.spbau.Vcs;
import ru.spbau.zhidkov.vcs.VcsCommit;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by Нико on 30.03.2017.
 */
public class CommitHandler {
    private static final Logger logger = LogManager.getLogger(CommitHandler.class);
    private VcsFileHandler vcsFileHandler;
    private static final String INITIAL_COMMIT_MESSAGE = "Initial commit.";
    private static final String INITIAL_COMMIT_PREV_HASH = "";

    public CommitHandler(VcsFileHandler vcsFileHandler) {
        this.vcsFileHandler = vcsFileHandler;
    }

    public static String getInitialCommitMessage() {
        return INITIAL_COMMIT_MESSAGE;
    }

    public void assertRevisionExists(String commitHash) throws Vcs.VcsRevisionNotFoundException, IOException {
        if (!vcsFileHandler.commitExists(commitHash)) {
            logger.error("revision {} doesn't exist", commitHash);
            throw new Vcs.VcsRevisionNotFoundException("Provided revision doesn't exist");
        }
    }

    public static String getInitialCommitPrevHash() {
        return INITIAL_COMMIT_PREV_HASH;
    }

    public @NotNull List<Path> getAllActiveFilesInRevision(String hash) throws IOException {
        List<Path> repFiles = new ArrayList<>();
        getAllActiveFilesInCurrentRevision(hash,
                new TreeSet<>(), repFiles);
        return repFiles;
    }

    private void getAllActiveFilesInCurrentRevision(String commitHash, @NotNull Collection<Path> checked,
                                                           @NotNull List<Path> repFiles) throws IOException {
        VcsCommit commitHandler = vcsFileHandler.getCommit(commitHash);
        for (Map.Entry<Path, String> entry : commitHandler.getChildrenAdd().entrySet()) {
            if (checked.contains(entry.getKey())) continue;
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
