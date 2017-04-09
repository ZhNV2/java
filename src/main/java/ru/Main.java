package ru;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import ru.spbau.Vcs;
import ru.spbau.zhidkov.vcs.VcsCommit;
import ru.spbau.zhidkov.vcs.VcsObject;
import ru.spbau.zhidkov.vcs.file.FileSystem;
import ru.spbau.zhidkov.vcs.file.ObjectDeserializer;
import ru.spbau.zhidkov.vcs.file.ObjectSerializer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * Main class for all application for parsing command line arguments
 * and call appropriate method from vcs.
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * Enum containing all possible command line commands.
     */
    public enum CommandName {
        init, add, commit, log, checkout, branch, merge, reset, rm, clean, status;

        private Command command;

        public Command getCommand() {
            return command;
        }

        /**
         * Initializes enum elements with corresponding parser. It
         * should be called every time before you run {@link
         * #execute(Vcs, String[]) execute(Vcs, String[])}.
         */
        public static void initialize(JCommanderParser jcp) {
            init.command = jcp.new CommandInit();
            add.command = jcp.new CommandAdd();
            commit.command = jcp.new CommandCommit();
            log.command = jcp.new CommandLog();
            checkout.command = jcp.new CommandCheckout();
            branch.command = jcp.new CommandBranch();
            merge.command = jcp.new CommandMerge();
            reset.command = jcp.new CommandReset();
            rm.command = jcp.new CommandRemove();
            clean.command = jcp.new CommandClean();
            status.command = jcp.new CommandStatus();
        }
    }

    /**
     * Main application method. Parses args and calls appropriate
     * vcs methods.
     *
     * @param args command line args
     */
    public static void main(String[] args) {
        logger.info("Main has started with following args: {}", Arrays.stream(args).collect(Collectors.joining(" ")));
        Vcs vcs = new Vcs(Paths.get(System.getProperty("user.dir")));
        try  {
            execute(vcs, args);
        } catch (Vcs.VcsException | ParameterException e) {
            logger.error("vcsException or parameter exception has been caught in main");
            System.out.println(e.getMessage());
            baseErrorHandling(vcs);
        } catch (FileNotFoundException | NoSuchFileException e) {
            logger.error("file not found exception has been caught in main");
            System.out.println("File not found: " + e.getMessage() + "." + "\n" +
                    "Probably you have deleted this file, " +
                    "specified wrong one or forgot to init the repository");
            baseErrorHandling(vcs);
        } catch (AccessDeniedException e) {
            logger.error("access denied exception has been caught in main");
            System.out.println("Access to file " + e.getMessage() + " is forbidden. Check your permission rights");
            baseErrorHandling(vcs);
        } catch (Exception e) {
            logger.fatal("{} has been caught in main because of {}", e.getClass().getName(), e.getMessage());
            System.out.println("If you see this message, then something has gone completely wrong.");
            System.out.println("Please report this bug to the developers:");
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            e.printStackTrace();
            baseErrorHandling(vcs);
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
    public static void execute(@NotNull Vcs vcs, String[] args) throws IOException, Vcs.VcsException {
        JCommanderParser cm = new JCommanderParser(vcs);
        JCommander jc = new JCommander(cm);
        CommandName.initialize(cm);
        for (CommandName commandName : CommandName.values()) {
            Command command = commandName.getCommand();
            jc.addCommand(commandName.toString(), command);
        }
        vcs.saveWorkingCopy();
        jc.parse(args);
        if (cm.help) {
            jc.usage();
        } else {
            String command = jc.getParsedCommand();
            if (command == null) throw new ParameterException("You should specify the command");
            CommandName.valueOf(command).getCommand().run();
        }
        vcs.clearWorkingCopy();
    }

    private static void baseErrorHandling(@NotNull Vcs vcs) {
        logger.warn("base error handling has started");
        String HELP = "\n" + "Use --help for more information about available commands";
        System.out.println(HELP);
        try {
            vcs.restoreWorkingCopy();
        } catch (IOException e) {
            System.out.println("Can't restore working copy, because of:");
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
        }
    }



}
