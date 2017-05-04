package ru.spbau.zhidkov.server;

import org.apache.commons.lang3.ArrayUtils;
import ru.spbau.zhidkov.IO.FileSystem;
import ru.spbau.zhidkov.IO.Reader;
import ru.spbau.zhidkov.IO.Writer;
import ru.spbau.zhidkov.utils.FilesList;
import ru.spbau.zhidkov.utils.Query;

import java.io.IOException;
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
        fileSystem.rmTmpFiles();
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
        QueryReader queryReader = new QueryReader(channel);
        channel.register(key.selector(), SelectionKey.OP_READ, queryReader);
    }

    private void read(SelectionKey key) throws IOException {
        QueryReader queryReader = (QueryReader) key.attachment();
        if (!queryReader.read()) {
            return;
        }
        answerQuery(queryReader.getQuery(), key);
    }

    private void write(SelectionKey key) throws IOException {
        Writer writer = (Writer) key.attachment();
        writer.write();
    }

    private void answerQuery(Query query, SelectionKey key) throws IOException {
        if (!fileSystem.exists(query.getPath())) {
            sendEmptyFile(key);
            return;
        }
        switch (query.getType()) {
            case 1: {
                if (!fileSystem.isDir(query.getPath())) {
                    sendEmptyFile(key);
                }
                Path tmpFile = fileSystem.createTmpFile();
                getDirList(query).writeToFile(tmpFile, fileSystem);
                runWriter(tmpFile, key);
                break;
            }
            case 2: {
                runWriter(query.getPath(), key);
                break;
            }
            default: {
                sendEmptyFile(key);
            }
        }
    }


    private FilesList getDirList(Query query) throws IOException {
        Map<Path, Boolean> dirs = new HashMap<>();
        List<Path> paths = fileSystem.list(query.getPath()).collect(Collectors.toList());
        for (Path path : paths) {
            if (!fileSystem.isTmpFile(path)) {
                dirs.put(path, fileSystem.isDir(path));
            }
        }
        return new FilesList(dirs);
    }

    private void runWriter(Path path, SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        FileChannel fileChannel = fileSystem.inputChannelOf(path);
        Writer fileWriter = new Writer(fileChannel, channel, fileSystem.sizeOf(path)) {
            @Override
            public void finish() throws IOException {
                fileChannel.close();
                key.cancel();
                fileSystem.rmTmpFiles();
            }
        };
        channel.register(key.selector(), SelectionKey.OP_WRITE, fileWriter);
    }

    private void sendEmptyFile(SelectionKey key) throws IOException {
        Path path = fileSystem.createTmpFile();
        runWriter(path, key);
    }

    /** Class for reading query from <tt>ReadableByteChannel</tt>*/
    public static class QueryReader extends Reader {

        private byte[] bytes = new byte[0];

        public QueryReader(ReadableByteChannel readableByteChannel) {
            super(readableByteChannel);
        }

        /**
         * Action that should be performed after something
         * was read in <tt>ByteBuffer</tt>
         *
         * @return if update was successful or not
         * @throws IOException in case of errors in IO operations
         */
        @Override
        protected boolean update() throws IOException {
            while (byteBuffer.hasRemaining()) {
                byte[] tmp = new byte[byteBuffer.remaining()];
                byteBuffer.get(tmp);
                bytes = ArrayUtils.addAll(bytes, tmp);
            }
            return true;
        }

        /**
         * Returns query that was read from channel.
         * Should be called only after {@link #read() Reader::read()}
         * returns {@code true}
         *
         * @return read query
         * @throws IOException in case of errors in IO operations
         */
        public Query getQuery() throws IOException {
            return Query.buildQuery(bytes);
        }
    }
}
