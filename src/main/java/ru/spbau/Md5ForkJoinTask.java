package ru.spbau;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import static ru.spbau.Md5Counter.concat;
import static ru.spbau.Md5Counter.countMD5;

/**
 * Created by Нико on 30.03.2017.
 */
public class Md5ForkJoinTask extends RecursiveTask<byte[]> {
    private final String file;

    public Md5ForkJoinTask(String file) {
        this.file = file;
    }

    @Override
    protected byte[] compute() {
        List<Md5ForkJoinTask> subTasks = new ArrayList<>();

        if (!Files.isDirectory(Paths.get(file))) {
            try {
                return countMD5(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Problems with file " + file);
            }
        }

        File folder = new File(file);
        for (File subFile : folder.listFiles()) {
            Md5ForkJoinTask task = new Md5ForkJoinTask(subFile.getPath());
            task.fork();
            subTasks.add(task);
        }

        byte[] content = new byte[0];
        for (Md5ForkJoinTask task : subTasks) {
            content = concat(content, task.join());
        }

        return countMD5(new ByteArrayInputStream(content));
    }

}
