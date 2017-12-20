package ru.spbau.zhidkov.IO;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Class for writing data from <tt>InputStream</tt>
 * to <tt>WritableByteChannel</tt>
 */
public class Writer {

    private final static int BUFFER_SIZE = 2048;

    private final ByteBuffer byteBuffer;
    private final WritableByteChannel writableByteChannel;
    private final InputStream inputStream;

    private long cntBytesToWrite;
    private long bytesAlreadyWritten = 0;


    /**
     * Builds new <tt>Writer</tt> by given data
     *
     * @param writableByteChannel channel to write in
     * @param cntBytesToWrite number of bytes should be received
     * @param inputStream stream to get data from
     */
    public Writer(WritableByteChannel writableByteChannel, long cntBytesToWrite, InputStream inputStream) {
        this.writableByteChannel = writableByteChannel;
        this.cntBytesToWrite = Long.BYTES + cntBytesToWrite;
        this.inputStream = inputStream;
        byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        byteBuffer.clear();
        byteBuffer.putLong(this.cntBytesToWrite);
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
            if (bytesAlreadyWritten == cntBytesToWrite) {
                return true;
            }
            byteBuffer.clear();
            boolean read = readFromStream();
            byteBuffer.flip();
            if (!read) {
                return false;
            }
        }
    }

    private boolean writeToChannel() throws IOException {
        int written =  writableByteChannel.write(byteBuffer);
        if (written == -1) {
            throw new IOException("Channel has finished before all data were written");
        } else if (written == 0) {
            return false;
        } else {
            bytesAlreadyWritten += written;
        }
        return true;
    }

    private boolean readFromStream() throws IOException {
        int read = read();
        if (read == -1) {
            throw new IOException("Channel has finished before all data were read");
        } else if (read == 0) {
            return false;
        }
        return true;
    }

    private int read() throws IOException {
        int read = 0;
        while (true) {
            if (!byteBuffer.hasRemaining()) {
                return read;
            }
            int oneByte = inputStream.read();
            if (oneByte == -1) {
                return read == 0 ? -1 : read;
            }
            byteBuffer.put((byte) oneByte);
            read++;
        }
    }

    public void closeStream() throws IOException {
        inputStream.close();
    }
}
