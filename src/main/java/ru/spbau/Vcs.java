package ru.spbau;

import ru.spbau.zhidkov.VcsBlob;
import ru.spbau.zhidkov.VcsCommit;
import ru.spbau.zhidkov.VcsObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;


abstract public class Vcs {
    private static final String CURRENT_FOLDER = System.getProperty("user.dir");
    private static final String ROOT_DIR = CURRENT_FOLDER + File.separator + ".vcs";
    private static final String OBJECTS_DIR = ROOT_DIR + File.separator + "objects";
    private static final String BRANCHES_DIR = ROOT_DIR + File.separator + "branches";
    private static final String ADD_LIST = ROOT_DIR + File.separator + "addList";
    public static final String INITIAL_COMMIT_MESSAGE = "Initial commit.";
    private static final String ONE_LINE_VARS_DIR = ROOT_DIR + File.separator + "one_lines_vars";
    private static final String HEAD = ONE_LINE_VARS_DIR + File.separator + "HEAD";
    private static final String MASTER = "master";
    private static final String AUTHOR_NAME = ONE_LINE_VARS_DIR + File.separator + "AUTHOR_NAME";
    private static final String INITIAL_COMMIT_PREV_HASH = "";
    private static final String MERGE_MESSAGE = "Merged with branch ";
    private static final String WORKING_COPY = CURRENT_FOLDER + File.separator + ".wc";

    public static void saveWorkingCopy() throws IOException {
        List<Path> files = FileSystem.getAllFilesFromDirInOrder(CURRENT_FOLDER).stream()
                .filter(v->!v.startsWith(ROOT_DIR))
                .collect(Collectors.toList());
        FileSystem.copyFilesToDir(CURRENT_FOLDER, files, WORKING_COPY);
    }

    public static void restoreWorkingCopy() throws IOException {
        for (Path fileName : FileSystem.getAllFilesFromDirInRevOrder(CURRENT_FOLDER)) {
            if (!fileName.startsWith(ROOT_DIR) && !fileName.startsWith(WORKING_COPY)) {
                if (!fileName.equals(Paths.get(CURRENT_FOLDER)))
                    FileSystem.deleteIfExists(fileName.toString());
            }
        }
        FileSystem.copyFilesToDir(WORKING_COPY, FileSystem.getAllFilesFromDirInOrder(WORKING_COPY), CURRENT_FOLDER);
        clearWorkingCopy();
    }

    public static void clearWorkingCopy() throws IOException {
        FileSystem.deleteFolder(WORKING_COPY);
    }

    public static void init(String authorName) throws IOException {
        FileSystem.createDirectory(ROOT_DIR);
        FileSystem.createDirectory(OBJECTS_DIR);
        FileSystem.createDirectory(BRANCHES_DIR);
        FileSystem.createDirectory(ONE_LINE_VARS_DIR);
        FileSystem.createEmptyFile(ADD_LIST);
        FileSystem.createEmptyFile(HEAD);
        FileSystem.createEmptyFile(AUTHOR_NAME);
        FileSystem.writeToFile(AUTHOR_NAME, authorName);
        FileSystem.writeToFile(HEAD, MASTER);
        commit(INITIAL_COMMIT_MESSAGE);
    }

    public static void add(List<String> fileNames) throws IOException {
        for (String fileName : fileNames) {
            if (!FileSystem.exists(fileName)) {
                throw new FileNotFoundException(fileName);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String fileName : fileNames) {
            stringBuilder.append(fileName);
            stringBuilder.append(System.lineSeparator());
        }

        FileSystem.appendToFile(ADD_LIST, stringBuilder.toString().getBytes());
    }

    public static void commit(String message) throws IOException {
        List<String> filesToAdd = FileSystem.readAllLines(ADD_LIST);
        VcsCommit commit = new VcsCommit(message, new Date(), FileSystem.getFirstLine(AUTHOR_NAME),
                message.equals(INITIAL_COMMIT_MESSAGE) ? INITIAL_COMMIT_PREV_HASH :
                        FileSystem.getFirstLine(BRANCHES_DIR + File.separator + FileSystem.getFirstLine(HEAD)), new HashMap<>());

        Collection<VcsBlob> blobs = new ArrayList<>();
        for (String file : filesToAdd) {
            VcsBlob blob = new VcsBlob(FileSystem.readAllBytes(file));
            commit.addToChildren(file, blob.getHash());
            blobs.add(blob);
        }
        for (VcsBlob blob : blobs) {
            FileSystem.writeToFile(blob, OBJECTS_DIR);
        }
        FileSystem.writeToFile(commit, OBJECTS_DIR);
        FileSystem.writeToFile(BRANCHES_DIR + File.separator + FileSystem.getFirstLine(HEAD), commit.getHash());
        FileSystem.writeToFile(ADD_LIST, "");
    }

    public static StringBuilder log() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("On branch ").append(FileSystem.getFirstLine(HEAD)).append(System.getProperty("line.separator"));
        stringBuilder.append(System.getProperty("line.separator"));
        String commitHash = FileSystem.getFirstLine(BRANCHES_DIR + File.separator + FileSystem.getFirstLine(HEAD));
        while (!commitHash.equals(INITIAL_COMMIT_PREV_HASH)) {
            VcsCommit commit = (VcsCommit) VcsObject.readFromJson(OBJECTS_DIR + File.separator + commitHash, VcsCommit.class);
            commit.print(stringBuilder);
            commitHash = commit.getPrevCommitHash();
        }
        return stringBuilder;
    }

    public static void checkoutBranch(String branchName) throws IOException {
        checkIfBranchExist(branchName);
        String commitHash = FileSystem.getFirstLine(BRANCHES_DIR + File.separator + branchName);
        checkout(commitHash);

        FileSystem.writeToFile(HEAD, branchName);
    }

    public static void checkoutRevision(String commitHash) throws IOException {
        checkIfRevisionExist(commitHash);
        checkout(commitHash);

        FileSystem.writeToFile(BRANCHES_DIR + File.separator + FileSystem.getFirstLine(HEAD), commitHash);
    }

    private static void checkout(String commitHash) throws IOException {
        String lastCommitHash = FileSystem.getFirstLine(BRANCHES_DIR + File.separator + FileSystem.getFirstLine(HEAD));
        if (lastCommitHash.equals(commitHash)) return;
        if (!FileSystem.getFirstLine(ADD_LIST).equals("")) {
            throw new IllegalStateException("You have several files were added, but haven't committed yet");
        }
        deleteCommittedFiles(lastCommitHash);
        Collection<String> restored = new HashSet<>();
        restore(commitHash, restored);
    }

    public static void merge(String branchToMerge) throws IOException {
        if (branchToMerge.equals(FileSystem.getFirstLine(HEAD))) {
            throw new IllegalArgumentException("You can't merge branch with itself");
        }
        checkIfBranchExist(branchToMerge);
        VcsCommit commit = new VcsCommit(MERGE_MESSAGE + branchToMerge, new Date(), FileSystem.getFirstLine(AUTHOR_NAME),
                        FileSystem.getFirstLine(BRANCHES_DIR + File.separator + FileSystem.getFirstLine(HEAD)), new HashMap<>());
        Collection<String> checked = new HashSet<>();
        mergeCommit(FileSystem.getFirstLine(BRANCHES_DIR + File.separator + branchToMerge), checked, commit);

        FileSystem.writeToFile(commit, OBJECTS_DIR);
        FileSystem.writeToFile(BRANCHES_DIR + File.separator + FileSystem.getFirstLine(HEAD), commit.getHash());
    }

    public static void createBranch(String branchName) throws IOException {
        if (FileSystem.exists(BRANCHES_DIR + File.separator + branchName)) {
            throw new IllegalArgumentException("Branch with this name is already created");
        }
        FileSystem.writeToFile(BRANCHES_DIR + File.separator + branchName,
                FileSystem.getFirstLine(BRANCHES_DIR + File.separator + FileSystem.getFirstLine(HEAD)));
    }

    public static void deleteBranch(String branchName) throws IOException {
        if (FileSystem.getFirstLine(HEAD).equals(branchName)) {
            throw new IllegalArgumentException("You can't remove current branch");
        }
        checkIfBranchExist(branchName);
        FileSystem.deleteIfExists(BRANCHES_DIR + File.separator + branchName);
    }

    private static void mergeCommit(String commitHash, Collection<String> checked, VcsCommit newCommit) throws IOException {
        VcsCommit commit = getCommit(commitHash);
        for (Map.Entry<String, String> entry : commit.getChildren().entrySet()) {
            if (checked.contains(entry.getKey())) continue;
            String fileName = entry.getKey();
            checked.add(fileName);
            VcsBlob blob = (VcsBlob) VcsObject.readFromJson(OBJECTS_DIR + File.separator + entry.getValue(), VcsBlob.class);
            if (FileSystem.exists(fileName)) {
                byte[] fileBytes = FileSystem.readAllBytes(fileName);
                if (!Arrays.equals(fileBytes, blob.getContent())) {
                    throw new IllegalStateException("Can't merge, because file " + fileName + " is different in both branches");
                }
            } else {
                newCommit.addToChildren(fileName, blob.getHash());
                FileSystem.writeToFile(fileName, blob.getContent());
            }
        }
        if (!commit.getPrevCommitHash().equals(INITIAL_COMMIT_PREV_HASH)) {
            restore(commit.getPrevCommitHash(), checked);
        }
    }

    private static void deleteCommittedFiles(String commitHash) throws IOException {
        VcsCommit commit = getCommit(commitHash);
        for (Map.Entry<String, String> entry : commit.getChildren().entrySet()) {
            FileSystem.deleteIfExists(entry.getKey());
        }
        if (!commit.getPrevCommitHash().equals(INITIAL_COMMIT_PREV_HASH)) {
            deleteCommittedFiles(commit.getPrevCommitHash());
        }
    }

    private static void restore(String commitHash, Collection<String> restored) throws IOException {
        VcsCommit commit = getCommit(commitHash);
        for (Map.Entry<String, String> entry : commit.getChildren().entrySet()) {
            if (!restored.contains(entry.getKey())) {
                VcsBlob blob = (VcsBlob) VcsObject.readFromJson(OBJECTS_DIR + File.separator + entry.getValue(), VcsBlob.class);
                FileSystem.writeToFile(entry.getKey(), blob.getContent());
                restored.add(entry.getKey());
            }
        }
        if (!commit.getPrevCommitHash().equals(INITIAL_COMMIT_PREV_HASH)) {
            restore(commit.getPrevCommitHash(), restored);
        }
    }

    private static VcsCommit getCommit(String commitHash) throws IOException {
        return (VcsCommit) VcsObject.readFromJson(OBJECTS_DIR + File.separator + commitHash, VcsCommit.class);
    }

    private static void checkIfBranchExist(String branchName) throws IOException {
        if (!FileSystem.exists(BRANCHES_DIR + File.separator + branchName)) {
            throw new IllegalArgumentException("Provided branch doesn't exist");
        }
    }

    private static void checkIfRevisionExist(String commitHash) throws IOException {
        if (!FileSystem.exists(OBJECTS_DIR + File.separator + commitHash)) {
            throw new IllegalArgumentException("Provided revision doesn't exist");
        }
    }
}
