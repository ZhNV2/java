package ru.spbau;

import com.sun.istack.internal.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.zhidkov.BranchHandler;
import ru.spbau.zhidkov.CommitHandler;
import ru.spbau.zhidkov.ExternalFileHandler;
import ru.spbau.zhidkov.VcsFileHandler;
import ru.spbau.zhidkov.vcs.VcsBlob;
import ru.spbau.zhidkov.vcs.VcsCommit;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;


/**
 * Class implementing merge command.
 */
public class MergeCommand {
    private static final Logger logger = LogManager.getLogger(MergeCommand.class);
    private static final String MERGE_MESSAGE = "Merged with branch ";

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
    public void merge(String branchToMerge) throws IOException, Vcs.VcsIncorrectUsageException, Vcs.VcsBranchNotFoundException, Vcs.VcsBranchActionForbiddenException, Vcs.VcsConflictException {
        logger.traceEntry();
        if (branchToMerge.equals(branchHandler.getHeadName())) {
            logger.error("branch {} can not be merged with itself", branchToMerge);
            throw new Vcs.VcsBranchActionForbiddenException("You can't merge branch with itself");
        }
        branchHandler.assertBranchExists(branchToMerge);
        vcsFileHandler.assertListEmpty(VcsFileHandler.ListWithFiles.ADD_LIST);
        vcsFileHandler.assertListEmpty(VcsFileHandler.ListWithFiles.RM_LIST);
        VcsCommit commit = vcsFileHandler.buildCommit(MERGE_MESSAGE + branchToMerge, new Date(),
                vcsFileHandler.getAuthorName(), branchHandler.getHeadLastCommitHash(),
                new HashMap<>(), new ArrayList<>());
        Collection<Path> checked = new HashSet<>();
        mergeCommit(branchHandler.getBranchCommit(branchToMerge).getHash(), checked, commit);

        vcsFileHandler.writeCommit(commit);
        branchHandler.setCommitHash(branchHandler.getHeadName(), commit.getHash());
        logger.traceExit();
    }

    private void mergeCommit(String commitHash, @NotNull Collection<Path> checked, VcsCommit newCommit) throws IOException, Vcs.VcsConflictException {
        VcsCommit commit = vcsFileHandler.getCommit(commitHash);
        for (Map.Entry<Path, String> entry : commit.getChildrenAdd().entrySet()) {
            if (checked.contains(entry.getKey())) continue;
            Path fileName = entry.getKey();
            checked.add(fileName);
            VcsBlob blob = vcsFileHandler.getBlob(entry.getValue());
            if (externalFileHandler.exists(fileName)) {
                byte[] fileBytes = externalFileHandler.readAllBytes(fileName);
                if (!Arrays.equals(fileBytes, blob.getContent())) {
                    logger.error("conflict was detected with {}", fileName);
                    throw new Vcs.VcsConflictException("Can't merge, because file " + fileName + " is different in both branches");
                }
            } else {
                newCommit.addToChildrenAdd(fileName, blob.getHash());
                externalFileHandler.writeBytesToFile(fileName, blob.getContent());
            }
        }
        for (Path file : commit.getChildrenRm()) {
            checked.add(file);
        }
        if (!commit.getPrevCommitHash().equals(CommitHandler.getInitialCommitPrevHash())) {
            mergeCommit(commit.getPrevCommitHash(), checked, newCommit);
        }
    }






}
