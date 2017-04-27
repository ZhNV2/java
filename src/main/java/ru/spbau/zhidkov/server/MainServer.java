package ru.spbau.zhidkov.server;

import com.beust.jcommander.JCommander;
import ru.spbau.zhidkov.utils.MainParametersAbstract;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

/** Main class for server */
public class MainServer extends MainParametersAbstract {

    private final static String STOP_COMMAND = "stop";

    /**
     * Starts server work
     *
     * @param args command line args
     */
    public static void main(String[] args) {
        try {
            MainServer mainServer = new MainServer();
            JCommander jCommander = new JCommander(mainServer, args);
            mainServer.execute(jCommander);
        } catch (Exception e) {
            logException(e);
        }
    }

    private void execute(JCommander jCommander) throws IOException {
        if (help) {
            jCommander.usage();
            return;
        }

        System.out.println("server has started, type \"stop\" to finish it");
        Server server = Server.buildServer(hostname, serverPort, Paths.get(System.getProperty("user.dir")));
        new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                logException(e);
            }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("server>");
            String text = scanner.next();
            if (text.equals(STOP_COMMAND)) {
                server.stop();
                break;
            }
        }
        scanner.close();
    }

}
