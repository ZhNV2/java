package ru.spbau.zhidkov.IO;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/** Class for reading data from <tt>ReadableByteChannel</tt> */
abstract public class Reader {

    private static final int BUFFER_SIZE = 2048;

    protected ByteBuffer byteBuffer;
    private ReadableByteChannel readableByteChannel;
    private long bytesToRead = -1;
    private long bytesAlreadyRead = 0;
    private boolean isBytesToReadSet = false;
    private boolean waitingForUpdate = false;

    public Reader(ReadableByteChannel readableByteChannel) {
        this.readableByteChannel = readableByteChannel;
        byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    }

    /**
     * Performs read operation. May return {@code false}
     * is something now is not ready for this operation (channel
     * for example).
     *
     * @return whether operation was complete or not
     * @throws IOException in case of errors in IO operations
     */
    public boolean read() throws IOException {
        while (true) {
            if (isBytesToReadSet) {
                if (waitingForUpdate) {
                    if (!update()) {
                        return false;
                    }
                    waitingForUpdate = false;
                }
                if (bytesToRead == bytesAlreadyRead) {
                    return true;
                }
                byteBuffer.clear();
                boolean okRead = readFromChannel();
                if (okRead) {
                    byteBuffer.flip();
                    waitingForUpdate = true;
                } else {
                    return false;
                }
            } else {
                while (bytesAlreadyRead < Long.BYTES) {
                    boolean okRead = readFromChannel();
                    if (!okRead) {
                        return false;
                    }
                }
                byteBuffer.flip();
                bytesToRead = byteBuffer.getLong();
                waitingForUpdate = true;
                isBytesToReadSet = true;
            }
        }

    }

    private boolean readFromChannel() throws IOException {
        int read = readableByteChannel.read(byteBuffer);
        if (read == -1) {
            throw new IOException("Channel has finished before all data were read");
        } else if (read == 0) {

            return false;
        } else {
            bytesAlreadyRead += read;
        }
        return true;
    }

    protected abstract boolean update() throws IOException;

}
