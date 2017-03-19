package ru;

import com.beust.jcommander.JCommander;
import ru.spbau.Vcs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;

public class Main {

    private static String HELP = System.getProperty("line.separator") + "Use --help for more information about available commands";

    public enum CommandName {
        init(new JCommanderParser.CommandInit()),
        add(new JCommanderParser.CommandAdd()),
        commit(new JCommanderParser.CommandCommit()),
        log(new JCommanderParser.CommandLog()),
        checkout(new JCommanderParser.CommandCheckout()),
        branch(new JCommanderParser.CommandBranch()),
        merge(new JCommanderParser.CommandMerge());

        private Command command;

        CommandName(Command command) {
            this.command = command;
        }

        public Command getCommand() {
            return command;
        }
    }

    public static void main(String[] args) throws IOException {

        JCommanderParser cm = new JCommanderParser();
        JCommander jc = new JCommander(cm);

        for (CommandName commandName : CommandName.values()) {
            Command command = commandName.getCommand();
            jc.addCommand(commandName.toString(), command);
        }

        try {
            jc.parse(args);
            if (cm.help) {
                jc.usage();
            } else {
                Vcs.saveWorkingCopy();
                CommandName.valueOf(jc.getParsedCommand()).getCommand().run();
                Vcs.clearWorkingCopy();
            }
        } catch (FileAlreadyExistsException e) {
            System.out.println("You have already initialized repository in the current folder");
        } catch (FileNotFoundException | NoSuchFileException e) {
            System.out.println("File not found: " + e.getMessage() + "." + System.getProperty("line.separator") +
                    "Probably you have deleted this file, " +
                    "specified wrong one or forgot to init the repository");
            baseErrorHandling();
        } catch (AccessDeniedException e) {
            System.out.println("Access to file " + e.getMessage() + " is forbidden. Check your permission rights");
            baseErrorHandling();
        } catch (Exception e) {
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            baseErrorHandling();
        }
    }

    private static void baseErrorHandling() throws IOException {
        System.out.println(HELP);
        Vcs.restoreWorkingCopy();
    }
}
