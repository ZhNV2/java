package ru.spbau.zhidkov.vcs.commands;

import ru.spbau.zhidkov.vcs.handlers.CommitHandler;
import ru.spbau.zhidkov.vcs.handlers.VcsFileHandler;

import java.io.IOException;


/**
 * Class implementing init command.
 */
@SuppressWarnings("WeakerAccess")
public class InitCommand {

    private VcsFileHandler vcsFileHandler;
    private CommitCommand commitCommand;

    /**
     * Builds <tt>InitCommand</tt> with
     * provided args
     *
     * @param vcsFileHandler vcsFileHandler
     * @param commitCommand  commitCommand
     */
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
    public void init(String authorName) throws IOException {
        vcsFileHandler.init(authorName);
        commitCommand.commit(CommitHandler.getInitialCommitMessage());
    }
}
