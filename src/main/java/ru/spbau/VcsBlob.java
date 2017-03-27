package ru.spbau;

/**
 * Vcs object providing usual file (blob) structure.
 * It only contains {@code byte[]} array of all data
 * in presenting file.
 */
public class VcsBlob extends VcsObject {

    private byte[] content = null;

    public VcsBlob(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }
}
