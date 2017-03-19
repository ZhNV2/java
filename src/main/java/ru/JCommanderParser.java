package ru;

import com.beust.jcommander.*;
import ru.spbau.Vcs;

import java.io.IOException;
import java.util.List;

public class JCommanderParser {

    @Parameter(names = "--help", help = true)
    public boolean help;

    @Parameters(commandDescription = "Initialize empty repository in the current folder")
    public static class CommandInit implements Command {
        @Parameter(names = "--author", required = true, description = "Author's name")
        private String author;

        @Override
        public void run() throws IOException {
            Vcs.init(author);
        }
    }

    @Parameters(commandDescription = "Add file contents to the index")
    public static class CommandAdd implements Command {

        @Parameter(description = "Files to add to the index")
        private List<String> files;

        @Override
        public void run() throws IOException {
            Vcs.add(files);
        }

    }

    @Parameters(commandDescription = "Record changes to the repository")
    public static class CommandCommit implements Command {

        @Parameter(names = { "-m", "--message"}, description = "Commit message", required = true)
        private String message;

        @Override
        public void run() throws IOException {
            if (message.equals(Vcs.INITIAL_COMMIT_MESSAGE)) {
                throw new IllegalArgumentException("This commit message is reserved for first commit. Please, use another one");
            }
            Vcs.commit(message);
        }
    }

    @Parameters(commandDescription = "Print log containing information about commits")
    public static class CommandLog implements Command {
        @Override
        public void run() throws IOException {
            System.out.println(Vcs.log().toString());
        }
    }

    @Parameters(commandDescription = "Alert your repository to required revision or branch")
    public static class CommandCheckout implements Command {
        @Parameter(names = { "-r", "--revision"}, description = "Revision to checkout")
        private String revision;

        @Parameter(names = { "-b", "--branch"}, description = "Branch to checkout")
        private String branch;

        @Override
        public void run() throws IOException {
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

    @Parameters(commandDescription = "Create or delete your branch")
    public static class CommandBranch implements Command {
        @Parameter(names = { "-n", "--new"}, description = "Name of new branch")
        private String branchToCreate;

        @Parameter(names = { "-d", "--delete"}, description = "Branch to delete")
        private String branchToDelete;

        @Override
        public void run() throws IOException {
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

    @Parameters(commandDescription = "Merge current branch with specified one")
    public static class CommandMerge implements Command {

        @Parameter(names = {"-b", "--branch"}, description = "Branch to merge", required = true)
        private String branchToMerge;

        @Override
        public void run() throws IOException {
            Vcs.merge(branchToMerge);
        }
    }


}