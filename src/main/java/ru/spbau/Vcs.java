package ru.spbau;

import ru.Command;
import ru.spbau.zhidkov.*;
import ru.spbau.zhidkov.vcs.FileSystem;

import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * Class providing vcs functionality.
 */
@SuppressWarnings("WeakerAccess")
public class Vcs {

    private AddCommand addCommand;
    private BranchCommand branchCommand;
    private CheckoutCommand checkoutCommand;
    private CleanCommand cleanCommand;
    private CommitCommand commitCommand;
    private InitChecker initChecker;
    private InitCommand initCommand;
    private LogCommand logCommand;
    private MergeCommand mergeCommand;
    private RemoveCommand removeCommand;
    private ResetCommand resetCommand;
    private StatusCommand statusCommand;
    private WorkingCopyCommand workingCopyCommand;

    public Vcs(String dir) throws IOException {

        /*
      Sets the folder with which it will work to {@param currentFolder}

      */
        FileSystem fileSystem = new FileSystem(dir);

        VcsFileHandler vcsFileHandler = new VcsFileHandler(fileSystem);
        WorkingCopyHandler workingCopyHandler = new WorkingCopyHandler(fileSystem);
        ExternalFileHandler externalFileHandler = new ExternalFileHandler(fileSystem, workingCopyHandler, vcsFileHandler);
        CommitHandler commitHandler = new CommitHandler(vcsFileHandler);
        BranchHandler branchHandler = new BranchHandler(vcsFileHandler);

        addCommand = new AddCommand(externalFileHandler, vcsFileHandler);
        branchCommand = new BranchCommand(branchHandler, vcsFileHandler);
        checkoutCommand = new CheckoutCommand(branchHandler, commitHandler, vcsFileHandler, externalFileHandler);
        cleanCommand = new CleanCommand(vcsFileHandler, branchHandler, externalFileHandler, commitHandler);
        commitCommand = new CommitCommand(vcsFileHandler, branchHandler, externalFileHandler);
        initChecker = new InitChecker(vcsFileHandler);
        initCommand = new InitCommand(vcsFileHandler, commitCommand);
        logCommand = new LogCommand(branchHandler, vcsFileHandler);
        mergeCommand = new MergeCommand(branchHandler, vcsFileHandler, externalFileHandler);
        removeCommand = new RemoveCommand(externalFileHandler, vcsFileHandler, commitHandler, branchHandler);
        resetCommand = new ResetCommand(vcsFileHandler, externalFileHandler, branchHandler);
        statusCommand = new StatusCommand(vcsFileHandler, externalFileHandler, branchHandler);
        workingCopyCommand = new WorkingCopyCommand(workingCopyHandler, externalFileHandler);
    }


    private final static String ALREADY_INITIALIZED_REPO = "There is a repository in the current folder already.";

    private final static String UNINITIALIZED_REPO_MESSAGE = "There is no repository found in the current folder."
            + System.lineSeparator() + "Use init command to initialize repository";


    /**
     * Saves all files that are not relating to vcs in temporary storage.
     *
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void saveWorkingCopy() throws IOException {
        workingCopyCommand.saveWorkingCopy();
    }

    /**
     * Returns folder files to their original state.
     *
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void restoreWorkingCopy() throws IOException {
        workingCopyCommand.restoreWorkingCopy();
    }

    /**
     * Deletes temporary storage.
     *
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void clearWorkingCopy() throws IOException {
        workingCopyCommand.clearWorkingCopy();
    }

    /**
     * Adds files to repo (adds them to temporary list of
     * files to add).
     *
     * @param fileNames list of files to add.
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void add(List<String> fileNames) throws IOException, VcsIncorrectUsageException {
        assertInitialized();
        addCommand.add(fileNames);
    }

    /**
     * Defines new branch.
     *
     * @param branchName new branch
     * @throws IOException                           if something has gone wrong during
     *                                               the work with file system
     * @throws Vcs.VcsBranchActionForbiddenException when trying to make illegal
     *                                               actions with branch
     * @throws VcsIncorrectUsageException            when vcs can't perform command because of incorrect
     *                                               usage
     */
    public void createBranch(String branchName) throws IOException, Vcs.VcsBranchActionForbiddenException, VcsIncorrectUsageException {
        assertInitialized();
        branchCommand.createBranch(branchName);
    }

    /**
     * Deletes specified branch.
     *
     * @param branchName to delete
     * @throws IOException                           if something has gone wrong during
     *                                               the work with file system
     * @throws Vcs.VcsBranchNotFoundException        when trying to access branch
     *                                               which doesn't exist.
     * @throws Vcs.VcsBranchActionForbiddenException when trying to make illegal
     *                                               actions with branch
     */
    public void deleteBranch(String branchName) throws IOException, Vcs.VcsBranchNotFoundException, Vcs.VcsBranchActionForbiddenException, VcsIncorrectUsageException {
        assertInitialized();
        branchCommand.deleteBranch(branchName);
    }

    /**
     * Switches current branch to another.
     *
     * @param branchName branch to switch to
     * @throws IOException                    if something has gone wrong during
     *                                        the work with file system
     * @throws Vcs.VcsBranchNotFoundException when trying to access branch
     *                                        which doesn't exist.
     * @throws VcsIncorrectUsageException     when vcs can't perform command because of incorrect
     *                                        usage
     */
    public void checkoutBranch(String branchName) throws IOException, Vcs.VcsBranchNotFoundException, VcsIncorrectUsageException {
        assertInitialized();
        checkoutCommand.checkoutBranch(branchName);
    }

    /**
     * Switches current revision to provided.
     *
     * @param commitHash hash of revision to switch to
     * @throws IOException                      if something has gone wrong during
     *                                          the work with file system
     * @throws Vcs.VcsRevisionNotFoundException when trying to access revision
     *                                          which doesn't exist
     * @throws VcsIncorrectUsageException       when vcs can't perform command because of incorrect
     *                                          usage
     */
    public void checkoutRevision(String commitHash) throws IOException, Vcs.VcsRevisionNotFoundException, VcsIncorrectUsageException, VcsBranchNotFoundException {
        assertInitialized();
        checkoutCommand.checkoutRevision(commitHash);
    }

    /**
     * CommitCommand all files that were added after last commit.
     *
     * @param message commit message
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void commit(String message) throws IOException, VcsIncorrectUsageException {
        assertInitialized();
        commitCommand.commit(message);
    }

    /**
     * Initializes repo in the current folder.
     *
     * @param authorName author name
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void init(String authorName) throws IOException, VcsIncorrectUsageException {
        if (initChecker.hasInitialized()) {
            throw new VcsIncorrectUsageException(ALREADY_INITIALIZED_REPO);
        }
        initCommand.init(authorName);
    }

    /**
     * Prints log about all commits from current to initial in
     * the current branch.
     *
     * @return information about all commits in current branch
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public StringBuilder log() throws IOException, VcsIncorrectUsageException {
        assertInitialized();
        return logCommand.log();
    }

    /**
     * Merges current branch with other. Revision may only contain
     * unique files or content equal files.
     *
     * @param branchToMerge branch to merge with
     * @throws IOException                           if something has gone wrong during
     *                                               the work with file system
     * @throws Vcs.VcsBranchNotFoundException        when trying to access branch
     *                                               which doesn't exist.
     * @throws Vcs.VcsConflictException              when conflict during merge was detected
     * @throws Vcs.VcsBranchActionForbiddenException when trying to make illegal
     *                                               actions with branch
     * @throws VcsIncorrectUsageException            when vcs can't perform command because of incorrect
     *                                               usage
     */
    public void merge(String branchToMerge) throws IOException, Vcs.VcsBranchNotFoundException, Vcs.VcsConflictException, Vcs.VcsBranchActionForbiddenException, VcsIncorrectUsageException {
        assertInitialized();
        mergeCommand.merge(branchToMerge);
    }


    public void reset(String fileName) throws IOException, VcsIncorrectUsageException {
        assertInitialized();
        resetCommand.reset(fileName);
    }

    public void clean() throws IOException, VcsIncorrectUsageException {
        assertInitialized();
        cleanCommand.clean();
    }

    public void remove(List<String> files) throws IOException, VcsIncorrectUsageException {
        assertInitialized();
        removeCommand.remove(files);
    }

    public StringBuilder status() throws IOException, VcsIncorrectUsageException {
        assertInitialized();
        StatusCommand.StatusHolder statusHolder = statusCommand.status();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Modified files:").append(System.lineSeparator());
        stringBuilder.append(listToPrint(statusHolder.modifiedFiles));
        stringBuilder.append("Added files:").append(System.lineSeparator());
        stringBuilder.append(listToPrint(statusHolder.addedFiles));
        stringBuilder.append("Removed files:").append(System.lineSeparator());
        stringBuilder.append(listToPrint(statusHolder.removedFiles));
        stringBuilder.append("Foreign files:").append(System.lineSeparator());
        stringBuilder.append(listToPrint(statusHolder.foreignFiles));
        return stringBuilder;
    }

    private void assertInitialized() throws IOException, VcsIncorrectUsageException {
        if (!initChecker.hasInitialized())
            throw new VcsIncorrectUsageException(UNINITIALIZED_REPO_MESSAGE);
    }

    private StringBuilder listToPrint(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        list.forEach(s->{stringBuilder.append(s).append(System.lineSeparator());});
        return stringBuilder;
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
    public static class VcsIncorrectUsageException extends VcsException {
        public VcsIncorrectUsageException(String s) {
            super(s);
        }
    }


}
