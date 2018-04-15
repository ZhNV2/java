package ru.spbau;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Нико on 30.03.2017.
 */
public class Md5Counter {

    private static final int BUFFER_SIZE = 4096;

    public static byte[] countMD5(InputStream content)  {
        byte[] result = new byte[0];
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            try (DigestInputStream stream = new DigestInputStream(content, md)) {
                while (stream.read(buffer) != -1) {
                    result = concat(result, buffer);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException("Error during work with file system");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Your java doesn't provide necessary algorithm");
        }
        return result;
    }

    public static byte[] concat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
