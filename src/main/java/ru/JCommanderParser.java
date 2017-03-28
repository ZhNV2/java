package ru;

import com.beust.jcommander.*;
import ru.spbau.Vcs;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class JCommanderParser {

    /**
     * If the --help option was chosen.
     */
    @Parameter(names = "--help", help = true)
    public boolean help;

    /**
     * Class for parse init command.
     */
    @Parameters(commandDescription = "Initialize empty repository in the current folder")
    public static class CommandInit implements Command {
        @Parameter(names = "--author", required = true, description = "Author's name")
        private String author;

        /**
         * Runs necessary action after parsing args.
         *
         * @throws IOException if something has gone wrong during
         *                     the work with file system
         */
        @Override
        public void run() throws IOException, Vcs.VcsIncorrectUsageException {
            Vcs.init(author);
        }
    }

    /**
     * Class for parse add command.
     */
    @Parameters(commandDescription = "Add file contents to the index")
    public static class CommandAdd implements Command {

        @Parameter(description = "Files to add to the index")
        private List<String> files;

        /**
         * Runs necessary action after parsing args.
         *
         * @throws IOException if something has gone wrong during
         *                     the work with file system
         */
        @Override
        public void run() throws IOException, Vcs.VcsIncorrectUsageException {
            if (files == null || files.size() == 0) throw new ParameterException("Specify files to add");
            Vcs.add(files);
        }

    }

    /**
     * Class for parse commit command.
     */
    @Parameters(commandDescription = "Record changes to the repository")
    public static class CommandCommit implements Command {

        @Parameter(names = {"-m", "--message"}, description = "Commit message", required = true)
        private String message;

        /**
         * Runs necessary action after parsing args.
         *
         * @throws IOException if something has gone wrong during
         *                     the work with file system
         */
        @Override
        public void run() throws IOException, Vcs.VcsIncorrectUsageException {
            if (message.equals(Vcs.getInitialCommitMessage())) {
                throw new IllegalArgumentException("This commit message is reserved for first commit. Please, use another one");
            }
            Vcs.commit(message);
        }
    }

    /**
     * Class for parse log command.
     */
    @Parameters(commandDescription = "Print log containing information about commits")
    public static class CommandLog implements Command {
        /**
         * Runs necessary action after parsing args.
         *
         * @throws IOException if something has gone wrong during
         *                     the work with file system
         */
        @Override
        public void run() throws IOException, Vcs.VcsIncorrectUsageException {
            System.out.println(Vcs.log().toString());
        }
    }

    /**
     * Class for parse checkout command.
     */
    @Parameters(commandDescription = "Alert your repository to required revision or branch")
    public static class CommandCheckout implements Command {
        @Parameter(names = {"-r", "--revision"}, description = "Revision to checkout")
        private String revision;

        @Parameter(names = {"-b", "--branch"}, description = "Branch to checkout")
        private String branch;

        /**
         * Runs necessary action after parsing args.
         *
         * @throws IOException                      if something has gone wrong during
         *                                          the work with file system
         * @throws Vcs.VcsIncorrectUsageException     when vcs can't perform command because of incorrect
         *                                          usage
         * @throws Vcs.VcsRevisionNotFoundException when trying to access revision
         *                                          which doesn't exist
         * @throws Vcs.VcsBranchNotFoundException   when trying to access
         *                                          branch which doesn't exist.
         */
        @Override
        public void run() throws IOException, Vcs.VcsIncorrectUsageException, Vcs.VcsRevisionNotFoundException, Vcs.VcsBranchNotFoundException {
            if (revision == null && branch == null) {
                throw new ParameterException("You should provide revision or branch to commit");
            } else if (revision != null && branch != null) {
                throw new ParameterException("You should provide only branch or revision not both together");
            } else if (revision != null) {
                Vcs.checkoutRevision(revision);
            } else {
                Vcs.checkoutBranch(branch);
            }
        }
    }

    /**
     * Class for parse branch command.
     */
    @Parameters(commandDescription = "Create or delete your branch")
    public static class CommandBranch implements Command {
        @Parameter(names = {"-n", "--new"}, description = "Name of new branch")
        private String branchToCreate;

        @Parameter(names = {"-d", "--delete"}, description = "Branch to delete")
        private String branchToDelete;

        /**
         * Runs necessary action after parsing args.
         *
         * @throws IOException                           if something has gone wrong during
         *                                               the work with file system
         * @throws Vcs.VcsBranchActionForbiddenException when trying to make illegal actions
         *                                               with branch
         * @throws Vcs.VcsBranchNotFoundException        when trying to
         *                                               access branch which doesn't exist.
         * @throws Vcs.VcsIncorrectUsageException          when vcs can't perform command because of incorrect
         *                                               usage
         */
        @Override
        public void run() throws IOException, Vcs.VcsBranchActionForbiddenException, Vcs.VcsBranchNotFoundException, Vcs.VcsIncorrectUsageException {
            if (branchToCreate == null && branchToDelete == null) {
                throw new ParameterException("You should specify do you want to create new branch or delete old one");
            } else if (branchToCreate != null && branchToDelete != null) {
                throw new ParameterException("You can only delete branch or create new one not both actions together");
            } else if (branchToCreate != null) {
                Vcs.createBranch(branchToCreate);
            } else {
                Vcs.deleteBranch(branchToDelete);
            }
        }
    }

    /**
     * Class for parse merge command.
     */
    @Parameters(commandDescription = "Merge current branch with specified one")
    public static class CommandMerge implements Command {

        @Parameter(names = {"-b", "--branch"}, description = "Branch to merge", required = true)
        private String branchToMerge;

        /**
         * Runs necessary action after parsing args.
         *
         * @throws IOException                           if something has gone wrong during
         *                                               the work with file system
         * @throws Vcs.VcsBranchActionForbiddenException when trying to make illegal actions
         *                                               with branch
         * @throws Vcs.VcsConflictException              when conflict during merge was detected
         * @throws Vcs.VcsBranchNotFoundException        when trying to access
         *                                               branch which doesn't exist.
         * @throws Vcs.VcsIncorrectUsageException          when vcs can't perform command because of incorrect
         *                                               usage
         */
        @Override
        public void run() throws IOException, Vcs.VcsBranchActionForbiddenException, Vcs.VcsConflictException, Vcs.VcsBranchNotFoundException, Vcs.VcsIncorrectUsageException {
            Vcs.merge(branchToMerge);
        }
    }

    /**
     * Class for parse reset command.
     */
    @Parameters(commandDescription = "Reset specified file with the last version in repository")
    public static class CommandReset implements Command {

        @Parameter(names = {"-f", "--file"}, description = "File to reset", required = true)
        private String fileName;

        /**
         * Runs necessary action after parsing args.
         *
         * @throws IOException                  if something has gone wrong during
         *                                      the work with file system
         * @throws Vcs.VcsIncorrectUsageException when vcs can't perform command because of incorrect
         *                                      usage
         */
        @Override
        public void run() throws IOException, Vcs.VcsIncorrectUsageException {
            Vcs.reset(fileName);
        }
    }

    /**
     * Class for parse rm command.
     */
    @Parameters(commandDescription = "Remove file from repository")
    public static class CommandRemove implements Command {

        @Parameter(names = {"-f", "--file"}, description = "File to remove", required = true)
        private String fileToRemove;


        @Override
        public void run() throws IOException, Vcs.VcsIncorrectUsageException {
            Vcs.remove(fileToRemove);
        }
    }

    /**
     * Class for parse clean command.
     */
    @Parameters(commandDescription = "Remove all files not from repository")
    public static class CommandClean implements Command {

        @Override
        public void run() throws IOException, Vcs.VcsIncorrectUsageException {
            Vcs.clean();
        }
    }

    /**
     * Class for parse status command.
     */
    @Parameters(commandDescription = "Status of current repository state")
    public static class CommandStatus implements Command {

        @Override
        public void run() throws IOException {
            System.out.println(Vcs.status().toString());
        }
    }
}