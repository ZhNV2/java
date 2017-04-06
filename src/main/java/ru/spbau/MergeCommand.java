package ru.spbau;

import ru.spbau.zhidkov.BranchHandler;
import ru.spbau.zhidkov.CommitHandler;
import ru.spbau.zhidkov.ExternalFileHandler;
import ru.spbau.zhidkov.VcsFileHandler;
import ru.spbau.zhidkov.vcs.VcsBlob;
import ru.spbau.zhidkov.vcs.VcsCommit;

import java.io.IOException;
import java.util.*;


/**
 * Class implementing merge command.
 */
public class MergeCommand {
    private static final String MERGE_MESSAGE = "Merged with branch ";;

    private BranchHandler branchHandler;
    private VcsFileHandler vcsFileHandler;
    private ExternalFileHandler externalFileHandler;

    public MergeCommand(BranchHandler branchHandler, VcsFileHandler vcsFileHandler, ExternalFileHandler externalFileHandler) {
        this.branchHandler = branchHandler;
        this.vcsFileHandler = vcsFileHandler;
        this.externalFileHandler = externalFileHandler;
    }

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
    public void merge(String branchToMerge) throws Vcs.VcsBranchActionForbiddenException, Vcs.VcsBranchNotFoundException, IOException, Vcs.VcsConflictException, Vcs.VcsIncorrectUsageException {
        if (branchToMerge.equals(branchHandler.getHeadName())) {
            throw new Vcs.VcsBranchActionForbiddenException("You can't merge branch with itself");
        }
        branchHandler.assertBranchExists(branchToMerge);
        vcsFileHandler.assertListEmpty(VcsFileHandler.ListWithFiles.ADD_LIST);
        vcsFileHandler.assertListEmpty(VcsFileHandler.ListWithFiles.RM_LIST);
        VcsCommit commit = new VcsCommit(MERGE_MESSAGE + branchToMerge, new Date(),
                vcsFileHandler.getAuthorName(), branchHandler.getHeadLastCommitHash(),
                new HashMap<>(), new ArrayList<>());
        Collection<String> checked = new HashSet<>();
        mergeCommit(branchHandler.getBranchCommit(branchToMerge).getHash(), checked, commit);

        vcsFileHandler.writeCommit(commit);
        //FileSystem.writeToFile(Vcs.getObjectsDir(), commit);
        branchHandler.setCommitHash(branchHandler.getHeadName(), commit.getHash());
        //FileSystem.writeStringToFile(Vcs.getBranchesDir() + File.separator + BranchCommand.getHeadBranch(), commit.getHash());
    }

    private void mergeCommit(String commitHash, Collection<String> checked, VcsCommit newCommit) throws IOException, Vcs.VcsConflictException {
        VcsCommit commit = vcsFileHandler.getCommit(commitHash);
        for (Map.Entry<String, String> entry : commit.getChildrenAdd().entrySet()) {
            if (checked.contains(entry.getKey())) continue;
            String fileName = entry.getKey();
            checked.add(fileName);
            VcsBlob blob = vcsFileHandler.getBlob(entry.getValue());
            //VcsBlob blob = (VcsBlob) VcsObject.readFromJson(Vcs.getObjectsDir() + File.separator + entry.getValue(), VcsBlob.class);
            if (externalFileHandler.exists(fileName)) {
                byte[] fileBytes = externalFileHandler.readAllBytes(fileName);
                if (!Arrays.equals(fileBytes, blob.getContent())) {
                    throw new Vcs.VcsConflictException("Can't merge, because file " + fileName + " is different in both branches");
                }
            } else {
                newCommit.addToChildrenAdd(fileName, blob.getHash());
                externalFileHandler.writeBytesToFile(fileName, blob.getContent());
                //FileSystem.writeBytesToFile(fileName, blob.getContent());
            }
        }
        for (String file : commit.getChildrenRm()) {
            checked.add(file);
        }
        if (!commit.getPrevCommitHash().equals(CommitHandler.getInitialCommitPrevHash())) {
            mergeCommit(commit.getPrevCommitHash(), checked, newCommit);
        }
    }






}
