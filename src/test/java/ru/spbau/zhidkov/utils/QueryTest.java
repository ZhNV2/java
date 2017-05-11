package ru.spbau.zhidkov.utils;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/** Class testing (de)serialization of <tt>Query</tt> class */
public class QueryTest {

    @Test
    public void serializeDeserialize() throws IOException {
        Query query = new Query(Query.QueryType.GET, Paths.get("aabab"));
        Query query1 = Query.fromByteArray(query.toByteArray());
        assertEquals(query.getPath(), query1.getPath());
        assertEquals(query.getType(), query1.getType());
    }

    @Test
    public void deserializeSerialize() throws IOException {
        Query query = new Query(Query.QueryType.LIST, Paths.get("dsafkj"));
        byte[] bytes = query.toByteArray();
        byte[] bytes1 = Query.fromByteArray(bytes).toByteArray();
        assertArrayEquals(bytes, bytes1);
    }
}
