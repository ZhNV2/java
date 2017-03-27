package ru.spbau;

import ru.spbau.zhidkov.FileSystem;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ru.spbau.Branch.checkIfBranchExist;
import static ru.spbau.Commit.getCommit;

/**
 * Created by Нико on 27.03.2017.
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
     * @throws Vcs.VcsIllegalStateException          when vcs can't perform command because of incorrect
     *                                           usage
     */
    public static void merge(String branchToMerge) throws IOException, Vcs.VcsBranchNotFoundException, Vcs.VcsConflictException, Vcs.VcsBranchActionForbiddenException, Vcs.VcsIllegalStateException {
        if (branchToMerge.equals(FileSystem.getFirstLine(Vcs.getHEAD()))) {
            throw new Vcs.VcsBranchActionForbiddenException("You can't merge branch with itself");
        }
        checkIfBranchExist(branchToMerge);
        if (!FileSystem.getFirstLine(Vcs.getAddList()).equals("")) {
            throw new Vcs.VcsIllegalStateException("You have several files were added, but haven't committed yet");
        }
        VcsCommit commit = new VcsCommit(Vcs.getMergeMessage() + branchToMerge, new Date(), FileSystem.getFirstLine(Vcs.getAuthorName()),
                FileSystem.getFirstLine(Vcs.getBranchesDir() + File.separator + FileSystem.getFirstLine(Vcs.getHEAD())), new HashMap<>());
        Collection<String> checked = new HashSet<>();
        mergeCommit(FileSystem.getFirstLine(Vcs.getBranchesDir() + File.separator + branchToMerge), checked, commit);

        FileSystem.writeToFile(commit, Vcs.getObjectsDir());
        FileSystem.writeStringToFile(Vcs.getBranchesDir() + File.separator + FileSystem.getFirstLine(Vcs.getHEAD()), commit.getHash());
    }

    private static void mergeCommit(String commitHash, Collection<String> checked, VcsCommit newCommit) throws IOException, Vcs.VcsConflictException {
        VcsCommit commit = getCommit(commitHash);
        for (Map.Entry<String, String> entry : commit.getChildren().entrySet()) {
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
                newCommit.addToChildren(fileName, blob.getHash());
                FileSystem.writeBytesToFile(fileName, blob.getContent());
            }
        }
        if (!commit.getPrevCommitHash().equals(Vcs.getInitialCommitPrevHash())) {
            mergeCommit(commit.getPrevCommitHash(), checked, newCommit);
        }
    }






}
