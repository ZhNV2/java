package ru.spbau.zhidkov.vcs;

import ru.spbau.zhidkov.vcs.file.FileSystem;
import ru.spbau.zhidkov.vcs.file.ObjectSerializer;

/**
 * Vcs object providing usual file (blob) structure.
 * It only contains {@code byte[]} array of all data
 * in presenting file.
 */
public class VcsBlob extends VcsObject {

    private byte[] content = null;

    public VcsBlob(FileSystem fileSystem, ObjectSerializer objectSerializer, byte[] content) {
        super(fileSystem, objectSerializer);
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }
}
