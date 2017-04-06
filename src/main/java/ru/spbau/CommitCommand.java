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
 * Class implementing commitHandler command.
 */
public class CommitCommand {

    private VcsFileHandler vcsFileHandler;
    private BranchHandler branchHandler;
    private ExternalFileHandler externalFileHandler;

    public CommitCommand(VcsFileHandler vcsFileHandler, BranchHandler branchHandler, ExternalFileHandler externalFileHandler) {
        this.vcsFileHandler = vcsFileHandler;
        this.branchHandler = branchHandler;
        this.externalFileHandler = externalFileHandler;
    }

    /**
     * CommitCommand all files that were added after last commitHandler.
     *
     * @param message commitHandler message
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void commit(String message) throws IOException, Vcs.VcsIncorrectUsageException {
        List<String> filesToAdd = vcsFileHandler.getList(VcsFileHandler.ListWithFiles.ADD_LIST);
        List<String> filesToRm = vcsFileHandler.getList(VcsFileHandler.ListWithFiles.RM_LIST);
        VcsCommit commit = new VcsCommit(message, new Date(), vcsFileHandler.getAuthorName(),
                message.equals(CommitHandler.getInitialCommitMessage()) ? CommitHandler.getInitialCommitPrevHash() :
                        branchHandler.getHeadLastCommitHash(),
                        new HashMap<>(), new ArrayList<>());

        Collection<VcsBlob> blobs = new ArrayList<>();
        for (String file : filesToAdd) {
            VcsBlob blob = new VcsBlob(externalFileHandler.readAllBytes(file));
            commit.addToChildrenAdd(file, blob.getHash());
            blobs.add(blob);
        }
        for (String file : filesToRm) {
            commit.addToChildrenRm(file);
        }
        for (VcsBlob blob : blobs) {
            vcsFileHandler.writeBlob(blob);
            //FileSystem.writeToFile(Vcs.getObjectsDir(), blob);
        }
        vcsFileHandler.writeCommit(commit);
        //FileSystem.writeToFile(Vcs.getObjectsDir(), commitHandler);
        branchHandler.setCommitHash(branchHandler.getHeadName(), commit.getHash());
        vcsFileHandler.clearList(VcsFileHandler.ListWithFiles.ADD_LIST);
        vcsFileHandler.clearList(VcsFileHandler.ListWithFiles.RM_LIST);
        //FileSystem.writeStringToFile(Vcs.getBranchesDir() + File.separator + BranchCommand.getHeadBranch(), commitHandler.getHash());
        //FileSystem.writeStringToFile(Vcs.getAddList(), "");
    }

//    public VcsCommit getCommit(String commitHash) throws IOException {
//        return (VcsCommit) VcsObject.readFromJson(Vcs.getObjectsDir() + File.separator + commitHash, VcsCommit.class);
//    }

//    public boolean isFileInCurrentRevision(String fileName) throws IOException {
//        return isFileInCurrentRevision(BranchCommand.getBranchLastCommitHash(BranchCommand.getHeadBranch()), fileName);
//    }

//    public List<String> getAllActiveFilesInCurrentRevision() throws IOException {
//        List<String> repFiles = new ArrayList<>();
//        getAllActiveFilesInCurrentRevision(BranchCommand.getBranchLastCommitHash(BranchCommand.getHeadBranch()),
//                new TreeSet<>(), repFiles);
//        return repFiles;
//    }

//    private boolean isFileInCurrentRevision(String commitHash, String fileName) throws IOException {
//        VcsCommit commitHandler = getCommit(commitHash);
//        for (Map.Entry<String, String> entry : commitHandler.getChildrenAdd().entrySet()) {
//            if (FileSystem.fileNameEquals(entry.getKey(), fileName)) return true;
//        }
//        return !commitHandler.getPrevCommitHash().equals(Vcs.getInitialCommitPrevHash())
//                && isFileInCurrentRevision(commitHandler.getPrevCommitHash(), fileName);
//    }

//    private void getAllActiveFilesInCurrentRevision(String commitHash, Collection<String> checked,
//                                                           List<String> repFiles) throws IOException {
//        VcsCommit commitHandler = getCommit(commitHash);
//        for (Map.Entry<String, String> entry : commitHandler.getChildrenAdd().entrySet()) {
//            if (checked.contains(entry.getKey())) continue;
//            repFiles.add(entry.getKey());
//            checked.add(entry.getKey());
//        }
//        for (String file : commitHandler.getChildrenRm()) {
//            checked.add(file);
//        }
//        if (!commitHandler.getPrevCommitHash().equals(Vcs.getInitialCommitMessage())) {
//            getAllActiveFilesInCurrentRevision(commitHandler.getPrevCommitHash(), checked, repFiles);
//        }
//    }
}
