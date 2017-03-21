package ru;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import ru.spbau.Vcs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;

public class Main {

    private static String HELP = System.getProperty("line.separator") + "Use --help for more information about available commands";

    public enum CommandName {
        init, add, commit, log, checkout, branch, merge;

        private Command command;

        public Command getCommand() {
            return command;
        }

        public static void initialize() {
            init.command = new JCommanderParser.CommandInit();
            add.command = new JCommanderParser.CommandAdd();
            commit.command = new JCommanderParser.CommandCommit();
            log.command = new JCommanderParser.CommandLog();
            checkout.command = new JCommanderParser.CommandCheckout();
            branch.command = new JCommanderParser.CommandBranch();
            merge.command = new JCommanderParser.CommandMerge();
        }
    }

    public static void main(String[] args) {

        try {
            execute(args);
        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            System.out.println(HELP);
        } catch (FileAlreadyExistsException e) {
            System.out.println("You have already initialized repository in the current folder");
            baseErrorHandling();
        } catch (FileNotFoundException | NoSuchFileException e) {
            System.out.println("File not found: " + e.getMessage() + "." + System.getProperty("line.separator") +
                    "Probably you have deleted this file, " +
                    "specified wrong one or forgot to init the repository");
            baseErrorHandling();
        } catch (AccessDeniedException e) {
            System.out.println("Access to file " + e.getMessage() + " is forbidden. Check your permission rights");
            baseErrorHandling();
        } catch (Exception e) {
            System.out.println("If you see this message, then something has gone completely wrong.");
            System.out.println("Please report this bug to the developers:");
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            baseErrorHandling();
        }
    }

    public static void execute(String[] args) throws IOException {
        JCommanderParser cm = new JCommanderParser();
        JCommander jc = new JCommander(cm);
        CommandName.initialize();
        for (CommandName commandName : CommandName.values()) {
            Command command = commandName.getCommand();
            jc.addCommand(commandName.toString(), command);
        }
        jc.parse(args);
        if (cm.help) {
            jc.usage();
        } else {
            Vcs.setCurrentFolder(System.getProperty("user.dir"));
            Vcs.saveWorkingCopy();
            String command = jc.getParsedCommand();
            if (command == null) {
                throw new ParameterException("You should specify the command");
            }
            CommandName.valueOf(command).getCommand().run();
            Vcs.clearWorkingCopy();
        }
    }

    private static void baseErrorHandling() {
        System.out.println(HELP);
        try {
            Vcs.restoreWorkingCopy();
        } catch (IOException e) {
            System.out.println("Can't restore working copy, because of:");
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
        }
    }


}
