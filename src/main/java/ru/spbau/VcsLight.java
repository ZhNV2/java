package ru.spbau;

import ru.Vcs;
import ru.spbau.zhidkov.VcsBlob;
import ru.spbau.zhidkov.VcsCommit;
import ru.spbau.zhidkov.VcsObject;
import ru.spbau.zhidkov.VcsTree;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class VcsLight implements Vcs {
    private static final String ROOT_DIR = System.getProperty("user.dir") + File.separator + ".vcs";
    private static final String OBJECTS_DIR = ROOT_DIR + File.separator + "objects";
    private static final String BRANCHES_DIR = ROOT_DIR + File.separator + "branches";
    private static final String ADD_LIST = ROOT_DIR + File.separator + "addList";
    private static final String INITIAL_COMMIT_MESSAGE = "Initial commit.";
    private static final String HEAD = ROOT_DIR + File.separator + "HEAD";
    private static final String MASTER = "master";
    private static final String NAME = ROOT_DIR + File.separator + "NAME";

    public void init(String authorName) throws IOException {
        Files.createDirectory(Paths.get(ROOT_DIR));
        Files.createDirectory(Paths.get(OBJECTS_DIR));
        Files.createDirectory(Paths.get(BRANCHES_DIR));
        createEmptyFile(ADD_LIST);
        createEmptyFile(HEAD);
        writeToFile(HEAD, MASTER);
        commit(INITIAL_COMMIT_MESSAGE);
    }

    public void add(List<String> fileNames) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (String fileName : fileNames) {
            stringBuilder.append(fileName);
            stringBuilder.append(System.lineSeparator());
        }
        Files.write(Paths.get(ADD_LIST), stringBuilder.toString().getBytes(), StandardOpenOption.APPEND);
    }

    public void commit(String message) throws IOException {
        List<String> filesToAdd = Files.lines(Paths.get(ADD_LIST)).distinct().collect(Collectors.toList());
        VcsTree tree = new VcsTree();
        for (String file : filesToAdd) {
            VcsBlob blob = new VcsBlob(Files.readAllBytes(Paths.get(file)));
            tree.addToChildren(file, blob.getHash());
            writeToFile(blob, OBJECTS_DIR);
        }
        VcsCommit commit = new VcsCommit(message, tree, new Date());
        writeToFile(tree, OBJECTS_DIR);
        writeToFile(commit, OBJECTS_DIR);
        Files.createSymbolicLink(Paths.get(BRANCHES_DIR + File.separator + getCurrentBranch()),
                Paths.get(commit.getPath(OBJECTS_DIR)));
    }

    private String getCurrentBranch() throws IOException {
        String result = Files.lines(Paths.get(HEAD)).findAny().orElse("error");
        if (result == "error") {
            // TODO: ...
        }
        return result;
    }

    private void writeToFile(VcsObject vcsObject, String dir) throws IOException {
        Files.write(Paths.get(vcsObject.getPath(dir)), vcsObject.getContent(), StandardOpenOption.CREATE);
    }

    private void writeToFile(String fileName, String text) throws IOException {
        Files.write(Paths.get(fileName), text.getBytes(), StandardOpenOption.CREATE);
    }

    private void createEmptyFile(String fileName) throws IOException {
        Files.deleteIfExists(Paths.get(fileName));
        Files.createFile(Paths.get(fileName));
    }

}
