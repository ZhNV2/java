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

    @Parameter(names = "--help", help = true, description = "help information")
    private boolean help;

    private final static String HELP = "use --help for more information";

    @Parameter(names = "--list", description = " returns list of files in required path")
    private String listDir;

    @Parameter(names = "--get", description = "copies file from server")
    private String getFile;

    @Parameter(names = "--stop", description = "finishes communication")
    private boolean stop = false;

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
            JCommander jCommander = new JCommander(this, args);

            if (help) {
                jCommander.usage();
                return false;
            }
            if (stop) {
                return true;
            }

            if (listDir != null && getFile != null) {
                throw new ParameterException("Specify one query");
            }
            if (listDir == null && getFile == null) {
                throw new ParameterException("you should provide list, get or stop instruction");
            }

            if (listDir != null) {
                Map<Path, FilesList.FileType> map = client.executeList(Paths.get(listDir));
                for (Map.Entry<Path, FilesList.FileType> entry : map.entrySet()) {
                    System.out.println(entry.getKey().toString() + "  " + entry.getValue().toString());
                }
                return false;
            } else {
                client.executeGet(Paths.get(getFile), Paths.get(getFile));
                return false;
            }
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
