package ru;

import ru.spbau.Vcs;

import java.io.IOException;

/**
 * Interface for basic command line commands.
 */
public interface Command {
    /**
     * Method should be started after processing command's arguments.
     *
     * @throws IOException      if something has gone wrong during
     *                          the work with file system
     * @throws Vcs.VcsException if something has gone wrong
     *                          during the vcs work
     */
    void run() throws IOException, Vcs.VcsException;
}
