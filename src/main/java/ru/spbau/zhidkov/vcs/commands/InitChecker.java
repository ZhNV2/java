package ru.spbau.zhidkov.vcs.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.zhidkov.vcs.handlers.VcsFileHandler;

import java.io.IOException;

/**Class checking whether repo has been initialized */
@SuppressWarnings("WeakerAccess")
public class InitChecker {

    private static final Logger logger = LogManager.getLogger(InitChecker.class);

    private VcsFileHandler vcsFileHandler;

    /**
     * Builds <tt>InitChecker</tt> with provided
     * <tt>VcsFileHandler</tt>
     *
     * @param vcsFileHandler vcsFileHandler
     */
    public InitChecker(VcsFileHandler vcsFileHandler) {
        this.vcsFileHandler = vcsFileHandler;
    }

    /**
     * Checks if repo has been already initialized or not.
     *
     * @return whether repo has been already initialized or not
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public boolean hasInitialized() throws IOException {
        boolean hasInitialized = vcsFileHandler.repoExists();
        logger.info("hasInitialized = {}", hasInitialized);
        return hasInitialized;
    }
}
