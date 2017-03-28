package ru.spbau;

import ru.spbau.zhidkov.VcsBlob;
import ru.spbau.zhidkov.VcsCommit;
import ru.spbau.zhidkov.VcsObject;
import ru.spbau.zhidkov.vcs.FileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import static ru.spbau.Commit.getCommit;
import static ru.spbau.Init.hasInitialized;

/**
 * Class implementing reset command.
 */
public class Reset {

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
    public static void reset(String fileName) throws IOException, Vcs.VcsIncorrectUsageException {
        if (!hasInitialized()) throw new Vcs.VcsIncorrectUsageException(Vcs.getUninitializedRepoMessage());
        if (!FileSystem.exists(fileName)) throw new FileNotFoundException(fileName);
        findLastVersion(Branch.getBranchLastCommitHash(Branch.getHeadBranch()), fileName);
    }

    private static void findLastVersion(String commitHash, String fileName) throws IOException, Vcs.VcsIncorrectUsageException {
        VcsCommit commit = getCommit(commitHash);
        for (Map.Entry<String, String> entry : commit.getChildrenAdd().entrySet()) {
            if (FileSystem.fileNameEquals(fileName, entry.getKey())) {
                VcsBlob blob = (VcsBlob) VcsObject.readFromJson(Vcs.getObjectsDir() +
                        File.separator + entry.getValue(), VcsBlob.class);
                FileSystem.writeBytesToFile(fileName, blob.getContent());
                return;
            }
        }
        for (String file : commit.getChildrenRm()) {
            if (FileSystem.fileNameEquals(file, fileName)) {
                throw new Vcs.VcsIncorrectUsageException("Provided file does not occur in repository");
            }
        }
        if (!commit.getPrevCommitHash().equals(Vcs.getInitialCommitPrevHash())) {
            findLastVersion(commit.getPrevCommitHash(), fileName);
        } else {
            throw new Vcs.VcsIncorrectUsageException("Provided file does not occur in repository");
        }
    }
}
