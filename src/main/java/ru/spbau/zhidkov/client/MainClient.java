package ru.spbau.zhidkov.client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import ru.spbau.zhidkov.utils.FilesList;
import ru.spbau.zhidkov.utils.MainParametersAbstract;
import ru.spbau.zhidkov.utils.Query;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;

/** Main class for client work */
public class MainClient extends MainParametersAbstract {

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
        } catch (ParameterException e) {
            System.out.println("An error occurred during the parsing:");
            logException(e);
        } catch (Exception e) {
            System.out.println("An error occurred during the connecting to the server:");
            logException(e);
        }
    }

    private void execute(JCommander jCommander) throws IOException {
        if (help) {
            jCommander.usage();
            return;
        }
        final Scanner scanner = new Scanner(System.in);
        final Client client = Client.buildClient(hostname, serverPort, Paths.get(System.getProperty("user.dir")));
        client.connect();
        while (true) {
            System.out.print("client>");
            CommandLineHandler commandLineHandler = new CommandLineHandler();
            String[] args = scanner.nextLine().split(" ");
            if (commandLineHandler.execute(client, args)) {
                break;
            }
        }
    }

}
