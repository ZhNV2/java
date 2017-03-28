package ru.spbau;

import ru.spbau.zhidkov.vcs.FileSystem;
import ru.spbau.zhidkov.VcsBlob;
import ru.spbau.zhidkov.VcsCommit;
import ru.spbau.zhidkov.VcsObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ru.spbau.Init.hasInitialized;

/**
 * Class implementing commit command.
 */
public class Commit {
    /**
     * Commit all files that were added after last commit.
     *
     * @param message commit message
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void commit(String message) throws IOException, Vcs.VcsIncorrectUsageException {
        if (!hasInitialized()) throw new Vcs.VcsIncorrectUsageException(Vcs.getUninitializedRepoMessage());
        List<String> filesToAdd = FileSystem.readAllLines(Vcs.getAddList());
        List<String> filesToRm = FileSystem.readAllLines(Vcs.getRmList());
        VcsCommit commit = new VcsCommit(message, new Date(), FileSystem.getFirstLine(Vcs.getAuthorName()),
                message.equals(Vcs.getInitialCommitMessage()) ? Vcs.getInitialCommitPrevHash() :
                        Branch.getBranchLastCommitHash(Branch.getHeadBranch()),
                        new HashMap<>(), new ArrayList<>());

        Collection<VcsBlob> blobs = new ArrayList<>();
        for (String file : filesToAdd) {
            VcsBlob blob = new VcsBlob(FileSystem.readAllBytes(file));
            commit.addToChildrenAdd(file, blob.getHash());
            blobs.add(blob);
        }
        for (String file : filesToRm) {
            commit.addToChildrenRm(file);
        }
        for (VcsBlob blob : blobs) {
            FileSystem.writeToFile(Vcs.getObjectsDir(), blob);
        }
        FileSystem.writeToFile(Vcs.getObjectsDir(), commit);
        FileSystem.writeStringToFile(Vcs.getBranchesDir() + File.separator + Branch.getHeadBranch(), commit.getHash());
        FileSystem.writeStringToFile(Vcs.getAddList(), "");
    }

    public static VcsCommit getCommit(String commitHash) throws IOException {
        return (VcsCommit) VcsObject.readFromJson(Vcs.getObjectsDir() + File.separator + commitHash, VcsCommit.class);
    }

    public static boolean isFileInCurrentRevision(String fileName) throws IOException {
        return isFileInCurrentRevision(Branch.getBranchLastCommitHash(Branch.getHeadBranch()), fileName);
    }

    public static List<String> getAllActiveFilesInCurrentRevision() throws IOException {
        List<String> repFiles = new ArrayList<>();
        getAllActiveFilesInCurrentRevision(Branch.getBranchLastCommitHash(Branch.getHeadBranch()),
                new TreeSet<>(), repFiles);
        return repFiles;
    }

    private static boolean isFileInCurrentRevision(String commitHash, String fileName) throws IOException {
        VcsCommit commit = getCommit(commitHash);
        for (Map.Entry<String, String> entry : commit.getChildrenAdd().entrySet()) {
            if (FileSystem.fileNameEquals(entry.getKey(), fileName)) return true;
        }
        return !commit.getPrevCommitHash().equals(Vcs.getInitialCommitPrevHash())
                && isFileInCurrentRevision(commit.getPrevCommitHash(), fileName);
    }

    private static void getAllActiveFilesInCurrentRevision(String commitHash, Collection<String> checked,
                                                           List<String> repFiles) throws IOException {
        VcsCommit commit = getCommit(commitHash);
        for (Map.Entry<String, String> entry : commit.getChildrenAdd().entrySet()) {
            if (checked.contains(entry.getKey())) continue;
            repFiles.add(entry.getKey());
            checked.add(entry.getKey());
        }
        for (String file : commit.getChildrenRm()) {
            checked.add(file);
        }
        if (!commit.getPrevCommitHash().equals(Vcs.getInitialCommitMessage())) {
            getAllActiveFilesInCurrentRevision(commit.getPrevCommitHash(), checked, repFiles);
        }
    }
}
