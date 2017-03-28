package ru.spbau;

import ru.spbau.zhidkov.vcs.FileSystem;
import ru.spbau.zhidkov.VcsBlob;
import ru.spbau.zhidkov.VcsCommit;
import ru.spbau.zhidkov.VcsObject;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import static ru.spbau.Branch.checkIfBranchExist;
import static ru.spbau.Commit.getCommit;
import static ru.spbau.Init.hasInitialized;

/**
 * Class implementing checkout command.
 */
public class Checkout {
    /**
     * Switches current branch to another.
     *
     * @param branchName branch to switch to
     * @throws IOException                if something has gone wrong during
     *                                    the work with file system
     * @throws Vcs.VcsBranchNotFoundException when trying to access branch
     *                                    which doesn't exist.
     * @throws Vcs.VcsIncorrectUsageException   when vcs can't perform command because of incorrect
     *                                    usage
     */
    public static void checkoutBranch(String branchName) throws IOException, Vcs.VcsBranchNotFoundException, Vcs.VcsIncorrectUsageException {
        if (!hasInitialized()) throw new Vcs.VcsIncorrectUsageException(Vcs.getUninitializedRepoMessage());
        checkIfBranchExist(branchName);
        String commitHash = Branch.getBranchLastCommitHash(branchName);
        checkout(commitHash);

        FileSystem.writeStringToFile(Vcs.getHEAD(), branchName);
    }

    /**
     * Switches current revision to provided.
     *
     * @param commitHash hash of revision to switch to
     * @throws IOException                  if something has gone wrong during
     *                                      the work with file system
     * @throws Vcs.VcsRevisionNotFoundException when trying to access revision
     *                                      which doesn't exist
     * @throws Vcs.VcsIncorrectUsageException     when vcs can't perform command because of incorrect
     *                                      usage
     */
    public static void checkoutRevision(String commitHash) throws IOException, Vcs.VcsRevisionNotFoundException, Vcs.VcsIncorrectUsageException {
        if (!hasInitialized()) throw new Vcs.VcsIncorrectUsageException(Vcs.getUninitializedRepoMessage());
        checkIfRevisionExist(commitHash);
        checkout(commitHash);

        FileSystem.writeStringToFile(Vcs.getBranchesDir() + File.separator + Branch.getHeadBranch(), commitHash);
    }

    private static void checkout(String commitHash) throws IOException, Vcs.VcsIncorrectUsageException {
        String lastCommitHash = Branch.getBranchLastCommitHash(Branch.getHeadBranch());
        if (lastCommitHash.equals(commitHash)) return;
        if (!FileSystem.getFirstLine(Vcs.getAddList()).equals("")) {
            throw new Vcs.VcsIncorrectUsageException("You have several files were added, but haven't committed yet");
        }
        if (!FileSystem.getFirstLine(Vcs.getRmList()).equals("")) {
            throw new Vcs.VcsIncorrectUsageException("You have several files were removed, but haven't committed yet");
        }
        deleteCommittedFiles(lastCommitHash);
        Collection<String> restored = new HashSet<>();
        restore(commitHash, restored);
    }

    private static void deleteCommittedFiles(String commitHash) throws IOException {
        VcsCommit commit = getCommit(commitHash);
        for (Map.Entry<String, String> entry : commit.getChildrenAdd().entrySet()) {
            FileSystem.deleteIfExists(entry.getKey());
        }
        if (!commit.getPrevCommitHash().equals(Vcs.getInitialCommitPrevHash())) {
            deleteCommittedFiles(commit.getPrevCommitHash());
        }
    }

    private static void restore(String commitHash, Collection<String> restored) throws IOException {
        VcsCommit commit = getCommit(commitHash);
        for (Map.Entry<String, String> entry : commit.getChildrenAdd().entrySet()) {
            if (!restored.contains(entry.getKey())) {
                VcsBlob blob = (VcsBlob) VcsObject.readFromJson(Vcs.getObjectsDir() + File.separator + entry.getValue(), VcsBlob.class);
                FileSystem.writeBytesToFile(entry.getKey(), blob.getContent());
                restored.add(entry.getKey());
            }
        }
        for (String fileRm : commit.getChildrenRm()) {
            restored.add(fileRm);
        }
        if (!commit.getPrevCommitHash().equals(Vcs.getInitialCommitPrevHash())) {
            restore(commit.getPrevCommitHash(), restored);
        }
    }

    private static void checkIfRevisionExist(String commitHash) throws IOException, Vcs.VcsRevisionNotFoundException {
        if (!FileSystem.exists(Vcs.getObjectsDir() + File.separator + commitHash)) {
            throw new Vcs.VcsRevisionNotFoundException("Provided revision doesn't exist");
        }
    }
}
