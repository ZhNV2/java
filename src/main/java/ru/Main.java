package ru;

import ru.spbau.Md5CheckSumMultiThread;
import ru.spbau.Md5CheckSumOneThread;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Нико on 30.03.2017.
 */
public class Main {

    public static void main(String[] args) {
        try {
            execute(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void execute(String[] args) throws IOException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Please specify file");
        }
        String fileName = args[0];
        List<Md5CheckSumCounterI> counters = new ArrayList<>();
        counters.add(new Md5CheckSumOneThread());
        counters.add(new Md5CheckSumMultiThread());
        for (Md5CheckSumCounterI counter : counters) {
            long start = System.currentTimeMillis();
            Files.write(Paths.get("res" + counter.getClassDesc()), counter.countCheckSum(fileName));
            long end = System.currentTimeMillis();
            System.out.println("Total time of " + counter.getClassDesc() + " is " + (end - start));
        }
    }
}
