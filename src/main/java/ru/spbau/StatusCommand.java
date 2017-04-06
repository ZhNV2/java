package ru.spbau;

import ru.spbau.zhidkov.BranchHandler;
import ru.spbau.zhidkov.ExternalFileHandler;
import ru.spbau.zhidkov.VcsFileHandler;
import ru.spbau.zhidkov.vcs.VcsBlob;
import ru.spbau.zhidkov.vcs.VcsCommit;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;


public class StatusCommand {

    private VcsFileHandler vcsFileHandler;
    private ExternalFileHandler externalFileHandler;
    private BranchHandler branchHandler;

    public StatusCommand(VcsFileHandler vcsFileHandler, ExternalFileHandler externalFileHandler, BranchHandler branchHandler) {
        this.vcsFileHandler = vcsFileHandler;
        this.externalFileHandler = externalFileHandler;
        this.branchHandler = branchHandler;
    }

    public StatusHolder status() throws IOException {
        List<String> modifiedFiles = new ArrayList<>();
        List<String> removedFiles = vcsFileHandler.getList(VcsFileHandler.ListWithFiles.RM_LIST);
        List<String> addedFiles = vcsFileHandler.getList(VcsFileHandler.ListWithFiles.ADD_LIST);

        List<String> externalFiles = externalFileHandler.readAllExternalFiles().stream()
                .map(Path::toString)
                .filter(v -> !externalFileHandler.isDirectory(v))
                .collect(Collectors.toList());

        for (String file : addedFiles) {
            externalFiles.remove(file);
        }
        for (String file : removedFiles) {
            externalFiles.remove(file);
        }

        status(branchHandler.getHeadLastCommitHash(), externalFiles, new TreeSet<>(), removedFiles,
                addedFiles, modifiedFiles);
        return new StatusHolder(modifiedFiles, addedFiles, removedFiles, externalFiles);
    }

    private void status(String commitHash, Collection<String> curFiles, Collection<String> checked,
                        List<String> removedFiles, List<String> addedFiles,
                        List<String> modifiedFiles) throws IOException {
        VcsCommit commit = vcsFileHandler.getCommit(commitHash);
        for (Map.Entry<String, String> entry : commit.getChildrenAdd().entrySet()) {
            String fileName = entry.getKey();
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
        for (String fileName : commit.getChildrenRm()) {
            checked.add(fileName);
        }
    }

    public static class StatusHolder {
        public List<String> modifiedFiles;
        public List<String> addedFiles;
        public List<String> removedFiles;
        public List<String> foreignFiles;


        public StatusHolder(List<String> modifiedFiles, List<String> addedFiles, List<String> removedFiles, List<String> foreignFiles) {
            this.modifiedFiles = modifiedFiles;
            this.addedFiles = addedFiles;
            this.removedFiles = removedFiles;
            this.foreignFiles = foreignFiles;
        }
    }
}
