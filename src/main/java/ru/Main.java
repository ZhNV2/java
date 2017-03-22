package ru;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import ru.spbau.Vcs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;

import static ru.Main.CommandName.init;

/**
 * Main class for all application for parsing command line arguments
 * and call appropriate method from vcs.
 */
public class Main {

    private static String HELP = System.getProperty("line.separator") + "Use --help for more information about available commands";

    /**
     * Enum containing all possible command line commands.
     */
    public enum CommandName {
        init, add, commit, log, checkout, branch, merge;

        private Command command;

        public Command getCommand() {
            return command;
        }

        /**
         * Initializes enum elements with corresponding parser. It
         * should be called every time before you run {@link
         * #execute(String[]) execute(String[])}.
         */
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

    /**
     * Main application method. Parses args and calls appropriate
     * vcs methods.
     *
     * @param args command line args
     */
    public static void main(String[] args) {
        try {
            execute(args);
        } catch (Vcs.VcsException | ParameterException e) {
            System.out.println(e.getMessage());
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

    /**
     * Method that parses args and calls appropriate methods
     * in vcs.
     *
     * @param args command line args to be parsed
     * @throws IOException      if something has gone wrong during
     *                          the work with file system
     * @throws Vcs.VcsException if something has gone wrong
     *                          during the vcs work
     */
    public static void execute(String[] args) throws IOException, Vcs.VcsException {
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
            } else if (command.equals(init.toString()) && Vcs.hasInitialized()) {
                throw new ParameterException("You have already initialized repository in the current folder");
            } else if (!command.equals(init.toString()) && !Vcs.hasInitialized()) {
                throw new ParameterException("You have to initialize repository in the current folder");
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
