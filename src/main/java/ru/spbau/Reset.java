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
     * @throws Vcs.VcsIllegalStateException if provided file
     *                                      is not in repository yet
     */
    public static void reset(String fileName) throws IOException, Vcs.VcsIllegalStateException {
        if (!FileSystem.exists(fileName)) throw new FileNotFoundException(fileName);
        findLastVersion(FileSystem.getFirstLine(Vcs.getBranchesDir() +
                File.separator + Branch.getHeadBranch()), fileName);
    }

    private static void findLastVersion(String commitHash, String fileName) throws IOException, Vcs.VcsIllegalStateException {
        VcsCommit commit = getCommit(commitHash);
        for (Map.Entry<String, String> entry : commit.getChildren().entrySet()) {
            if (FileSystem.fileNameEquals(fileName, entry.getKey())) {
                VcsBlob blob = (VcsBlob) VcsObject.readFromJson(Vcs.getObjectsDir() +
                        File.separator + entry.getValue(), VcsBlob.class);
                FileSystem.writeBytesToFile(fileName, blob.getContent());
                return;
            }
        }
        if (!commit.getPrevCommitHash().equals(Vcs.getInitialCommitPrevHash())) {
            findLastVersion(commit.getPrevCommitHash(), fileName);
        } else {
            throw new Vcs.VcsIllegalStateException("Provided file does not occur in repository");
        }
    }
}
