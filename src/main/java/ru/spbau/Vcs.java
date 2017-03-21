package ru.spbau;

import com.beust.jcommander.ParameterException;
import ru.spbau.zhidkov.VcsBlob;
import ru.spbau.zhidkov.VcsCommit;
import ru.spbau.zhidkov.VcsObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


abstract public class Vcs {


    public static void setCurrentFolder(String currentFolder) {
        CURRENT_FOLDER = currentFolder;
        ROOT_DIR = CURRENT_FOLDER + File.separator + ".vcs";
        OBJECTS_DIR = ROOT_DIR + File.separator + "objects";
        BRANCHES_DIR = ROOT_DIR + File.separator + "branches";
        ADD_LIST = ROOT_DIR + File.separator + "addList";
        INITIAL_COMMIT_MESSAGE = "Initial commit.";
        ONE_LINE_VARS_DIR = ROOT_DIR + File.separator + "one_lines_vars";
        HEAD = ONE_LINE_VARS_DIR + File.separator + "HEAD";
        MASTER = "master";
        AUTHOR_NAME = ONE_LINE_VARS_DIR + File.separator + "AUTHOR_NAME";
        INITIAL_COMMIT_PREV_HASH = "";
        MERGE_MESSAGE = "Merged with branch ";
        WORKING_COPY = CURRENT_FOLDER + File.separator + ".wc";
    }

    private static String CURRENT_FOLDER;
    private static String ROOT_DIR;
    private static String OBJECTS_DIR;
    private static String BRANCHES_DIR;
    private static String ADD_LIST;
    private static String INITIAL_COMMIT_MESSAGE;
    private static String ONE_LINE_VARS_DIR;
    private static String HEAD;
    private static String MASTER;
    private static String AUTHOR_NAME;
    private static String INITIAL_COMMIT_PREV_HASH;
    private static String MERGE_MESSAGE;
    private static String WORKING_COPY;

    public static void saveWorkingCopy() throws IOException {
        List<Path> files = FileSystem.readAllFiles(CURRENT_FOLDER).stream()
                .filter(v->!v.startsWith(ROOT_DIR))
                .collect(Collectors.toList());
        FileSystem.copyFilesToDir(CURRENT_FOLDER, files, WORKING_COPY);
    }

    public static void restoreWorkingCopy() throws IOException {
        System.out.println("Restoring working copy");
        List<Path> filesInRevOrd = FileSystem.readAllFiles(CURRENT_FOLDER).stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        for (Path fileName : filesInRevOrd) {
            if (!fileName.startsWith(ROOT_DIR) && !fileName.startsWith(WORKING_COPY)) {
                if (!fileName.equals(Paths.get(CURRENT_FOLDER)))
                    FileSystem.deleteIfExists(fileName.toString());
            }
        }
        FileSystem.copyFilesToDir(WORKING_COPY, FileSystem.readAllFiles(WORKING_COPY), CURRENT_FOLDER);
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
        FileSystem.writeStringToFile(AUTHOR_NAME, authorName);
        FileSystem.writeStringToFile(HEAD, MASTER);
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
        FileSystem.writeStringToFile(BRANCHES_DIR + File.separator + FileSystem.getFirstLine(HEAD), commit.getHash());
        FileSystem.writeStringToFile(ADD_LIST, "");
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

        FileSystem.writeStringToFile(HEAD, branchName);
    }

    public static void checkoutRevision(String commitHash) throws IOException {
        checkIfRevisionExist(commitHash);
        checkout(commitHash);

        FileSystem.writeStringToFile(BRANCHES_DIR + File.separator + FileSystem.getFirstLine(HEAD), commitHash);
    }

    private static void checkout(String commitHash) throws IOException {
        String lastCommitHash = FileSystem.getFirstLine(BRANCHES_DIR + File.separator + FileSystem.getFirstLine(HEAD));
        if (lastCommitHash.equals(commitHash)) return;
        if (!FileSystem.getFirstLine(ADD_LIST).equals("")) {
            throw new ParameterException("You have several files were added, but haven't committed yet");
        }
        deleteCommittedFiles(lastCommitHash);
        Collection<String> restored = new HashSet<>();
        restore(commitHash, restored);
    }

    public static void merge(String branchToMerge) throws IOException {
        if (branchToMerge.equals(FileSystem.getFirstLine(HEAD))) {
            throw new ParameterException("You can't merge branch with itself");
        }
        checkIfBranchExist(branchToMerge);
        VcsCommit commit = new VcsCommit(MERGE_MESSAGE + branchToMerge, new Date(), FileSystem.getFirstLine(AUTHOR_NAME),
                        FileSystem.getFirstLine(BRANCHES_DIR + File.separator + FileSystem.getFirstLine(HEAD)), new HashMap<>());
        Collection<String> checked = new HashSet<>();
        mergeCommit(FileSystem.getFirstLine(BRANCHES_DIR + File.separator + branchToMerge), checked, commit);

        FileSystem.writeToFile(commit, OBJECTS_DIR);
        FileSystem.writeStringToFile(BRANCHES_DIR + File.separator + FileSystem.getFirstLine(HEAD), commit.getHash());
    }

    public static void createBranch(String branchName) throws IOException {
        if (FileSystem.exists(BRANCHES_DIR + File.separator + branchName)) {
            throw new ParameterException("Branch with this name is already created");
        }
        FileSystem.writeStringToFile(BRANCHES_DIR + File.separator + branchName,
                FileSystem.getFirstLine(BRANCHES_DIR + File.separator + FileSystem.getFirstLine(HEAD)));
    }

    public static void deleteBranch(String branchName) throws IOException {
        if (FileSystem.getFirstLine(HEAD).equals(branchName)) {
            throw new ParameterException("You can't remove current branch");
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
                    throw new ParameterException("Can't merge, because file " + fileName + " is different in both branches");
                }
            } else {
                newCommit.addToChildren(fileName, blob.getHash());
                FileSystem.writeBytesToFile(fileName, blob.getContent());
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
                FileSystem.writeBytesToFile(entry.getKey(), blob.getContent());
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
            throw new ParameterException("Provided branch doesn't exist");
        }
    }

    private static void checkIfRevisionExist(String commitHash) throws IOException {
        if (!FileSystem.exists(OBJECTS_DIR + File.separator + commitHash)) {
            throw new ParameterException("Provided revision doesn't exist");
        }
    }

    public static String getCurrentFolder() {
        return CURRENT_FOLDER;
    }

    public static String getRootDir() {
        return ROOT_DIR;
    }

    public static String getObjectsDir() {
        return OBJECTS_DIR;
    }

    public static String getBranchesDir() {
        return BRANCHES_DIR;
    }

    public static String getAddList() {
        return ADD_LIST;
    }

    public static String getInitialCommitMessage() {
        return INITIAL_COMMIT_MESSAGE;
    }

    public static String getOneLineVarsDir() {
        return ONE_LINE_VARS_DIR;
    }

    public static String getHEAD() {
        return HEAD;
    }

    public static String getMASTER() {
        return MASTER;
    }

    public static String getAuthorName() {
        return AUTHOR_NAME;
    }

    public static String getInitialCommitPrevHash() {
        return INITIAL_COMMIT_PREV_HASH;
    }

    public static String getMergeMessage() {
        return MERGE_MESSAGE;
    }

    public static String getWorkingCopy() {
        return WORKING_COPY;
    }
}
