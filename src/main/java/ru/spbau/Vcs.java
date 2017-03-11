package ru.spbau;

import ru.spbau.zhidkov.VcsBlob;
import ru.spbau.zhidkov.VcsCommit;
import ru.spbau.zhidkov.VcsObject;
import ru.spbau.zhidkov.VcsTree;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

abstract public class Vcs {
    private static final String ROOT_DIR = System.getProperty("user.dir") + File.separator + ".vcs";
    private static final String OBJECTS_DIR = ROOT_DIR + File.separator + "objects";
    private static final String BRANCHES_DIR = ROOT_DIR + File.separator + "branches";
    private static final String ADD_LIST = ROOT_DIR + File.separator + "addList";
    private static final String INITIAL_COMMIT_MESSAGE = "Initial commit.";
    private static final String ONE_LINE_VARS_DIR = ROOT_DIR + File.separator + "one_lines_vars";
    private static final String HEAD = ONE_LINE_VARS_DIR + File.separator + "HEAD";
    private static final String MASTER = "master";
    private static final String AUTHOR_NAME = ONE_LINE_VARS_DIR + File.separator + "AUTHOR_NAME";
    private static final String INITIAL_COMMIT_PREV_HASH = "";

    public static void init(String authorName) throws IOException {
        Files.createDirectory(Paths.get(ROOT_DIR));
        Files.createDirectory(Paths.get(OBJECTS_DIR));
        Files.createDirectory(Paths.get(BRANCHES_DIR));
        Files.createDirectory(Paths.get(ONE_LINE_VARS_DIR));
        createEmptyFile(ADD_LIST);
        createEmptyFile(HEAD);
        createEmptyFile(AUTHOR_NAME);
        writeToFile(AUTHOR_NAME, authorName);
        writeToFile(HEAD, MASTER);
        commit(INITIAL_COMMIT_MESSAGE);
    }

    public static void add(List<String> fileNames) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (String fileName : fileNames) {
            stringBuilder.append(fileName);
            stringBuilder.append(System.lineSeparator());
        }
        Files.write(Paths.get(ADD_LIST), stringBuilder.toString().getBytes(), StandardOpenOption.APPEND);
    }

    public static void commit(String message) throws IOException {
        List<String> filesToAdd = Files.lines(Paths.get(ADD_LIST)).distinct().collect(Collectors.toList());
        VcsTree tree;
        if (message.equals(INITIAL_COMMIT_MESSAGE)) {
            tree = new VcsTree(new HashMap<>());
        } else {
            String lastCommitHash = getFirstLine(BRANCHES_DIR + File.separator + getFirstLine(HEAD));
            VcsCommit lastCommit = getCommit(lastCommitHash);
            VcsTree lastTree = getTree(lastCommit);
            tree = new VcsTree(lastTree.getChildren());
        }
        for (String file : filesToAdd) {
            VcsBlob blob = new VcsBlob(Files.readAllBytes(Paths.get(file)));
            tree.addToChildren(file, blob.getHash());
            writeToFile(blob, OBJECTS_DIR);
        }
        VcsCommit commit = new VcsCommit(tree.getHash(), message, new Date(), getFirstLine(AUTHOR_NAME),
                message.equals(INITIAL_COMMIT_MESSAGE) ? INITIAL_COMMIT_PREV_HASH :
                        getFirstLine(BRANCHES_DIR + File.separator + getFirstLine(HEAD)));
        writeToFile(tree, OBJECTS_DIR);
        writeToFile(commit, OBJECTS_DIR);
        writeToFile(BRANCHES_DIR + File.separator + getFirstLine(HEAD), commit.getHash());
        writeToFile(ADD_LIST, "");
    }

    public static StringBuilder log() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(System.getProperty("line.separator"));
        stringBuilder.append("On branch ").append(getFirstLine(HEAD)).append(System.getProperty("line.separator"));
        stringBuilder.append(System.getProperty("line.separator"));
        String commitHash = getFirstLine(BRANCHES_DIR + File.separator + getFirstLine(HEAD));
        while (!commitHash.equals(INITIAL_COMMIT_PREV_HASH)) {
            VcsCommit commit = (VcsCommit) VcsObject.readFromJson(OBJECTS_DIR + File.separator + commitHash, VcsCommit.class);
            commit.print(stringBuilder);
            commitHash = commit.getPrevCommitHash();
        }
        return stringBuilder;
    }

    public static void checkout(String commitHash, String branchName) throws IOException {
            if (branchName != null) commitHash = getFirstLine(BRANCHES_DIR + File.separator + branchName);
        String lastCommitHash = getFirstLine(BRANCHES_DIR + File.separator + getFirstLine(HEAD));
        if (lastCommitHash.equals(commitHash)) return;
        if (!getFirstLine(ADD_LIST).equals("")) {
            // TODO:
        }
        VcsCommit lastCommit = getCommit(lastCommitHash);
        VcsTree lastTree = getTree(lastCommit);
        for (Map.Entry<String, String> entry : lastTree.getChildren().entrySet()) {
            Files.deleteIfExists(Paths.get(entry.getKey()));
        }
        VcsCommit checkoutCommit = (VcsCommit) VcsObject.readFromJson(OBJECTS_DIR + File.separator + commitHash, VcsCommit.class);
        VcsTree checkoutTree = (VcsTree) VcsObject.readFromJson(OBJECTS_DIR + File.separator + checkoutCommit.getTreeHash(), VcsTree.class);
        for (Map.Entry<String, String> entry : checkoutTree.getChildren().entrySet()) {
            VcsBlob blob = (VcsBlob) VcsObject.readFromJson(OBJECTS_DIR + File.separator + entry.getValue(), VcsBlob.class);
            writeToFile(entry.getKey(), blob.getContent());
        }
        if (branchName == null) {
            writeToFile(BRANCHES_DIR + File.separator + getFirstLine(HEAD), commitHash);
        } else {
            writeToFile(HEAD, branchName);
        }
    }



    public static void createBranch(String branchName) throws IOException {
        writeToFile(BRANCHES_DIR + File.separator + branchName,
                getFirstLine(BRANCHES_DIR + File.separator + getFirstLine(HEAD)));
    }

    public static void deleteBranch(String branchName) throws IOException {
        if (getFirstLine(HEAD).equals(branchName)) {
            // TODO:
        }
        Files.deleteIfExists(Paths.get(BRANCHES_DIR + File.separator + branchName));
    }

    private static VcsCommit getCommit(String commitHash) throws IOException {
        return (VcsCommit) VcsObject.readFromJson(OBJECTS_DIR + File.separator + commitHash, VcsCommit.class);
    }

    private static VcsTree getTree(VcsCommit commit) throws IOException {
        return (VcsTree) VcsObject.readFromJson(OBJECTS_DIR + File.separator + commit.getTreeHash(), VcsTree.class);
    }

    private static String getFirstLine(String fileName) throws IOException {
        String result = Files.lines(Paths.get(fileName)).findFirst().orElse("error");
        if (result.equals("error")) {
            // TODO: ...
        }
        return result;
    }

    private static void writeToFile(VcsObject vcsObject, String dir) throws IOException {
        vcsObject.writeAsJson(vcsObject.getPath(dir));
    }

    private static void writeToFile(String fileName, byte[] content) throws IOException {
        Files.write(Paths.get(fileName), content);
    }

    private static void writeToFile(String fileName, String text) throws IOException {
        Files.write(Paths.get(fileName), text.getBytes());
    }

    private static void createEmptyFile(String fileName) throws IOException {
        Files.deleteIfExists(Paths.get(fileName));
        Files.createFile(Paths.get(fileName));
    }

}
