package ru.spbau.zhidkov;

import ru.spbau.Vcs;
import ru.spbau.zhidkov.vcs.FileSystem;
import ru.spbau.zhidkov.vcs.VcsBlob;
import ru.spbau.zhidkov.vcs.VcsCommit;
import ru.spbau.zhidkov.vcs.VcsObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Нико on 30.03.2017.
 */
public class VcsFileHandler {
    
    private FileSystem fileSystem;
    
    public VcsFileHandler(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        String CURRENT_FOLDER = "";
        ROOT_DIR = CURRENT_FOLDER + ".vcs";
        OBJECTS_DIR = ROOT_DIR + File.separator + "objects";
        BRANCHES_DIR = ROOT_DIR + File.separator + "branches";
        ADD_LIST = ROOT_DIR + File.separator + "addList";
        RM_LIST = ROOT_DIR + File.separator + "rmList";
        ONE_LINE_VARS_DIR = ROOT_DIR + File.separator + "one_lines_vars";
        HEAD = ONE_LINE_VARS_DIR + File.separator + "HEAD";
        MASTER = "master";
        AUTHOR_NAME = ONE_LINE_VARS_DIR + File.separator + "AUTHOR_NAME";
    }

    private final String ROOT_DIR;
    private final String OBJECTS_DIR;
    private final String BRANCHES_DIR;
    private final String ADD_LIST;
    private final String RM_LIST;
    private final String ONE_LINE_VARS_DIR;
    private final String HEAD;
    private final String MASTER;
    private final String AUTHOR_NAME;

    public boolean commitExists(String commitHash) throws IOException {
        return fileSystem.exists(OBJECTS_DIR + File.separator + commitHash);
    }

    public boolean branchExists(String branchName) throws IOException {
        return fileSystem.exists(BRANCHES_DIR + File.separator + branchName);
        
    }

    public void setBranchCommit(String branchName, String commitHash) throws IOException {
        fileSystem.writeStringToFile(BRANCHES_DIR + File.separator + branchName, commitHash);
    }

    public VcsCommit getBranchCommit(String branchName) throws IOException {
        return getCommit(fileSystem.getFirstLine(BRANCHES_DIR + File.separator + branchName));
    }

    public String getHeadBranch() throws IOException {
        return fileSystem.getFirstLine(HEAD);
    }

    public void deleteBranch(String branchName) throws IOException {
        fileSystem.deleteIfExists(BRANCHES_DIR + File.separator + branchName);
    }

    public void setHeadBranch(String headBranch) throws IOException {
        fileSystem.writeStringToFile(HEAD, headBranch);
    }

    public enum ListWithFiles {
        ADD_LIST, RM_LIST;
    }

    private String getListEnum(ListWithFiles listWithFiles) {
        switch (listWithFiles) {
            case ADD_LIST: return ADD_LIST;
            case RM_LIST: return RM_LIST;
        }
        throw new IllegalArgumentException();
    }
    
    public void removeFromList(ListWithFiles listWithFiles, List<String> files) throws IOException {
        fileSystem.writeStringToFile(getListEnum(listWithFiles), connectFilesToString(siftedList(fileSystem.readAllLines(getListEnum(listWithFiles)), files)));
    }

    public void addToList(ListWithFiles listWithFiles, List<String> files) throws IOException {
        fileSystem.appendToFile(getListEnum(listWithFiles), connectFilesToString(fileSystem.normalize(files)).getBytes());
    }

    public List<String> getList(ListWithFiles listWithFiles) throws IOException {
        return fileSystem.readAllLines(getListEnum(listWithFiles));
    }

    public void assertListEmpty(ListWithFiles listWithFiles) throws IOException, Vcs.VcsIncorrectUsageException {
        if (!fileSystem.getFirstLine(getListEnum(listWithFiles)).equals("")) {
            throw new Vcs.VcsIncorrectUsageException("You have several files were added, but haven't committed yet");
        }
    }

    public void clearList(ListWithFiles listWithFiles) throws IOException {
        fileSystem.writeStringToFile(getListEnum(listWithFiles), "");
    }




    public String getAuthorName() throws IOException {
        return fileSystem.getFirstLine(AUTHOR_NAME);
    }

    public void writeBlob(VcsBlob blob) throws IOException {
        blob.writeAsJson(OBJECTS_DIR + File.separator + blob.getHash());
    }

    public void writeCommit(VcsCommit commit) throws IOException {
        commit.writeAsJson(OBJECTS_DIR + File.separator + commit.getHash());
    }

    public void init(String authorName) throws IOException {
        fileSystem.createDirectory(ROOT_DIR);
        fileSystem.createDirectory(OBJECTS_DIR);
        fileSystem.createDirectory(BRANCHES_DIR);
        fileSystem.createDirectory(ONE_LINE_VARS_DIR);
        fileSystem.createEmptyFile(ADD_LIST);
        fileSystem.createEmptyFile(RM_LIST);
        fileSystem.createEmptyFile(HEAD);
        fileSystem.createEmptyFile(AUTHOR_NAME);
        fileSystem.writeStringToFile(AUTHOR_NAME, authorName);
        fileSystem.writeStringToFile(HEAD, MASTER);
    }

    public boolean repoExists() throws IOException {
        return fileSystem.exists(ROOT_DIR);
    }

    public VcsBlob getBlob(String blobHash) throws IOException {
        return  (VcsBlob) VcsObject.readFromJson(OBJECTS_DIR + File.separator + blobHash, VcsBlob.class);
    }

    public VcsCommit getCommit(String commitHash) throws IOException {
        return  (VcsCommit) VcsObject.readFromJson(OBJECTS_DIR + File.separator + commitHash, VcsCommit.class);
    }


    public boolean from(Path s) {
        return s.startsWith(ROOT_DIR);
    }


    private String connectFilesToString(List<String> files) {
        String res = "";
        for (String file : files) {
            res += file + System.lineSeparator();
        }
        return res;
    }

    private List<String> siftedList(List<String> basicFiles, List<String> filesToDelete) {
        return basicFiles.stream()
                .filter(x -> !filesToDelete.contains(x))
                .collect(Collectors.toList());
    }
}
