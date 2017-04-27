package ru.spbau.zhidkov.IO;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Class for write data from <tt>ReadableByteChannel</tt>
 * to <tt>WritableByteChannel</tt>
 */
public class Writer {

    private final static int BUFFER_SIZE = 2048;

    private ByteBuffer byteBuffer;
    private ReadableByteChannel channelToReadFrom;
    private WritableByteChannel writableByteChannel;
    private long bytesToWrite;
    private long bytesAlreadyWritten = 0;

    /**
     * Builds new <tt>Writer</tt> by given data
     *
     * @param channelToReadFrom channel to read information from
     * @param writableByteChannel channel to write in
     * @param bytesToWrite number of bytes should be received
     */
    public Writer(ReadableByteChannel channelToReadFrom, WritableByteChannel writableByteChannel, long bytesToWrite) {
        this.channelToReadFrom = channelToReadFrom;
        this.writableByteChannel = writableByteChannel;
        this.bytesToWrite = Long.BYTES + bytesToWrite;
        byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        byteBuffer.clear();
        byteBuffer.putLong(this.bytesToWrite);
        byteBuffer.flip();
    }

    /**
     * Performs write operation. If any of channels are not
     * ready for operation, returns {@code false}.
     *
     * @return whether write operation was complete
     * @throws IOException in case of errors in IO operations
     */
    public boolean write() throws IOException {
        while (true) {
            while (byteBuffer.hasRemaining()) {
                boolean written = writeToChannel();
                if (!written) {
                    return false;
                }
            }
            if (bytesAlreadyWritten == bytesToWrite) {
                finish();
                return true;
            }
            byteBuffer.clear();
            boolean read = readFromChannel();
            byteBuffer.flip();
            if (!read) {
                return false;
            }
        }


    }

    private boolean writeToChannel() throws IOException {
        int written = writableByteChannel.write(byteBuffer);
        if (written == -1) {
            throw new IOException("Channel has finished before all data were written");
        } else if (written == 0) {
            return false;
        } else {
            bytesAlreadyWritten += written;
        }
        return true;
    }

    private boolean readFromChannel() throws IOException {
        int read = channelToReadFrom.read(byteBuffer);
        if (read == -1) {
            throw new IOException("Channel has finished before all data were read");
        } else if (read == 0) {
            return false;
        }
        return true;
    }

    protected void finish() throws IOException {}
}
