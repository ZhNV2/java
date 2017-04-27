package test.ru.spbau.zhidkov.vcs.file;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import ru.spbau.zhidkov.vcs.vcsObjects.VcsCommit;
import ru.spbau.zhidkov.vcs.file.FileSystem;
import ru.spbau.zhidkov.vcs.file.ObjectDeserializer;
import ru.spbau.zhidkov.vcs.file.ObjectSerializer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class SerializationTest {

    @Test
    public void serializeDeserializeCommitTest() throws IOException {
        Map<Path, String> add = ImmutableMap.of(Paths.get("a"), "a", Paths.get("B"), "b");
        List<Path> rm = Arrays.asList(Paths.get("b"), Paths.get("c"));
        VcsCommit vcsCommit = new VcsCommit(mock(FileSystem.class), mock(ObjectSerializer.class),
                "hello", new Date(), "1", "2", add, rm);
        VcsCommit newCommit = (VcsCommit) new ObjectDeserializer().deserialize(new ObjectSerializer().serialize(vcsCommit),
                VcsCommit.class);
        assertTrue(CollectionUtils.isEqualCollection(newCommit.getChildrenRm(), rm));
        assertTrue(CollectionUtils.isEqualCollection(newCommit.getChildrenAdd().entrySet(), add.entrySet()));
    }

    @Test
    public void deserializeSerializeCommitTest() throws IOException {
        String json = "{\"message\":\"hello\",\"date\":\"Apr 9, 2017 4:05:42 AM\",\"author\":\"1\"" +
                ",\"prevCommitHash\":\"2\",\"childrenAdd\":{\"a\":\"a\",\"B\":\"b\"},\"childrenRm\":\"b,c\"}";
        String newJson = new ObjectSerializer().serialize(new ObjectDeserializer().deserialize(json, VcsCommit.class));
        assertEquals(json, newJson);
    }

    @Test
    public void emptyRmDeserializeTest() throws IOException {
        String json = "{\"message\":\"hello\",\"date\":\"Apr 9, 2017 4:05:42 AM\",\"author\":\"1\",\"" +
                "prevCommitHash\":\"2\",\"childrenAdd\":{\"a\":\"a\",\"B\":\"b\"},\"childrenRm\":\"\"}";
        VcsCommit vcsCommit = (VcsCommit) new ObjectDeserializer().deserialize(json, VcsCommit.class);
        assertTrue(CollectionUtils.isEqualCollection(vcsCommit.getChildrenRm(), CollectionUtils.EMPTY_COLLECTION));
    }
}
