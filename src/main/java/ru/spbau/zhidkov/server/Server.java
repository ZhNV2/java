package ru.spbau.zhidkov.server;

import ru.spbau.zhidkov.IO.FileSystem;
import ru.spbau.zhidkov.IO.Reader;
import ru.spbau.zhidkov.IO.Writer;
import ru.spbau.zhidkov.utils.FilesList;
import ru.spbau.zhidkov.utils.Query;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/** Class providing server functionality */
@SuppressWarnings("WeakerAccess")
public class Server {

    private InetSocketAddress serverAddress;
    private FileSystem fileSystem;
    private boolean shouldStop = false;
    private Selector selector;

    private Server(String hostname, int port, FileSystem fileSystem) {
        serverAddress = new InetSocketAddress(hostname, port);
        this.fileSystem = fileSystem;
    }


    /**
     * Starts server cycle
     * @throws IOException in case of errors in IO operations
     */
    public void start() throws IOException {

        selector = Selector.open();

        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(serverAddress);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (!shouldStop) {
            selector.selectNow();
            Iterator keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = (SelectionKey) keys.next();
                keys.remove();
                if (!key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()) {
                    accept(key);
                } else if (key.isReadable()) {
                    read(key);
                } else if (key.isWritable()) {
                    write(key);
                }
            }
        }
        serverChannel.socket().close();
    }

    /**
     * Builds server from given data
     *
     * @param hostname host name
     * @param serverPort port
     * @param basicFolder folder with which server is
     *                    going to work
     * @return <tt>Server</tt> instance
     */
    public static Server buildServer(String hostname, int serverPort, Path basicFolder) {
        return new Server(hostname, serverPort, new FileSystem(basicFolder));
    }

    /**
     * Stops server
     *
     * @throws IOException in case of errors in IO operations
     */
    public void stop() throws IOException {
        for (SelectionKey selectionKey : selector.keys()) {
            selectionKey.cancel();
        }
        shouldStop = true;
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        Reader reader = new Reader(channel, new ByteArrayOutputStream());
        channel.register(key.selector(), SelectionKey.OP_READ, reader);
    }

    private void read(SelectionKey key) throws IOException {
        if (key.attachment() == null) {
            return;
        }
        Reader reader = (Reader) key.attachment();
        if (!reader.read()) {
            return;
        }
        Query query = Query.fromByteArray(((ByteArrayOutputStream) reader.getOutputStream()).toByteArray());
        answerQuery(query, key);
    }

    private void write(SelectionKey key) throws IOException {
        Writer writer = (Writer) key.attachment();
        if (!writer.write()) {
            return;
        }
        writer.closeStream();
        SocketChannel channel = (SocketChannel) key.channel();
        Reader reader = new Reader(channel, new ByteArrayOutputStream());
        channel.register(key.selector(), SelectionKey.OP_READ, reader);
    }

    private void answerQuery(Query query, SelectionKey key) throws IOException {
        if (!fileSystem.exists(query.getPath())) {
            sendEmptyAnswer(key);
            return;
        }
        switch (query.getType()) {
            case LIST: {
                if (!fileSystem.isDir(query.getPath())) {
                    sendEmptyAnswer(key);
                }
                sendFilesList(getFileList(query), key);
                break;
            }
            case GET: {
                sendFile(query.getPath(), key);
                break;
            }
            default: {
                sendEmptyAnswer(key);
            }
        }
    }

    private void sendFilesList(FilesList filesList, SelectionKey key) throws IOException {
        byte[] byteFileList = filesList.toByteArray();
        runWriter(byteFileList.length, new ByteArrayInputStream(byteFileList), key);
    }

    private void sendFile(Path path, SelectionKey key) throws IOException {
        runWriter(fileSystem.sizeOf(path), fileSystem.getBufferedInputStream(path), key);
    }

    private FilesList getFileList(Query query) throws IOException {
        Map<Path, FilesList.FileType> dirs = new HashMap<>();
        List<Path> paths = fileSystem.list(query.getPath()).collect(Collectors.toList());
        for (Path path : paths) {
            dirs.put(path, fileSystem.isDir(path) ? FilesList.FileType.FOLDER : FilesList.FileType.FILE);
        }
        return new FilesList(dirs);
    }

    private void runWriter(long size, InputStream inputStream, SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        channel.register(key.selector(), SelectionKey.OP_WRITE, new Writer(channel, size, inputStream));
    }

    private void sendEmptyAnswer(SelectionKey key) throws IOException {
        sendFilesList(new FilesList(new HashMap<>()), key);
    }


}
