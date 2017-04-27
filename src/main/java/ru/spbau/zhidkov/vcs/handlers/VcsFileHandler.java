package ru.spbau.zhidkov.vcs.handlers;

import org.jetbrains.annotations.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.spbau.zhidkov.vcs.commands.Vcs;
import ru.spbau.zhidkov.vcs.file.FileSystem;
import ru.spbau.zhidkov.vcs.vcsObjects.VcsBlob;
import ru.spbau.zhidkov.vcs.vcsObjects.VcsCommit;
import ru.spbau.zhidkov.vcs.vcsObjects.VcsObjectHandler;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**Handler for operations with vcs inner file system */
@SuppressWarnings("WeakerAccess")
public class VcsFileHandler {

    private static final Logger logger = LogManager.getLogger(VcsFileHandler.class);

    private FileSystem fileSystem;
    private VcsObjectHandler vcsObjectHandler;

    private final Path ROOT_DIR;
    private final Path OBJECTS_DIR;
    private final Path BRANCHES_DIR;
    private final Path PATH_ADD_LIST;
    private final Path PATH_RM_LIST;
    private final Path ONE_LINE_VARS_DIR;
    private final Path HEAD;
    private final String MASTER;
    private final Path AUTHOR_NAME;

    /**
     * Builds instance with provided <tt>FileSystem</tt>
     * and <tt>VcsObjectHandler</tt>
     *
     * @param fileSystem       file system
     * @param vcsObjectHandler vcs object handler
     */
    public VcsFileHandler(FileSystem fileSystem, VcsObjectHandler vcsObjectHandler) {
        this.fileSystem = fileSystem;
        this.vcsObjectHandler = vcsObjectHandler;
        ROOT_DIR = Paths.get(".vcs");
        OBJECTS_DIR = ROOT_DIR.resolve("objects");
        BRANCHES_DIR = ROOT_DIR.resolve("branches");
        PATH_ADD_LIST = ROOT_DIR.resolve("addList");
        PATH_RM_LIST = ROOT_DIR.resolve("rmList");
        ONE_LINE_VARS_DIR = ROOT_DIR.resolve("one_lines_vars");
        HEAD = ONE_LINE_VARS_DIR.resolve("HEAD");
        MASTER = "master";
        AUTHOR_NAME = ONE_LINE_VARS_DIR.resolve("AUTHOR_NAME");
    }

    /**
     * Checks if specified commit exists
     *
     * @param commitHash hash of commit to check
     * @return whether commit exists
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public boolean commitExists(String commitHash) throws IOException {
        return fileSystem.exists(OBJECTS_DIR.resolve(commitHash));
    }

    /**
     * Checks if provided branch exists
     *
     * @param branchName branch to check
     * @return whether provided branch exists
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public boolean branchExists(String branchName) throws IOException {
        return fileSystem.exists(BRANCHES_DIR.resolve(branchName));
    }

    /**
     * Sets branch pointer to specified commit
     *
     * @param branchName branch to change pointer
     * @param commitHash hash of new branch commit
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void setBranchCommit(String branchName, String commitHash) throws IOException {
        fileSystem.writeStringToFile(BRANCHES_DIR.resolve(branchName), commitHash);
    }

    /**
     * Returns branch last commit hash
     *
     * @param branchName branch to get commit
     * @return last commit
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public
    @NotNull
    VcsCommit getBranchCommit(String branchName) throws IOException {
        return getCommit(fileSystem.getFirstLine(BRANCHES_DIR.resolve(branchName)));
    }

    /**
     * Returns HEAD branch name
     *
     * @return HEAD name
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public String getHeadBranch() throws IOException {
        return fileSystem.getFirstLine(HEAD);
    }

    /**
     * Deletes branch
     *
     * @param branchName branch to delete
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void deleteBranch(String branchName) throws IOException {
        fileSystem.deleteIfExists(BRANCHES_DIR.resolve(branchName));
    }

    /**
     * Sets HEAD branch
     *
     * @param headBranch new HEAD
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void setHeadBranch(String headBranch) throws IOException {
        fileSystem.writeStringToFile(HEAD, headBranch);
    }

    /**enum for two storing lists: add and rm */
    public enum ListWithFiles {
        ADD_LIST, RM_LIST
    }

    private Path getListEnum(ListWithFiles listWithFiles) {
        switch (listWithFiles) {
            case ADD_LIST:
                return PATH_ADD_LIST;
            case RM_LIST:
                return PATH_RM_LIST;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Removes files from list
     *
     * @param listWithFiles list to delete files from
     * @param files         files to delete
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void removeFromList(ListWithFiles listWithFiles, List<Path> files) throws IOException {
        fileSystem.writeStringToFile(getListEnum(listWithFiles),
                connectPathsToString(siftedList(fileSystem.readAllLines(getListEnum(listWithFiles))
                        .stream()
                        .map(Paths::get)
                        .collect(Collectors.toList()), files)));
    }

    /**
     * Adds new files to list
     *
     * @param listWithFiles list to add files
     * @param files         files to add
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void addToList(ListWithFiles listWithFiles, List<Path> files) throws IOException {
        fileSystem.appendToFile(getListEnum(listWithFiles), connectPathsToString(files).getBytes());
    }

    /**
     * Returns all files been storing in list.
     *
     * @param listWithFiles list to read files
     * @return files to read
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public
    @NotNull
    List<Path> getList(ListWithFiles listWithFiles) throws IOException {
        return fileSystem
                .readAllLines(getListEnum(listWithFiles))
                .stream()
                .map(Paths::get)
                .collect(Collectors.toList());
    }

    /**
     * Checks if list is empty. Throws an exception if so
     *
     * @param listWithFiles list to check
     * @throws IOException                    if something has gone wrong during
     *                                        the work with file system
     * @throws Vcs.VcsIncorrectUsageException is list is not empty
     */
    public void assertListEmpty(ListWithFiles listWithFiles) throws IOException, Vcs.VcsIncorrectUsageException {
        if (!fileSystem.getFirstLine(getListEnum(listWithFiles)).equals("")) {
            logger.error("uncommitted changes");
            throw new Vcs.VcsIncorrectUsageException("You have several files were added/removed, " +
                    "but haven't committed yet");
        }
    }

    /**
     * Clears list
     *
     * @param listWithFiles list to clear
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void clearList(ListWithFiles listWithFiles) throws IOException {
        fileSystem.writeStringToFile(getListEnum(listWithFiles), "");
    }


    /**
     * Return repo author name
     *
     * @return author name
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public String getAuthorName() throws IOException {
        return fileSystem.getFirstLine(AUTHOR_NAME);
    }

    /**
     * Writes blob to file system
     *
     * @param blob blob to write
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void writeBlob(VcsBlob blob) throws IOException {
        blob.writeAsJson(OBJECTS_DIR.resolve(blob.getHash()));
    }

    /**
     * Writes commit to file system
     *
     * @param commit commit to write
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void writeCommit(VcsCommit commit) throws IOException {
        commit.writeAsJson(OBJECTS_DIR.resolve(commit.getHash()));
    }

    /**
     * Initialize vcs file system with author name
     *
     * @param authorName author name
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public void init(String authorName) throws IOException {
        fileSystem.createDirectory(ROOT_DIR);
        fileSystem.createDirectory(OBJECTS_DIR);
        fileSystem.createDirectory(BRANCHES_DIR);
        fileSystem.createDirectory(ONE_LINE_VARS_DIR);
        fileSystem.createEmptyFile(PATH_ADD_LIST);
        fileSystem.createEmptyFile(PATH_RM_LIST);
        fileSystem.createEmptyFile(HEAD);
        fileSystem.createEmptyFile(AUTHOR_NAME);
        fileSystem.writeStringToFile(AUTHOR_NAME, authorName);
        fileSystem.writeStringToFile(HEAD, MASTER);
    }

    /**
     * Checks if repo already exists
     *
     * @return whether repo exists
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public boolean repoExists() throws IOException {
        return fileSystem.exists(ROOT_DIR) && fileSystem.exists(HEAD);
    }


    /**
     * Returns blob by hash
     *
     * @param blobHash blob hash
     * @return built blob
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public
    @NotNull
    VcsBlob getBlob(String blobHash) throws IOException {
        return (VcsBlob) vcsObjectHandler.readFromJson(OBJECTS_DIR.resolve(blobHash), VcsBlob.class);
    }

    /**
     * Builds blob from file content
     *
     * @param file to build blob
     * @return built blob
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public
    @NotNull
    VcsBlob buildBlob(Path file) throws IOException {
        return vcsObjectHandler.buildBlob(file);
    }

    /**
     * Returns commit by its hash
     *
     * @param commitHash commit hash
     * @return built commit
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public
    @NotNull
    VcsCommit getCommit(String commitHash) throws IOException {
        return (VcsCommit) vcsObjectHandler.readFromJson(OBJECTS_DIR.resolve(commitHash), VcsCommit.class);
    }

    /**
     * Builds commit with provided args
     *
     * @param message        commit message
     * @param date           commit date
     * @param author         commit author
     * @param prevCommitHash commit prev commit hash
     * @param childrenAdd    <tt>Map</tt> of commit files were added
     * @param childrenRm     <tt>List</tt> of commit files were removed
     * @return build commit
     */
    public
    @NotNull
    VcsCommit buildCommit(String message, Date date, String author, String prevCommitHash,
                          Map<Path, String> childrenAdd, List<Path> childrenRm) {
        return vcsObjectHandler.buildCommit(message, date, author, prevCommitHash, childrenAdd, childrenRm);
    }

    /**
     * Checks if <tt>Path</tt> connected with vcs file system
     *
     * @param path to check
     * @return whether path connected with vcs system
     */
    public boolean from(Path path) {
        return path.startsWith(ROOT_DIR);
    }

    private String connectPathsToString(@NotNull List<Path> files) {
        String res = "";
        for (Path file : files) {
            res += file + System.lineSeparator();
        }
        return res;
    }

    private
    @NotNull
    List<Path> siftedList(@NotNull List<Path> basicFiles, @NotNull List<Path> filesToDelete) {
        return basicFiles.stream()
                .filter(((Predicate<Path>) filesToDelete::contains).negate())
                .collect(Collectors.toList());
    }
}
