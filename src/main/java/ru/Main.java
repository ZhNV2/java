package ru;

import ru.spbau.Vcs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Main {

    public enum Command {
        init, add, commit, log, checkout, branch, merge;
    }

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            // TODO: ...
        }
        String command = args[0];
        if (command.equals(Command.init.toString())) {

            Vcs.init(args[1]);

        } else if (command.equals(Command.add.toString())) {
            List<String> fileNames = new ArrayList<>();
            Collections.addAll(fileNames, args);
            fileNames.remove(command);
            Vcs.add(fileNames);

        } else if (command.equals(Command.commit.toString())) {
            Vcs.commit(args[1]);

        } else if (command.equals(Command.log.toString())) {
            System.out.println(Vcs.log().toString());
        } else if (command.equals(Command.checkout.toString())) {
            if (args[1].equals("-r")) {
                Vcs.checkout(args[2], null);
            } else if (args[1].equals("-b")) {
                Vcs.checkout(null, args[2]);
            } else {
                // TODO:
            }
        } else if (command.equals(Command.branch.toString())) {
            if (args[1].equals("-n")) {
                Vcs.createBranch(args[2]);
            } else if (args[1].equals("-r")) {
                Vcs.deleteBranch(args[2]);
            } else {
                // TODO:
            }
        } else if (command.equals(Command.merge.toString())) {
            Vcs.merge(args[1]);
        } else {
            // TODO:
        }
    }
}
