package ru.spbau.zhidkov.client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import ru.spbau.zhidkov.utils.MainParametersAbstract;
import ru.spbau.zhidkov.utils.Query;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/** Main class for client work */
public class MainClient extends MainParametersAbstract {

    @Parameter(names = "--query", required = true, description = "parameter \"get\" copies file from server, " +
            "\"list\" returns list of files in required path")
    private String query;

    @Parameter(names = "--path", required = true, description = "path to query file")
    private String path;

    /**
     * Starts client work
     *
     * @param args command line args
     */
    public static void main(String[] args) {
        try {
            MainClient mainClient = new MainClient();
            JCommander jCommander = new JCommander(mainClient, args);
            mainClient.execute(jCommander);
        } catch (Exception e) {
            logException(e);
        }
    }

    private void execute(JCommander jCommander) throws IOException {
        if (help) {
            jCommander.usage();
            return;
        }
        Client client = Client.buildClient(hostname, serverPort, Paths.get(System.getProperty("user.dir")));
        client.connect();

        if (query.equals(Query.QueryType.LIST.toString().toLowerCase())) {
            Map<Path, Boolean> map = client.executeList(Paths.get(path));
            for (Map.Entry<Path, Boolean> entry : map.entrySet()) {
                System.out.println(entry.getKey().toString() + "  " + entry.getValue());
            }
        } else if (query.equals(Query.QueryType.GET.toString().toLowerCase())) {
            client.executeGet(Paths.get(path));
        } else {
            client.disconnect();
            throw new ParameterException("query parameter should be either list or get");
        }
        client.disconnect();
    }

}
