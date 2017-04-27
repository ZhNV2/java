package ru.spbau.zhidkov.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

/* Class presenting query structure */
public class Query {

    private int type;
    private Path path;

    /* Enum of possible types of query */
    public enum QueryType {
        LIST(1), GET(2);

        QueryType(int typeNumber) {
            this.typeNumber = typeNumber;
        }

        public int getTypeNumber() {
            return typeNumber;
        }

        int typeNumber;
    }

    public Query(int type, Path path) {
        this.type = type;
        this.path = path;
    }

    /**
     * Returns type of query. 1 for list, 2 for get
     *
     * @return type of query
     */
    public int getType() {
        return type;
    }

    /**
     * Returns path of query
     *
     * @return path of query
     */
    public Path getPath() {
        return path;
    }

    /**
     * Builds query from byte array
     *
     * @param bytes to build query from
     * @return built query
     * @throws IOException in case of errors in IO operations
     */
    public static Query buildQuery(byte[] bytes) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bytes));
        int type = dataInputStream.readInt();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
        Path path = Paths.get(bufferedReader.readLine());
        bufferedReader.close();
        return new Query(type, path);
    }

    /**
     * Convert query to byte array
     *
     * @return byte array presenting query
     * @throws IOException in case of errors in IO operations
     */
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(4 + path.toString().getBytes().length);
        byteArrayOutputStream.write(ByteBuffer.allocate(4).putInt(type).array());
        byteArrayOutputStream.write(path.toString().getBytes());
        return byteArrayOutputStream.toByteArray();
    }
}
