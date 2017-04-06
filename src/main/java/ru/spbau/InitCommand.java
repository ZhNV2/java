package ru.spbau;

import ru.spbau.zhidkov.CommitHandler;
import ru.spbau.zhidkov.VcsFileHandler;

import java.io.IOException;


/**
 * Class implementing init command.
 */
public class InitCommand {

    private VcsFileHandler vcsFileHandler;
    private CommitCommand commitCommand;

    public InitCommand(VcsFileHandler vcsFileHandler, CommitCommand commitCommand) {
        this.vcsFileHandler = vcsFileHandler;
        this.commitCommand = commitCommand;
    }

    /**
     * Initializes repo in the current folder.
     *
     * @param authorName author name
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void init(String authorName) throws IOException, Vcs.VcsIncorrectUsageException {
        vcsFileHandler.init(authorName);
        commitCommand.commit(CommitHandler.getInitialCommitMessage());
    }
}
