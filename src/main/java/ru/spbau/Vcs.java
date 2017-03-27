package ru.spbau;

import ru.spbau.zhidkov.FileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static ru.spbau.Init.hasInitialized;


/**
 * Class providing vcs functionality.
 */
@SuppressWarnings("WeakerAccess")
abstract public class Vcs {

    /**
     * Sets the folder with which it will work to {@param currentFolder}
     *
     * @param currentFolder folder containing (or that will contain)
     *                      repository
     */
    public static void setCurrentFolder(String currentFolder) {
        CURRENT_FOLDER = currentFolder;
        ROOT_DIR = CURRENT_FOLDER + File.separator + ".vcs";
        OBJECTS_DIR = ROOT_DIR + File.separator + "objects";
        BRANCHES_DIR = ROOT_DIR + File.separator + "branches";
        ADD_LIST = ROOT_DIR + File.separator + "addList";
        INITIAL_COMMIT_MESSAGE = "Initial commit.";
        ONE_LINE_VARS_DIR = ROOT_DIR + File.separator + "one_lines_vars";
        HEAD = ONE_LINE_VARS_DIR + File.separator + "HEAD";
        MASTER = "master";
        AUTHOR_NAME = ONE_LINE_VARS_DIR + File.separator + "AUTHOR_NAME";
        INITIAL_COMMIT_PREV_HASH = "";
        MERGE_MESSAGE = "Merged with branch ";
        WORKING_COPY = CURRENT_FOLDER + File.separator + ".wc";
    }

    private static String CURRENT_FOLDER;
    private static String ROOT_DIR;
    private static String OBJECTS_DIR;
    private static String BRANCHES_DIR;
    private static String ADD_LIST;
    private static String INITIAL_COMMIT_MESSAGE;
    private static String ONE_LINE_VARS_DIR;
    private static String HEAD;
    private static String MASTER;
    private static String AUTHOR_NAME;
    private static String INITIAL_COMMIT_PREV_HASH;
    private static String MERGE_MESSAGE;
    private static String WORKING_COPY;


    /**
     * Saves all files that are not relating to vcs in temporary storage.
     *
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void saveWorkingCopy() throws IOException {
        WorkingCopy.saveWorkingCopy();
    }

    /**
     * Returns folder files to their original state.
     *
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void restoreWorkingCopy() throws IOException {
        WorkingCopy.restoreWorkingCopy();
    }

    /**
     * Deletes temporary storage.
     *
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void clearWorkingCopy() throws IOException {
        WorkingCopy.clearWorkingCopy();
    }

    /**
     * Adds files to repo (adds them to temporary list of
     * files to add).
     *
     * @param fileNames list of files to add.
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void add(List<String> fileNames) throws IOException, Vcs.VcsIllegalStateException {
        Add.add(fileNames);
    }

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
        Branch.createBranch(branchName);
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
        Branch.deleteBranch(branchName);
    }

    /**
     * Switches current branch to another.
     *
     * @param branchName branch to switch to
     * @throws IOException                if something has gone wrong during
     *                                    the work with file system
     * @throws Vcs.VcsBranchNotFoundException when trying to access branch
     *                                    which doesn't exist.
     * @throws Vcs.VcsIllegalStateException   when vcs can't perform command because of incorrect
     *                                    usage
     */
    public static void checkoutBranch(String branchName) throws IOException, Vcs.VcsBranchNotFoundException, Vcs.VcsIllegalStateException {
       Checkout.checkoutBranch(branchName);
    }

    /**
     * Switches current revision to provided.
     *
     * @param commitHash hash of revision to switch to
     * @throws IOException                  if something has gone wrong during
     *                                      the work with file system
     * @throws Vcs.VcsRevisionNotFoundException when trying to access revision
     *                                      which doesn't exist
     * @throws Vcs.VcsIllegalStateException     when vcs can't perform command because of incorrect
     *                                      usage
     */
    public static void checkoutRevision(String commitHash) throws IOException, Vcs.VcsRevisionNotFoundException, Vcs.VcsIllegalStateException {
        Checkout.checkoutRevision(commitHash);
    }

    /**
     * Commit all files that were added after last commit.
     *
     * @param message commit message
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void commit(String message) throws IOException {
        Commit.commit(message);
    }

    /**
     * Initializes repo in the current folder.
     *
     * @param authorName author name
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static void init(String authorName) throws IOException, Vcs.VcsIllegalStateException {
        Init.init(authorName);
    }

    /**
     * Prints log about all commits from current to initial in
     * the current branch.
     *
     * @return information about all commits in current branch
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public static StringBuilder log() throws IOException {
        return Log.log();
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
     * @throws Vcs.VcsIllegalStateException          when vcs can't perform command because of incorrect
     *                                           usage
     */
    public static void merge(String branchToMerge) throws IOException, Vcs.VcsBranchNotFoundException, Vcs.VcsConflictException, Vcs.VcsBranchActionForbiddenException, Vcs.VcsIllegalStateException {
        Merge.merge(branchToMerge);
    }

    /**
     * Abstract class for all throwable by vcs exceptions.
     */
    public static abstract class VcsException extends Exception {
        public VcsException(String s) {
            super(s);
        }
    }

    /**
     * Is thrown when trying to access branch which doesn't exist.
     */
    public static class VcsBranchNotFoundException extends VcsException {
        public VcsBranchNotFoundException(String s) {
            super(s);
        }
    }

    /**
     * Is thrown when trying to make illegal actions with branch.
     */
    public static class VcsBranchActionForbiddenException extends VcsException {
        public VcsBranchActionForbiddenException(String s) {
            super(s);
        }
    }

    /**
     * Is thrown when trying to access revision which doesn't exist.
     */
    public static class VcsRevisionNotFoundException extends VcsException {
        public VcsRevisionNotFoundException(String s) {
            super(s);
        }
    }

    /**
     * Is thrown when conflict during merge was detected.
     */
    public static class VcsConflictException extends VcsException {
        public VcsConflictException(String s) {
            super(s);
        }
    }

    /**
     * Is thrown when vcs can't perform command because of incorrect
     * usage.
     */
    public static class VcsIllegalStateException extends VcsException {
        public VcsIllegalStateException(String s) {
            super(s);
        }
    }

    public static String getBranchesDir() {
        return BRANCHES_DIR;
    }

    public static String getAddList() {
        return ADD_LIST;
    }

    public static String getInitialCommitMessage() {
        return INITIAL_COMMIT_MESSAGE;
    }

    public static String getHEAD() {
        return HEAD;
    }

    public static String getAuthorName() {
        return AUTHOR_NAME;
    }

    public static String getInitialCommitPrevHash() {
        return INITIAL_COMMIT_PREV_HASH;
    }

    public static String getCurrentFolder() {
        return CURRENT_FOLDER;
    }

    public static String getRootDir() {
        return ROOT_DIR;
    }

    public static String getObjectsDir() {
        return OBJECTS_DIR;
    }

    public static String getOneLineVarsDir() {
        return ONE_LINE_VARS_DIR;
    }

    public static String getMASTER() {
        return MASTER;
    }

    public static String getMergeMessage() {
        return MERGE_MESSAGE;
    }

    public static String getWorkingCopy() {
        return WORKING_COPY;
    }
}
