package ru.spbau.zhidkov.vcs.vcsObjects;

import org.jetbrains.annotations.NotNull;
import ru.spbau.zhidkov.vcs.file.FileSystem;
import ru.spbau.zhidkov.vcs.file.ObjectDeserializer;
import ru.spbau.zhidkov.vcs.file.ObjectSerializer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**Class encapsulating work with vcs objects */
public class VcsObjectHandler {

    private FileSystem fileSystem;
    private ObjectDeserializer objectDeserializer;
    private ObjectSerializer objectSerializer;

    /**
     * Builds class with provided <tt>FileSystem</tt>,
     * <tt>ObjectDeserializer</tt> and
     * <tt>ObjectSerializer</tt>
     *
     * @param fileSystem         file system
     * @param objectDeserializer object deserializer
     * @param objectSerializer   object serializer
     */
    public VcsObjectHandler(FileSystem fileSystem, ObjectDeserializer objectDeserializer,
                            ObjectSerializer objectSerializer) {
        this.fileSystem = fileSystem;
        this.objectDeserializer = objectDeserializer;
        this.objectSerializer = objectSerializer;
    }

    /**
     * Builds commit with mandatory params.
     *
     * @param message        commit message
     * @param date           commit date
     * @param author         commit author
     * @param prevCommitHash commit prev commit hash
     * @param childrenAdd    commit <tt>Map</tt> of new files
     * @param childrenRm     commit <tt>List</tt> of removed
     *                       files
     * @return built commit
     */
    public
    @NotNull
    VcsCommit buildCommit(String message, Date date, String author,
                          String prevCommitHash, Map<Path, String> childrenAdd, List<Path> childrenRm) {
        return new VcsCommit(fileSystem, objectSerializer, message, date, author, prevCommitHash, childrenAdd, childrenRm);
    }

    /**
     * Builds blob from file
     *
     * @param file to build blob from
     * @return built blob
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public
    @NotNull
    VcsBlob buildBlob(Path file) throws IOException {
        return new VcsBlob(fileSystem, objectSerializer, fileSystem.readAllBytes(file));
    }

    /**
     * Reads vcs object from file
     *
     * @param fileName       file to read obj from
     * @param vcsObjectClass object class
     * @return built object
     * @throws IOException if something has gone wrong during
     *                     the work with file system
     */
    public
    @NotNull
    VcsObject readFromJson(Path fileName, Class<? extends VcsObject> vcsObjectClass) throws IOException {
        String content = fileSystem.readAllLines(fileName).get(0);
        VcsObject vcsObject = vcsObjectClass.cast(objectDeserializer.deserialize(content, vcsObjectClass));
        vcsObject.setFileSystem(fileSystem);
        vcsObject.setObjectSerializer(objectSerializer);
        return vcsObject;
    }
}
