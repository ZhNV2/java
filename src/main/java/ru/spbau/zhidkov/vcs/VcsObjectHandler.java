package ru.spbau.zhidkov.vcs;

import com.sun.istack.internal.NotNull;
import ru.spbau.zhidkov.vcs.file.FileSystem;
import ru.spbau.zhidkov.vcs.file.ObjectDeserializer;
import ru.spbau.zhidkov.vcs.file.ObjectSerializer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Нико on 08.04.2017.
 */
public class VcsObjectHandler {

    private FileSystem fileSystem;
    private ObjectDeserializer objectDeserializer;
    private ObjectSerializer objectSerializer;

    public VcsObjectHandler(FileSystem fileSystem, ObjectDeserializer objectDeserializer, ObjectSerializer objectSerializer) {
        this.fileSystem = fileSystem;
        this.objectDeserializer = objectDeserializer;
        this.objectSerializer = objectSerializer;
    }


    public @NotNull VcsCommit buildCommit(String message, Date date, String author, String prevCommitHash, Map<Path, String> childrenAdd, List<Path> childrenRm) {
        return new VcsCommit(fileSystem, objectSerializer, message, date, author, prevCommitHash, childrenAdd, childrenRm);
    }

    public @NotNull VcsBlob buildBlob(Path file) throws IOException {
        return new VcsBlob(fileSystem, objectSerializer, fileSystem.readAllBytes(file));
    }

    public @NotNull VcsObject readFromJson(Path fileName, Class<? extends VcsObject> vcsObjectClass) throws IOException {
        String content = fileSystem.readAllLines(fileName).get(0);
        VcsObject vcsObject = vcsObjectClass.cast(objectDeserializer.deserialize(content, vcsObjectClass));
        vcsObject.setFileSystem(fileSystem);
        vcsObject.setObjectSerializer(objectSerializer);
        return vcsObject;
    }
}
