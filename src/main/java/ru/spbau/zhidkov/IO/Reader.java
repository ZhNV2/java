package ru.spbau.zhidkov.IO;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * Class for reading data from <tt>ReadableByteChannel</tt>
 * to <tt>OutputStream</tt>
 */
public class Reader {

    private static final int BUFFER_SIZE = 2048;

    private ByteBuffer byteBuffer;
    private ReadableByteChannel readableByteChannel;
    private long bytesToRead = -1;
    private long bytesAlreadyRead = 0;
    private boolean isBytesToReadSet = false;
    private boolean waitingForUpdate = false;
    private OutputStream outputStream;

    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Builds new <tt>Reader</tt> from giver params
     *
     * @param readableByteChannel channel to read from
     * @param outputStream stream to write to
     */
    public Reader(ReadableByteChannel readableByteChannel, OutputStream outputStream) {
        this.readableByteChannel = readableByteChannel;
        this.outputStream = outputStream;
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
                    write();
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

    private void write() throws IOException {
        while (byteBuffer.hasRemaining()) {
            outputStream.write(byteBuffer.get());
        }
    }

    public void closeStream() throws IOException {
        outputStream.close();
    }
}
