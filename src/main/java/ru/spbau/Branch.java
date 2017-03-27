package ru.spbau;

import ru.spbau.zhidkov.vcs.FileSystem;

import java.io.File;
import java.io.IOException;

/**
 * Class implementing branch command.
 */
public class Branch {
    /**
     * Defines new branch.
     *
     * @param branchName new branch
     * @throws IOException                       if something has gone wrong during
     *                                           the work with file system
     * @throws Vcs.VcsBranchActionForbiddenException when trying to make illegal
     *                                           actions with branch
     * @throws Vcs.VcsIllegalStateException          when vcs can't perform command because of incorrect
     *                                           usage
     */
    public static void createBranch(String branchName) throws IOException, Vcs.VcsBranchActionForbiddenException, Vcs.VcsIllegalStateException {
        if (FileSystem.exists(Vcs.getBranchesDir() + File.separator + branchName)) {
            throw new Vcs.VcsBranchActionForbiddenException("Branch with this name is already created");
        }
        if (!FileSystem.getFirstLine(Vcs.getAddList()).equals("")) {
            throw new Vcs.VcsIllegalStateException("You have several files were added, but haven't committed yet");
        }
        FileSystem.writeStringToFile(Vcs.getBranchesDir() + File.separator + branchName,
                FileSystem.getFirstLine(Vcs.getBranchesDir() + File.separator + Branch.getHeadBranch()));
    }

    /**
     * Deletes specified branch.
     *
     * @param branchName to delete
     * @throws IOException                       if something has gone wrong during
     *                                           the work with file system
     * @throws Vcs.VcsBranchNotFoundException        when trying to access branch
     *                                           which doesn't exist.
     * @throws Vcs.VcsBranchActionForbiddenException when trying to make illegal
     *                                           actions with branch
     */
    public static void deleteBranch(String branchName) throws IOException, Vcs.VcsBranchNotFoundException, Vcs.VcsBranchActionForbiddenException {
        if (Branch.getHeadBranch().equals(branchName)) {
            throw new Vcs.VcsBranchActionForbiddenException("You can't remove current branch");
        }
        checkIfBranchExist(branchName);
        FileSystem.deleteIfExists(Vcs.getBranchesDir() + File.separator + branchName);
    }

    public static void checkIfBranchExist(String branchName) throws IOException, Vcs.VcsBranchNotFoundException {
        if (!FileSystem.exists(Vcs.getBranchesDir() + File.separator + branchName)) {
            throw new Vcs.VcsBranchNotFoundException("Provided branch doesn't exist");
        }
    }

    public static String getHeadBranch() throws IOException {
        return FileSystem.getFirstLine(Vcs.getHEAD());
    }
}
