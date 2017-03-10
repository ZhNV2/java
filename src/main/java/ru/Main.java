package ru;

import ru.spbau.VcsLight;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Main {

    public enum Command {
        init, add, commit;
    }

    public static void main(String[] args) {
        VcsLight vcs = new VcsLight();
        if (args.length == 0) {
            // TODO: ...
        }
        String command = args[0];
        if (command.equals(Command.init.toString())) {
            try {
                vcs.init(args[1]);
            } catch (IOException e) {
                //  TODO: ...
            }
        } else if (command.equals(Command.add.toString())) {
            List<String> fileNames = new ArrayList<>();
            Collections.addAll(fileNames, args);
            fileNames.remove(command);
            try {
                vcs.add(fileNames);
            } catch (IOException e) {
                // TODO: ...
            }
        } else if (command.equals(Command.commit.toString())) {

        }

    }
}
