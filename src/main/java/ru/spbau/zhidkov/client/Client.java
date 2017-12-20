package ru.spbau.zhidkov.client;


import ru.spbau.zhidkov.IO.FileSystem;
import ru.spbau.zhidkov.IO.Reader;
import ru.spbau.zhidkov.IO.Writer;
import ru.spbau.zhidkov.utils.FilesList;
import ru.spbau.zhidkov.utils.Query;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.Map;

/** Class for client structure */
@SuppressWarnings("WeakerAccess")
public class Client {

    private SocketChannel socketChannel;
    private FileSystem fileSystem;

    private String hostname;
    private int port;

    private Client(FileSystem fileSystem, String hostname, int port) {
        this.fileSystem = fileSystem;
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Builds client with given data
     *
     * @param hostname host name
     * @param port port
     * @param basicFolder basic folder for <tt>FileSystem</tt>
     * @return built client
     */
    public static Client buildClient(String hostname, int port, Path basicFolder) {
        return new Client(new FileSystem(basicFolder), hostname, port);
    }

    /**
     * Connects to the server
     *
     * @throws IOException in case of errors in IO operations
     */
    public void connect() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(hostname, port));
        while (!socketChannel.finishConnect()) {
        }
    }

    public boolean isConnected() {
        return socketChannel.isConnected();
    }

    /**
     * Performs get query
     *
     * @param path query path parameter
     * @param pathToSave path to save get result
     * @throws IOException in case of errors in IO operations
     */
    public void executeGet(Path path, Path pathToSave) throws IOException {
        sendQuery(new Query(Query.QueryType.GET, path));
        readAnsFromServer(fileSystem.getBufferedOutputStream(pathToSave));
    }

    /**
     * Performs list query
     *
     * @param path query path parameter
     * @return Map of files in path on server. If Value
     * is equal to {@code true} then key is folder.
     * @throws IOException in case of errors in IO operations
     */
    public Map<Path, FilesList.FileType> executeList(Path path) throws IOException {
        sendQuery(new Query(Query.QueryType.LIST, path));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        readAnsFromServer(outputStream);
        FilesList filesList = FilesList.formByteArray(outputStream.toByteArray());
        return filesList.getFiles();
    }

    /**
     * Disconnects from server
     *
     * @throws IOException in case of errors in IO operations
     */
    public void disconnect() throws IOException {
        socketChannel.close();
    }

    private void sendQuery(Query query) throws IOException {
        byte[] bytesToWrite = query.toByteArray();
        Writer fileWriter = new Writer(socketChannel, bytesToWrite.length, new ByteArrayInputStream(bytesToWrite));
        while (!fileWriter.write()) {

        }
    }

    private void readAnsFromServer(OutputStream outputStream) throws IOException {
        Reader reader = new Reader(socketChannel, outputStream);
        while (!reader.read()) {

        }
        reader.closeStream();
    }

}