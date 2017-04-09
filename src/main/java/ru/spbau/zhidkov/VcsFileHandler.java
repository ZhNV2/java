package ru.spbau.zhidkov;

import org.jetbrains.annotations.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.ResetCommand;
import ru.spbau.Vcs;
import ru.spbau.zhidkov.vcs.*;
import ru.spbau.zhidkov.vcs.file.FileSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Нико on 30.03.2017.
 */
public class VcsFileHandler {
    private static final Logger logger = LogManager.getLogger(VcsFileHandler.class);

    private FileSystem fileSystem;
    private VcsObjectHandler vcsObjectHandler;
    
    public VcsFileHandler(FileSystem fileSystem, VcsObjectHandler vcsObjectHandler) {
        this.fileSystem = fileSystem;
        this.vcsObjectHandler = vcsObjectHandler;
        ROOT_DIR = Paths.get(".vcs");
        OBJECTS_DIR = ROOT_DIR.resolve("objects");
        BRANCHES_DIR = ROOT_DIR.resolve("branches");
        ADD_LIST = ROOT_DIR.resolve("addList");
        RM_LIST = ROOT_DIR.resolve("rmList");
        ONE_LINE_VARS_DIR = ROOT_DIR.resolve("one_lines_vars");
        HEAD = ONE_LINE_VARS_DIR.resolve("HEAD");
        MASTER = "master";
        AUTHOR_NAME = ONE_LINE_VARS_DIR.resolve("AUTHOR_NAME");
    }

    private final Path ROOT_DIR;
    private final Path OBJECTS_DIR;
    private final Path BRANCHES_DIR;
    private final Path ADD_LIST;
    private final Path RM_LIST;
    private final Path ONE_LINE_VARS_DIR;
    private final Path HEAD;
    private final String MASTER;
    private final Path AUTHOR_NAME;

    public boolean commitExists(String commitHash) throws IOException {
        return fileSystem.exists(OBJECTS_DIR.resolve(commitHash));
    }

    public boolean branchExists(String branchName) throws IOException {
        return fileSystem.exists(BRANCHES_DIR.resolve(branchName));
        
    }

    public void setBranchCommit(String branchName, String commitHash) throws IOException {
        fileSystem.writeStringToFile(BRANCHES_DIR.resolve(branchName), commitHash);
    }

    public VcsCommit getBranchCommit(String branchName) throws IOException {
        return getCommit(fileSystem.getFirstLine(BRANCHES_DIR.resolve(branchName)));
    }

    public String getHeadBranch() throws IOException {
        return fileSystem.getFirstLine(HEAD);
    }

    public void deleteBranch(String branchName) throws IOException {
        fileSystem.deleteIfExists(BRANCHES_DIR.resolve(branchName));
    }

    public void setHeadBranch(String headBranch) throws IOException {
        fileSystem.writeStringToFile(HEAD, headBranch);
    }

    public enum ListWithFiles {
        ADD_LIST, RM_LIST;
    }

    private Path getListEnum(ListWithFiles listWithFiles) {
        switch (listWithFiles) {
            case ADD_LIST: return ADD_LIST;
            case RM_LIST: return RM_LIST;
        }
        throw new IllegalArgumentException();
    }
    
    public void removeFromList(ListWithFiles listWithFiles, List<Path> files) throws IOException {
        fileSystem.writeStringToFile(getListEnum(listWithFiles),
                connectPathsToString(siftedList(fileSystem.readAllLines(getListEnum(listWithFiles))
                        .stream()
                        .map(Paths::get)
                        .collect(Collectors.toList()), files)));
    }

    public void addToList(ListWithFiles listWithFiles, List<Path> files) throws IOException {
        fileSystem.appendToFile(getListEnum(listWithFiles), connectPathsToString(files).getBytes());
    }

    public @NotNull List<Path> getList(ListWithFiles listWithFiles) throws IOException {
        return fileSystem.readAllLines(getListEnum(listWithFiles)).stream().map(Paths::get).collect(Collectors.toList());
    }

    public void assertListEmpty(ListWithFiles listWithFiles) throws IOException, Vcs.VcsIncorrectUsageException {
        if (!fileSystem.getFirstLine(getListEnum(listWithFiles)).equals("")) {
            logger.error("uncommitted changes");
            throw new Vcs.VcsIncorrectUsageException("You have several files were added/removed, but haven't committed yet");
        }
    }

    public void clearList(ListWithFiles listWithFiles) throws IOException {
        fileSystem.writeStringToFile(getListEnum(listWithFiles), "");
    }




    public String getAuthorName() throws IOException {
        return fileSystem.getFirstLine(AUTHOR_NAME);
    }

    public void writeBlob(VcsBlob blob) throws IOException {
        blob.writeAsJson(OBJECTS_DIR.resolve(blob.getHash()));
    }

    public void writeCommit(VcsCommit commit) throws IOException {
        commit.writeAsJson(OBJECTS_DIR.resolve(commit.getHash()));
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
        return fileSystem.exists(ROOT_DIR) && fileSystem.exists(HEAD);
    }


    public VcsBlob getBlob(String blobHash) throws IOException {
        return (VcsBlob) vcsObjectHandler.readFromJson(OBJECTS_DIR.resolve(blobHash), VcsBlob.class);
    }

    public VcsBlob buildBlob(Path file) throws IOException {
        return vcsObjectHandler.buildBlob(file);
    }




    public VcsCommit getCommit(String commitHash) throws IOException {
        return (VcsCommit) vcsObjectHandler.readFromJson(OBJECTS_DIR.resolve(commitHash), VcsCommit.class);
    }

    public VcsCommit buildCommit(String message, Date date, String author, String prevCommitHash, Map<Path, String> childrenAdd, List<Path> childrenRm) {
        return vcsObjectHandler.buildCommit(message, date, author, prevCommitHash, childrenAdd, childrenRm);
    }


    public boolean from(Path s) {
        return s.startsWith(ROOT_DIR);
    }


    private String connectPathsToString(List<Path> files) {
        String res = "";
        for (Path file : files) {
            res += file + System.lineSeparator();
        }
        return res;
    }

    private List<Path> siftedList(List<Path> basicFiles, List<Path> filesToDelete) {
        return basicFiles.stream()
                .filter(((Predicate<Path>) filesToDelete::contains).negate())
                .collect(Collectors.toList());
    }
}
