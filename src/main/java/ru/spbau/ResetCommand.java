package ru.spbau;

import ru.spbau.zhidkov.BranchHandler;
import ru.spbau.zhidkov.CommitHandler;
import ru.spbau.zhidkov.ExternalFileHandler;
import ru.spbau.zhidkov.VcsFileHandler;
import ru.spbau.zhidkov.vcs.VcsBlob;
import ru.spbau.zhidkov.vcs.VcsCommit;
import ru.spbau.zhidkov.vcs.FileSystem;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;


/**
 * Class implementing reset command.
 */
public class ResetCommand {

    private VcsFileHandler vcsFileHandler;
    private ExternalFileHandler externalFileHandler;
    private BranchHandler branchHandler;

    public ResetCommand(VcsFileHandler vcsFileHandler, ExternalFileHandler externalFileHandler, BranchHandler branchHandler) {
        this.vcsFileHandler = vcsFileHandler;
        this.externalFileHandler = externalFileHandler;
        this.branchHandler = branchHandler;
    }

    /**
     * Resets specified file to its last version been
     * storing in repository.
     *
     * @param fileName file to reset
     * @throws IOException                  if something has gone wrong during
     *                                      the work with file system
     * @throws Vcs.VcsIncorrectUsageException if provided file
     *                                      is not in repository yet
     */
    public void reset(String fileName) throws Vcs.VcsIncorrectUsageException, IOException {
        //if (!FileSystem.exists(fileName)) throw new FileNotFoundException(fileName);
        findLastVersion(branchHandler.getHeadLastCommitHash(), fileName);
        vcsFileHandler.removeFromList(VcsFileHandler.ListWithFiles.ADD_LIST, Collections.singletonList(fileName));
        vcsFileHandler.removeFromList(VcsFileHandler.ListWithFiles.RM_LIST, Collections.singletonList(fileName));
    }

    private void findLastVersion(String commitHash, String fileName) throws Vcs.VcsIncorrectUsageException, IOException {
        fileName = externalFileHandler.normalize(fileName);
        VcsCommit commit = vcsFileHandler.getCommit(commitHash);
        for (Map.Entry<String, String> entry : commit.getChildrenAdd().entrySet()) {
            if (fileName.equals(entry.getKey())) {
                VcsBlob blob = vcsFileHandler.getBlob(entry.getValue());
                externalFileHandler.writeBytesToFile(fileName, blob.getContent());
                return;
            }
        }
        for (String file : commit.getChildrenRm()) {
            if (fileName.equals(file)) {
                throw new Vcs.VcsIncorrectUsageException("Provided file is not been storing in the current repository, try to reset it from necessary revision");
            }
        }
        if (!commit.getPrevCommitHash().equals(CommitHandler.getInitialCommitPrevHash())) {
            findLastVersion(commit.getPrevCommitHash(), fileName);
        } else {
            throw new Vcs.VcsIncorrectUsageException("Provided file does not occur in repository");
        }
    }
}
