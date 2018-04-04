package ru.spbau.zhidkov.console;

import com.beust.jcommander.*;
import com.beust.jcommander.internal.Nullable;
import ru.spbau.zhidkov.vcs.commands.Vcs;
import ru.spbau.zhidkov.vcs.handlers.CommitHandler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/** Class for parsing args */
@SuppressWarnings("WeakerAccess")
public class JCommanderParser {

    private Vcs vcs;

    /** If the --help option was chosen */
    @Parameter(names = "--help", help = true)
    public boolean help;

    public JCommanderParser(Vcs vcs) {
        this.vcs = vcs;
    }


    /** Class for parsing init command */
    @Parameters(commandDescription = "Initialize empty repository in the current folder")
    public class ParserCommandInit implements ParserCommand {
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
            vcs.init(author);
        }
    }

    /** Class for parsing add command */
    @Parameters(commandDescription = "Add file contents to the index")
    public class ParserCommandAdd implements ParserCommand {

        @Parameter(description = "Files to add to the index")
        private
        @Nullable
        List<Path> files;

        /**
         * Runs necessary action after parsing args.
         *
         * @throws IOException if something has gone wrong during
         *                     the work with file system
         */
        @Override
        public void run() throws IOException, Vcs.VcsIncorrectUsageException {
            if (files == null || files.size() == 0) throw new ParameterException("Specify files to add");
            vcs.add(files);
        }

    }

    /** Class for parsing commit command */
    @Parameters(commandDescription = "Record changes to the repository")
    public class ParserCommandCommit implements ParserCommand {

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
            if (message.equals(CommitHandler.getInitialCommitMessage())) {
                throw new IllegalArgumentException("This commit message is reserved for first commit. " +
                        "Please, use another one");
            }
            vcs.commit(message);
        }
    }

    /** Class for parsing log command */
    @Parameters(commandDescription = "Print log containing information about commits")
    public class ParserCommandLog implements ParserCommand {
        /**
         * Runs necessary action after parsing args.
         *
         * @throws IOException if something has gone wrong during
         *                     the work with file system
         */
        @Override
        public void run() throws IOException, Vcs.VcsIncorrectUsageException {
            System.out.println(vcs.log());
        }
    }

    /** Class for parsing checkout command */
    @Parameters(commandDescription = "Alert your repository to required revision or branch")
    public class ParserCommandCheckout implements ParserCommand {
        @Parameter(names = {"-r", "--revision"}, description = "Revision to checkout")
        private String revision;

        @Parameter(names = {"-b", "--branch"}, description = "Branch to checkout")
        private String branch;

        /**
         * Runs necessary action after parsing args.
         *
         * @throws IOException if something has gone wrong during
         *                     the work with file system
         * @throws Vcs.VcsIncorrectUsageException   when vcs can't perform command because of incorrect
         *                                          usage
         * @throws Vcs.VcsRevisionNotFoundException when trying to access revision
         *                                          which doesn't exist
         * @throws Vcs.VcsBranchNotFoundException   when trying to access
         *                                          branch which doesn't exist.
         */
        @Override
        public void run() throws IOException, Vcs.VcsIncorrectUsageException, Vcs.VcsRevisionNotFoundException,
                Vcs.VcsBranchNotFoundException {
            if (revision == null && branch == null) {
                throw new ParameterException("You should provide revision or branch to commit");
            } else if (revision != null && branch != null) {
                throw new ParameterException("You should provide only branch or revision not both together");
            } else if (revision != null) {
                vcs.checkoutRevision(revision);
            } else {
                vcs.checkoutBranch(branch);
            }
        }
    }

    /**Class for parsing branch command */
    @Parameters(commandDescription = "Create or delete your branch")
    public class ParserCommandBranch implements ParserCommand {
        @Parameter(names = {"-n", "--new"}, description = "Name of new branch")
        private String branchToCreate;

        @Parameter(names = {"-d", "--delete"}, description = "Branch to delete")
        private String branchToDelete;

        /**
         * Runs necessary action after parsing args.
         *
         * @throws IOException if something has gone wrong during
         *                     the work with file system
         * @throws Vcs.VcsBranchActionForbiddenException when trying to make illegal actions
         *                                               with branch
         * @throws Vcs.VcsBranchNotFoundException        when trying to
         *                                               access branch which doesn't exist.
         * @throws Vcs.VcsIncorrectUsageException        when vcs can't perform command because of incorrect
         *                                               usage
         */
        @Override
        public void run() throws IOException, Vcs.VcsBranchActionForbiddenException, Vcs.VcsBranchNotFoundException,
                Vcs.VcsIncorrectUsageException {
            if (branchToCreate == null && branchToDelete == null) {
                throw new ParameterException("You should specify do you want to create new branch or delete old one");
            } else if (branchToCreate != null && branchToDelete != null) {
                throw new ParameterException("You can only delete branch or create new one not both actions together");
            } else if (branchToCreate != null) {
                vcs.createBranch(branchToCreate);
            } else {
                vcs.deleteBranch(branchToDelete);
            }
        }
    }

    /**Class for parsing merge command */
    @Parameters(commandDescription = "Merge current branch with specified one")
    public class ParserCommandMerge implements ParserCommand {

        @Parameter(names = {"-b", "--branch"}, description = "Branch to merge", required = true)
        private String branchToMerge;

        /**
         * Runs necessary action after parsing args.
         *
         * @throws IOException if something has gone wrong during
         *                     the work with file system
         * @throws Vcs.VcsBranchActionForbiddenException when trying to make illegal actions
         *                                               with branch
         * @throws Vcs.VcsConflictException              when conflict during merge was detected
         * @throws Vcs.VcsBranchNotFoundException        when trying to access
         *                                               branch which doesn't exist.
         * @throws Vcs.VcsIncorrectUsageException        when vcs can't perform command because of incorrect
         *                                               usage
         */
        @Override
        public void run() throws IOException, Vcs.VcsBranchActionForbiddenException, Vcs.VcsConflictException,
                Vcs.VcsBranchNotFoundException, Vcs.VcsIncorrectUsageException {
            vcs.merge(branchToMerge);
        }
    }

    /**Class for parsing reset command */
    @Parameters(commandDescription = "Reset specified file with the last version in repository")
    public class ParserCommandReset implements ParserCommand {

        @Parameter(names = {"-f", "--file"}, description = "File to reset", required = true)
        private Path fileName;

        /**
         * Runs necessary action after parsing args.
         *
         * @throws IOException if something has gone wrong during
         *                     the work with file system
         * @throws Vcs.VcsIncorrectUsageException when vcs can't perform command because of incorrect
         *                                        usage
         */
        @Override
        public void run() throws IOException, Vcs.VcsIncorrectUsageException {
            vcs.reset(fileName);
        }
    }

    /**Class for parsing rm command */
    @Parameters(commandDescription = "Remove files from repository and from disk")
    public class ParserCommandRemove implements ParserCommand {

        @Parameter(description = "Files to remove from the repo")
        private
        @Nullable
        List<Path> files;


        @Override
        public void run() throws IOException, Vcs.VcsIncorrectUsageException {
            if (files == null || files.size() == 0) throw new ParameterException("Specify files to remove");
            vcs.remove(files);
        }
    }

    /**Class for parsing clean command */
    @Parameters(commandDescription = "Remove all files not from repository")
    public class ParserCommandClean implements ParserCommand {

        @Override
        public void run() throws IOException, Vcs.VcsIncorrectUsageException {
            vcs.clean();
        }
    }

    /**Class for parsing status command */
    @Parameters(commandDescription = "Status of current repository state")
    public class ParserCommandStatus implements ParserCommand {

        @Override
        public void run() throws IOException, Vcs.VcsIncorrectUsageException {
            System.out.println(vcs.status());
        }
    }
}