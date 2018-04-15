package ru.spbau;

import ru.Md5CheckSumCounterI;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static ru.spbau.Md5Counter.concat;
import static ru.spbau.Md5Counter.countMD5;


/**
 * Created by Нико on 30.03.2017.
 */
public class Md5CheckSumOneThread implements Md5CheckSumCounterI {


    public byte[] countCheckSum(String file) {
        if (Files.isDirectory(Paths.get(file))) {
            File folder = new File(file);
            byte[] content = new byte[0];
            for (File subFile : folder.listFiles()) {
                byte[] resCheckSum = countCheckSum(subFile.getPath());
                content = concat(content, resCheckSum);
            }
            return countMD5(new ByteArrayInputStream(content));
        } else {
            try {
                return countMD5(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Problems with file " + file);
            }
        }
    }

    public String getClassDesc() {
        return CLASS_DESC;
    }

    private static final String CLASS_DESC = "One thread implementation";
}
