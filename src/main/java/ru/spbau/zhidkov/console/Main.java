package ru.spbau.zhidkov.console;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.jetbrains.annotations.NotNull;
import ru.spbau.zhidkov.vcs.commands.Vcs;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main class for all application for parsing parserCommand line arguments
 * and call appropriate method from vcs.
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * Enum containing all possible parserCommand line commands.
     */
    public enum CommandName {
        init, add, commit, log, checkout, branch, merge, reset, rm, clean, status;

        private ParserCommand parserCommand;

        public ParserCommand getParserCommand() {
            return parserCommand;
        }

        /**
         * Initializes enum elements with corresponding parser. It
         * should be called every time before you run {@link
         * #execute(Vcs, String[]) execute(Vcs, String[])}.
         *
         * @param jcp base parser
         */
        public static void initialize(JCommanderParser jcp) {
            init.parserCommand = jcp.new ParserCommandInit();
            add.parserCommand = jcp.new ParserCommandAdd();
            commit.parserCommand = jcp.new ParserCommandCommit();
            log.parserCommand = jcp.new ParserCommandLog();
            checkout.parserCommand = jcp.new ParserCommandCheckout();
            branch.parserCommand = jcp.new ParserCommandBranch();
            merge.parserCommand = jcp.new ParserCommandMerge();
            reset.parserCommand = jcp.new ParserCommandReset();
            rm.parserCommand = jcp.new ParserCommandRemove();
            clean.parserCommand = jcp.new ParserCommandClean();
            status.parserCommand = jcp.new ParserCommandStatus();
        }
    }

    /**
     * Main application method. Parses args and calls appropriate
     * vcs methods.
     *
     * @param args parserCommand line args
     */
    public static void main(String[] args) {
        logger.info("Main has started with following args: {}", Arrays.stream(args).collect(Collectors.joining(" ")));
        Vcs vcs = new Vcs(Paths.get(System.getProperty("user.dir")));
        try {
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
     * @param vcs  vcs
     * @param args parserCommand line args to be parsed
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
            ParserCommand parserCommand = commandName.getParserCommand();
            jc.addCommand(commandName.toString(), parserCommand);
        }
        vcs.saveWorkingCopy();
        jc.parse(args);
        if (cm.help) {
            jc.usage();
        } else {
            String command = jc.getParsedCommand();
            if (command == null) {
                throw new ParameterException("You should specify the parserCommand");
            }
            CommandName.valueOf(command).getParserCommand().run();
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
