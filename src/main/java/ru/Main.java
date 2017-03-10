package ru;

import ru.spbau.VcsLight;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    private static final String INIT_COMMAND = "init";
    private static final String ADD_COMMAND = "add";

    public static void main(String[] args) {
        System.out.println("HEH");

        VcsLight vcs = new VcsLight();
        if (args[0].equals(INIT_COMMAND)) {
            try {
                vcs.init();
            } catch (IOException e) {
                //  TODO: ...
            }
        } else if (args[0].equals(ADD_COMMAND)) {
            List<String> fileNames = new ArrayList<>();
            Collections.addAll(fileNames, args);
            fileNames.remove(ADD_COMMAND);
            try {
                vcs.add(fileNames);
            } catch (IOException e) {
                // TODO: ...
            }
        }

    }
}
