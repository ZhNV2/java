package ru.spbau;

import ru.spbau.zhidkov.VcsBlob;
import ru.spbau.zhidkov.VcsCommit;
import ru.spbau.zhidkov.VcsObject;
import ru.spbau.zhidkov.vcs.FileSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static ru.spbau.Commit.getCommit;

public class Status {

    public static StatusHolder status() throws IOException {
        List<String> modifiedFiles = new ArrayList<>();
        List<String> removedFiles = FileSystem.readAllLines(Vcs.getRmList());
        List<String> addedFiles = FileSystem.readAllLines(Vcs.getAddList());

        List<String> curFiles = FileSystem.readAllFiles(Vcs.getCurrentFolder()).stream()
                .filter(v -> !v.startsWith(Vcs.getRootDir()))
                .filter(v -> !v.startsWith(Vcs.getWorkingCopy()))
                .map(Path::toString)
                .filter(v -> !FileSystem.isDirectory(v))
                .collect(Collectors.toList());

        status(Branch.getBranchLastCommitHash(Branch.getHeadBranch()), curFiles, new TreeSet<>(), removedFiles,
                addedFiles, modifiedFiles);
        return new StatusHolder(modifiedFiles, addedFiles, removedFiles, curFiles);
    }

    private static void status(String commitHash, Collection<String> curFiles, Collection<String> checked,
                        List<String> removedFiles, List<String> addedFiles,
                        List<String> modifiedFiles) throws IOException {
        VcsCommit commit = getCommit(commitHash);
        for (Map.Entry<String, String> entry : commit.getChildrenAdd().entrySet()) {
            String fileName = entry.getKey();
            if (checked.contains(fileName)) continue;
            checked.add(fileName);
            if (curFiles.contains(fileName)) {
                byte[] content1 = FileSystem.readAllBytes(fileName);
                VcsBlob blob = (VcsBlob) VcsObject.readFromJson(Vcs.getObjectsDir() + File.separator + entry.getValue(), VcsBlob.class);
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
