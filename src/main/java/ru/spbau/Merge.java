package ru.spbau;

import ru.spbau.zhidkov.vcs.FileSystem;
import ru.spbau.zhidkov.VcsBlob;
import ru.spbau.zhidkov.VcsCommit;
import ru.spbau.zhidkov.VcsObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ru.spbau.Branch.checkIfBranchExist;
import static ru.spbau.Commit.getCommit;
import static ru.spbau.Init.hasInitialized;

/**
 * Class implementing merge command.
 */
public class Merge {
    /**
     * Merges current branch with other. Revision may only contain
     * unique files or content equal files.
     *
     * @param branchToMerge branch to merge with
     * @throws IOException                       if something has gone wrong during
     *                                           the work with file system
     * @throws Vcs.VcsBranchNotFoundException        when trying to access branch
     *                                           which doesn't exist.
     * @throws Vcs.VcsConflictException              when conflict during merge was detected
     * @throws Vcs.VcsBranchActionForbiddenException when trying to make illegal
     *                                           actions with branch
     * @throws Vcs.VcsIncorrectUsageException          when vcs can't perform command because of incorrect
     *                                           usage
     */
    public static void merge(String branchToMerge) throws IOException, Vcs.VcsBranchNotFoundException, Vcs.VcsConflictException, Vcs.VcsBranchActionForbiddenException, Vcs.VcsIncorrectUsageException {
        if (!hasInitialized()) throw new Vcs.VcsIncorrectUsageException(Vcs.getUninitializedRepoMessage());
        if (branchToMerge.equals(Branch.getHeadBranch())) {
            throw new Vcs.VcsBranchActionForbiddenException("You can't merge branch with itself");
        }
        checkIfBranchExist(branchToMerge);
        if (!FileSystem.getFirstLine(Vcs.getAddList()).equals("")) {
            throw new Vcs.VcsIncorrectUsageException("You have several files were added, but haven't committed yet");
        }
        VcsCommit commit = new VcsCommit(Vcs.getMergeMessage() + branchToMerge, new Date(), FileSystem.getFirstLine(Vcs.getAuthorName()),
                Branch.getBranchLastCommitHash(Branch.getHeadBranch()), new HashMap<>(), new ArrayList<>());
        Collection<String> checked = new HashSet<>();
        mergeCommit(Branch.getBranchLastCommitHash(branchToMerge), checked, commit);

        FileSystem.writeToFile(Vcs.getObjectsDir(), commit);
        FileSystem.writeStringToFile(Vcs.getBranchesDir() + File.separator + Branch.getHeadBranch(), commit.getHash());
    }

    private static void mergeCommit(String commitHash, Collection<String> checked, VcsCommit newCommit) throws IOException, Vcs.VcsConflictException {
        VcsCommit commit = getCommit(commitHash);
        for (Map.Entry<String, String> entry : commit.getChildrenAdd().entrySet()) {
            if (checked.contains(entry.getKey())) continue;
            String fileName = entry.getKey();
            checked.add(fileName);
            VcsBlob blob = (VcsBlob) VcsObject.readFromJson(Vcs.getObjectsDir() + File.separator + entry.getValue(), VcsBlob.class);
            if (FileSystem.exists(fileName)) {
                byte[] fileBytes = FileSystem.readAllBytes(fileName);
                if (!Arrays.equals(fileBytes, blob.getContent())) {
                    throw new Vcs.VcsConflictException("Can't merge, because file " + fileName + " is different in both branches");
                }
            } else {
                newCommit.addToChildrenAdd(fileName, blob.getHash());
                FileSystem.writeBytesToFile(fileName, blob.getContent());
            }
        }
        for (String file : commit.getChildrenRm()) {
            checked.add(file);
        }
        if (!commit.getPrevCommitHash().equals(Vcs.getInitialCommitPrevHash())) {
            mergeCommit(commit.getPrevCommitHash(), checked, newCommit);
        }
    }






}
