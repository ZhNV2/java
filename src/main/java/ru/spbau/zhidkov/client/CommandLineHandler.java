package ru.spbau.zhidkov.client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import ru.spbau.zhidkov.utils.FilesList;
import ru.spbau.zhidkov.utils.Query;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/** Class for handling command line client work cycle */
public class CommandLineHandler {

    private final static String HELP = "use help for more information";
    private final static String USAGE = "use \"help\" for help\n" +
            "use \"stop\" to finish work session\n" +
            "use \"list dir\" to observe list of files in directory\n" +
            "use \"get file\" to download file in the same place as file you're gonna download lies";

    private enum Command {
        LIST, GET, HELP, STOP;
    }

    /**
     * Executes query been provided with arguments from command line and
     * client to communicate with server
     *
     * @param client to communicate with server
     * @param args of command line
     * @return if it is necessary to finish current terminal session
     */
    public boolean execute(Client client, String[] args) {
        try {
            if (args.length == 1 && args[0].toUpperCase().equals(Command.STOP.toString())) {
                return true;
            }
            if (args.length == 1 && args[0].toUpperCase().equals(Command.HELP.toString())) {
                System.out.println(USAGE);
                return false;
            }
            if (args.length == 2 && args[0].toUpperCase().equals(Command.LIST.toString())) {
                Map<Path, FilesList.FileType> map = client.executeList(Paths.get(args[1]));
                for (Map.Entry<Path, FilesList.FileType> entry : map.entrySet()) {
                    System.out.println(entry.getKey().toString() + "  " + entry.getValue().toString());
                }
                return false;
            }
            if (args.length == 2 && args[0].toUpperCase().equals(Command.GET.toString())) {
                client.executeGet(Paths.get(args[1]), Paths.get(args[1]));
                return false;
            }
            throw new ParameterException("invalid command");
        } catch (ParameterException e) {
            logError("An error occurred during parsing:", e);
            return false;
        } catch (Exception e) {
            logError("An error occurred during communication to the server:", e);
            try {
                client.disconnect();
                client.connect();
            } catch (Exception e1) {
                System.out.println("can't reconnect to server");
                return true;
            }
            return false;
        }
    }

    private void logError(String message, Exception e) {
        System.out.println(message);
        System.out.println(e.getMessage());
        System.out.println(HELP);
    }
}
