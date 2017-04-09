package ru.spbau;

import org.jetbrains.annotations.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.zhidkov.BranchHandler;
import ru.spbau.zhidkov.CommitHandler;
import ru.spbau.zhidkov.ExternalFileHandler;
import ru.spbau.zhidkov.VcsFileHandler;
import ru.spbau.zhidkov.vcs.VcsBlob;
import ru.spbau.zhidkov.vcs.VcsCommit;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class StatusCommand {
    private static final Logger logger = LogManager.getLogger(StatusCommand.class);
    private VcsFileHandler vcsFileHandler;
    private ExternalFileHandler externalFileHandler;
    private BranchHandler branchHandler;

    public StatusCommand(VcsFileHandler vcsFileHandler, ExternalFileHandler externalFileHandler, BranchHandler branchHandler) {
        this.vcsFileHandler = vcsFileHandler;
        this.externalFileHandler = externalFileHandler;
        this.branchHandler = branchHandler;
    }

    public StatusHolder status() throws IOException {
        logger.traceEntry();
        List<Path> modifiedFiles = new ArrayList<>();
        List<Path> removedFiles = vcsFileHandler.getList(VcsFileHandler.ListWithFiles.RM_LIST);
        List<Path> addedFiles = vcsFileHandler.getList(VcsFileHandler.ListWithFiles.ADD_LIST);

        List<Path> externalFiles = externalFileHandler.readAllExternalFiles().stream()
                .filter(((Predicate<Path>) externalFileHandler::isDirectory).negate())
                .collect(Collectors.toList());

        for (Path file : addedFiles) {
            externalFiles.remove(file);
        }
        for (Path file : removedFiles) {
            externalFiles.remove(file);
        }

        status(branchHandler.getHeadLastCommitHash(), externalFiles, new TreeSet<>(), removedFiles,
                addedFiles, modifiedFiles);
        logger.traceExit();
        return new StatusHolder(modifiedFiles, addedFiles, removedFiles, externalFiles);
    }

    private void status(String commitHash, @NotNull Collection<Path> curFiles, @NotNull Collection<Path> checked,
                        @NotNull List<Path> removedFiles, @NotNull List<Path> addedFiles,
                        @NotNull List<Path> modifiedFiles) throws IOException {
        VcsCommit commit = vcsFileHandler.getCommit(commitHash);
        for (Map.Entry<Path, String> entry : commit.getChildrenAdd().entrySet()) {
            Path fileName = entry.getKey();
            if (checked.contains(fileName)) continue;
            checked.add(fileName);
            if (curFiles.contains(fileName)) {
                byte[] content1 = externalFileHandler.readAllBytes(fileName);
                VcsBlob blob = vcsFileHandler.getBlob(entry.getValue());
                if (!Arrays.equals(content1, blob.getContent())) {
                    if (!addedFiles.contains(fileName)) {
                        modifiedFiles.add(fileName);
                    }
                }
                curFiles.remove(fileName);
            } else {
                if (!removedFiles.contains(fileName)) {
                    modifiedFiles.add(fileName);
                }
            }
        }
        for (Path fileName : commit.getChildrenRm()) {
            checked.add(fileName);
        }
        if (!commit.getPrevCommitHash().equals(CommitHandler.getInitialCommitPrevHash())) {
            status(commit.getPrevCommitHash(), curFiles, checked, removedFiles, addedFiles, modifiedFiles);
        }
    }

    public static class StatusHolder {
        public List<Path> modifiedFiles;
        public List<Path> addedFiles;
        public List<Path> removedFiles;
        public List<Path> foreignFiles;


        public StatusHolder(List<Path> modifiedFiles, List<Path> addedFiles, List<Path> removedFiles, List<Path> foreignFiles) {
            this.modifiedFiles = modifiedFiles;
            this.addedFiles = addedFiles;
            this.removedFiles = removedFiles;
            this.foreignFiles = foreignFiles;
        }
    }
}
