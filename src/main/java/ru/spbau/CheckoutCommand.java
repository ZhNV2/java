package ru.spbau;

import ru.spbau.zhidkov.BranchHandler;
import ru.spbau.zhidkov.CommitHandler;
import ru.spbau.zhidkov.ExternalFileHandler;
import ru.spbau.zhidkov.VcsFileHandler;
import ru.spbau.zhidkov.vcs.VcsBlob;
import ru.spbau.zhidkov.vcs.VcsCommit;
import ru.spbau.zhidkov.vcs.VcsObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;


/**
 * Class implementing checkout command.
 */
public class CheckoutCommand {

    private BranchHandler branchHandler;
    private CommitHandler commitHandler;
    private VcsFileHandler vcsFileHandler;
    private ExternalFileHandler externalFileHandler;

    public CheckoutCommand(BranchHandler branchHandler, CommitHandler commitHandler, VcsFileHandler vcsFileHandler, ExternalFileHandler externalFileHandler) {
        this.branchHandler = branchHandler;
        this.commitHandler = commitHandler;
        this.vcsFileHandler = vcsFileHandler;
        this.externalFileHandler = externalFileHandler;
    }

    /**
     * Switches current branchHandler to another.
     *
     * @param branchName branchHandler to switch to
     * @throws IOException                if something has gone wrong during
     *                                    the work with file system
     * @throws Vcs.VcsBranchNotFoundException when trying to access branchHandler
     *                                    which doesn't exist.
     * @throws Vcs.VcsIncorrectUsageException   when vcs can't perform command because of incorrect
     *                                    usage
     */
    public void checkoutBranch(String branchName) throws IOException, Vcs.VcsBranchNotFoundException, Vcs.VcsIncorrectUsageException {
        branchHandler.assertBranchExists(branchName);
        String commitHash = branchHandler.getBranchCommit(branchName).getHash();
        checkout(commitHash);

        branchHandler.setHead(branchName);
        //FileSystem.writeStringToFile(Vcs.getHEAD(), branchName);
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
    public void checkoutRevision(String commitHash) throws Vcs.VcsBranchNotFoundException, IOException, Vcs.VcsIncorrectUsageException {
        commitHandler.assertRevisionExists(commitHash);
        checkout(commitHash);

        branchHandler.setCommitHash(branchHandler.getHeadName(), commitHash);
//        FileSystem.writeStringToFile(Vcs.getBranchesDir() + File.separator + BranchCommand.getHeadBranch(), commitHash);
    }

    private void checkout(String commitHash) throws IOException, Vcs.VcsIncorrectUsageException {
        String lastCommitHash = branchHandler.getHeadLastCommitHash();
        if (lastCommitHash.equals(commitHash)) return;
        vcsFileHandler.assertListEmpty(VcsFileHandler.ListWithFiles.ADD_LIST);
        vcsFileHandler.assertListEmpty(VcsFileHandler.ListWithFiles.RM_LIST);
        deleteCommittedFiles(lastCommitHash);
        Collection<Path> restored = new HashSet<>();
        restore(commitHash, restored);
    }

    private void deleteCommittedFiles(String commitHash) throws IOException {
        VcsCommit commit = vcsFileHandler.getCommit(commitHash);
        for (Map.Entry<Path, String> entry : commit.getChildrenAdd().entrySet()) {
            externalFileHandler.deleteIfExists(entry.getKey());
        }
        if (!commit.getPrevCommitHash().equals(CommitHandler.getInitialCommitPrevHash())) {
            deleteCommittedFiles(commit.getPrevCommitHash());
        }
    }

    private void restore(String commitHash, Collection<Path> restored) throws IOException {
        VcsCommit commit = vcsFileHandler.getCommit(commitHash);
        for (Map.Entry<Path, String> entry : commit.getChildrenAdd().entrySet()) {
            if (!restored.contains(entry.getKey())) {
                VcsBlob blob = vcsFileHandler.getBlob(entry.getValue());
                //VcsBlob blob = (VcsBlob) VcsObject.readFromJson(Vcs.getObjectsDir() + File.separator + entry.getValue(), VcsBlob.class);
                externalFileHandler.writeBytesToFile(entry.getKey(), blob.getContent());
                restored.add(entry.getKey());
            }
        }
        for (Path fileRm : commit.getChildrenRm()) {
            restored.add(fileRm);
        }
        if (!commit.getPrevCommitHash().equals(CommitHandler.getInitialCommitPrevHash())) {
            restore(commit.getPrevCommitHash(), restored);
        }
    }

//    private void checkIfRevisionExist(String commitHash) throws IOException, Vcs.VcsRevisionNotFoundException {
//        if (!FileSystem.exists(Vcs.getObjectsDir() + File.separator + commitHash)) {
//            throw new Vcs.VcsRevisionNotFoundException("Provided revision doesn't exist");
//        }
//    }
}
