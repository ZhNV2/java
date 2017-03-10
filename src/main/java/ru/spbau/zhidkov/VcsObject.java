package ru.spbau.zhidkov;

import ru.Vcs;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public abstract class VcsObject {
    public byte[] getContent() {
        return content;
    }

    private byte[] content;

    VcsObject() {}

    VcsObject(byte[] content) {
        this.content = content;
    }

    public String getHash() {
        MessageDigest md =   null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            return byteArray2Hex(md.digest(content));
        } catch (NoSuchAlgorithmException e) {
            // TODO: smt?
            System.out.println("kEK");
            return null;
        }
    }

    private static String byteArray2Hex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public String getPath(String objectsDir) {
        return objectsDir + File.separator + getHash();
    }
}
