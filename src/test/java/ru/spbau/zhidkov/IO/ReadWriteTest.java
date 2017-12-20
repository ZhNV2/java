package ru.spbau.zhidkov.IO;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import ru.spbau.zhidkov.server.Server;
import ru.spbau.zhidkov.utils.Query;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.min;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Class testing <tt>Reader</tt> and <tt>Writer</tt> implementation */
public class ReadWriteTest {

    @Test(timeout = 5000)
    public void QueryReaderTest() throws IOException {
        Path path = Paths.get("abcdesakldfjasdkfljasfdkjassafjfkasdljfalsfj");
        Query query = new Query(Query.QueryType.LIST, path);
        byte[] queryBytes = query.toByteArray();
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(8 + queryBytes.length);
        byte[] bytes = buffer.array();
        bytes = ArrayUtils.addAll(bytes, queryBytes);

        SocketChannel channel = mock(SocketChannel.class);
        setUpReadChannel(channel, bytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Reader reader = new Reader(channel, outputStream);
        while (true) {
            if (reader.read()) {
                break;
            }
        }
        Query query1 = Query.fromByteArray(((ByteArrayOutputStream) reader.getOutputStream()).toByteArray());
        reader.closeStream();
        assertEquals(query.getType(), query1.getType());
        assertEquals(query.getPath(), query1.getPath());
    }

    @Test(timeout = 5000)
    public void WriterTest() throws IOException {
        FileChannel channelToRead = mock(FileChannel.class);
        byte[] bytes = "dsalkfjadslfjas;lfjslfjasdl;fajs;lfj".getBytes();
        setUpReadChannel(channelToRead, bytes);
        SocketChannel socketChannel = mock(SocketChannel.class);
        List<Byte> byteList = new ArrayList<>();
        setUpWriteChannel(socketChannel, byteList);

        Writer writer = new Writer(socketChannel, bytes.length, new ByteArrayInputStream(bytes));
        while (true) {
            if (writer.write()) {
                break;
            }
        }
        writer.closeStream();
        assertEquals(bytes.length + Long.BYTES, byteList.size());
        for (int i = 0; i < bytes.length; i++) {
            assertTrue(bytes[i] == byteList.get(i + Long.BYTES));
        }
    }

    private int written;

    private void setUpReadChannel(ReadableByteChannel channel, byte[] bytes) throws IOException {
        written = 0;
        when(channel.read(any())).thenAnswer(invocation -> {
            ByteBuffer byteBuffer = (ByteBuffer) invocation.getArguments()[0];

            int left = bytes.length - written;
            Random random = new Random();
            int toWrite = random.nextInt(1 + min(left, 3));
            for (int i = 0; i < toWrite; i++) {
                byteBuffer.put(bytes[written++]);
            }
            return toWrite;
        });
    }

    private void setUpWriteChannel(WritableByteChannel channel, final List<Byte> bytes) throws IOException {
        when(channel.write(any())).thenAnswer(invocation -> {
            ByteBuffer byteBuffer = (ByteBuffer) invocation.getArguments()[0];
            int left = byteBuffer.remaining();
            Random random = new Random();
            int toRead = random.nextInt(1 + min(left, 3));
            for (int i = 0; i < toRead; i++) {
                bytes.add(byteBuffer.get());
            }
            return toRead;
        });
    }

}
