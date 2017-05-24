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

    @Parameter(names = "--query", description = "parameter \"get\" copies file from server, " +
            "\"list\" returns list of files in required path")
    private String query;

    @Parameter(names = "--path", description = "path to query file")
    private String path;

    @Parameter(names = "--save", description = "path to save file in get query, " +
            "otherwise it's equal to path")
    private String pathToSave;

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

            if (query == null || path == null) {
                throw new ParameterException("you should provide query and path or call stop instruction");
            }

            if (query.equals(Query.QueryType.LIST.toString().toLowerCase())) {
                Map<Path, FilesList.FileType> map = client.executeList(Paths.get(path));
                for (Map.Entry<Path, FilesList.FileType> entry : map.entrySet()) {
                    System.out.println(entry.getKey().toString() + "  " + entry.getValue().toString());
                }
                return false;
            } else if (query.equals(Query.QueryType.GET.toString().toLowerCase())) {
                client.executeGet(Paths.get(path), Paths.get(pathToSave == null ? path : pathToSave));
                return false;
            } else {
                throw new ParameterException("query parameter should be either list or get");
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
