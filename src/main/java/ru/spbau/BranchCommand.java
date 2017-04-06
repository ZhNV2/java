package ru.spbau;

import ru.spbau.zhidkov.BranchHandler;
import ru.spbau.zhidkov.VcsFileHandler;

import java.io.IOException;

/**
 * Class implementing branchHandler command.
 */
public class BranchCommand {

    private BranchHandler branchHandler;
    private VcsFileHandler vcsFileHandler;

    public BranchCommand(BranchHandler branchHandler, VcsFileHandler vcsFileHandler) {
        this.branchHandler = branchHandler;
        this.vcsFileHandler = vcsFileHandler;
    }

    /**
     * Defines new branchHandler.
     *
     * @param branchName new branchHandler
     * @throws IOException                       if something has gone wrong during
     *                                           the work with file system
     * @throws Vcs.VcsBranchActionForbiddenException when trying to make illegal
     *                                           actions with branchHandler
     * @throws Vcs.VcsIncorrectUsageException          when vcs can't perform command because of incorrect
     *                                           usage
     */
    public void createBranch(String branchName) throws IOException, Vcs.VcsBranchActionForbiddenException, Vcs.VcsIncorrectUsageException {
        if (branchHandler.exists(branchName)) {
            throw new Vcs.VcsBranchActionForbiddenException("Branch with this name is already created");
        }
        vcsFileHandler.assertListEmpty(VcsFileHandler.ListWithFiles.ADD_LIST);
        vcsFileHandler.assertListEmpty(VcsFileHandler.ListWithFiles.RM_LIST);
        branchHandler.setCommitHash(branchName, branchHandler.getHeadLastCommitHash());
    }

    /**
     * Deletes specified branchHandler.
     *
     * @param branchName to delete
     * @throws IOException                       if something has gone wrong during
     *                                           the work with file system
     * @throws Vcs.VcsBranchNotFoundException        when trying to access branchHandler
     *                                           which doesn't exist.
     * @throws Vcs.VcsBranchActionForbiddenException when trying to make illegal
     *                                           actions with branchHandler
     */
    public void deleteBranch(String branchName) throws IOException, Vcs.VcsBranchNotFoundException, Vcs.VcsBranchActionForbiddenException {
        if (branchHandler.getHeadName().equals(branchName)) {
            throw new Vcs.VcsBranchActionForbiddenException("You can't remove current branch");
        }
        branchHandler.assertBranchExists(branchName);
        branchHandler.deleteBranch(branchName);

    }

    /*public void checkIfBranchExist(String branchName) throws IOException, Vcs.VcsBranchNotFoundException {
        if (!FileSystem.exists(Vcs.getBranchesDir() + File.separator + branchName)) {
            throw new Vcs.VcsBranchNotFoundException("Provided branchHandler doesn't exist");
        }
    }

    public String getHeadBranch() throws IOException {
        return FileSystem.getFirstLine(Vcs.getHEAD());
    }

    public String getBranchLastCommitHash(String branchHandler) throws IOException {
        return FileSystem.getFirstLine(Vcs.getBranchesDir() + File.separator + branchHandler);
    }*/
}
