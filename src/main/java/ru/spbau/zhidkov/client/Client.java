package ru.spbau.zhidkov.client;


import ru.spbau.zhidkov.IO.FileSystem;
import ru.spbau.zhidkov.IO.Reader;
import ru.spbau.zhidkov.IO.Writer;
import ru.spbau.zhidkov.utils.FilesList;
import ru.spbau.zhidkov.utils.Query;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
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
        while (!socketChannel.finishConnect());
    }

    /**
     * Performs get query
     *
     * @param path query path parameter
     * @param pathToSave path to save get result
     * @throws IOException in case of errors in IO operations
     */
    public void executeGet(Path path, Path pathToSave) throws IOException {
        sendQuery(new Query(Query.QueryType.GET.getTypeNumber(), path));
        getFileFromServer(pathToSave, FileSystem.outputChannelOfInner(pathToSave));
    }

    /**
     * Performs list query
     *
     * @param path query path parameter
     * @return Map of files in path on server. If Value
     * is equal to {@code true} then key is folder.
     * @throws IOException in case of errors in IO operations
     */
    public Map<Path, Boolean> executeList(Path path) throws IOException {
        sendQuery(new Query(Query.QueryType.LIST.getTypeNumber(), path));
        System.out.println("sent");
        Path tmpFile = fileSystem.createTmpFile();
        System.out.println("tmp");
        getFileFromServer(tmpFile, fileSystem.outputChannelOf(tmpFile));
        System.out.println("got");
        FilesList filesList = FilesList.buildFromFile(tmpFile, fileSystem);
        return filesList.getFiles();
    }

    /**
     * Disconnects from server
     *
     * @throws IOException in case of errors in IO operations
     */
    public void disconnect() throws IOException {
        socketChannel.close();
        fileSystem.rmTmpFiles();
    }

    private void sendQuery(Query query) throws IOException {
        Path tmpFilePath = fileSystem.createTmpFile();
        fileSystem.write(tmpFilePath, query.toByteArray());

        ReadableByteChannel fileChannel = fileSystem.inputChannelOf(tmpFilePath);
        Writer fileWriter = new Writer(fileChannel, socketChannel, fileSystem.sizeOf(tmpFilePath));
        while (!fileWriter.write()) ;
        fileChannel.close();
    }

    private void getFileFromServer(Path path, FileChannel fileChannel) throws IOException {
        System.out.println(path);
        FileReader fileReader = new FileReader(socketChannel, fileChannel);
        while (!fileReader.read()) ;
        System.out.println(path + " closed");
        fileChannel.close();

    }

    /**
     * Class for reading data from <tt>ReadableByteChannel</tt>
     * to <tt>FileChannel</tt>
     */
    private class FileReader extends Reader {

        private FileChannel fileChannel;

        public FileReader(ReadableByteChannel readableByteChannel, FileChannel fileChannel) {
            super(readableByteChannel);
            this.fileChannel = fileChannel;
        }

        /**
         * Operation should be performed after <tt>ByteBuffer</tt> got
         * updated.
         *
         * @return whether update was complete or not
         * @throws IOException in case of errors in IO operations
         */
        @Override
        protected boolean update() throws IOException {
            while (byteBuffer.hasRemaining()) {
                long written = fileChannel.write(byteBuffer);
                if (written == 0) {
                    return false;
                }
            }
            return true;
        }
    }

}