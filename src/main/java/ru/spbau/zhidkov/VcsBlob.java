package ru.spbau.zhidkov;

public class VcsBlob extends VcsObject {

    private byte[] content = null;

    public VcsBlob(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }
}
