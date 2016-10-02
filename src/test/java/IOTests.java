import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.spbau.SmartTrie;

import java.io.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Nikolay on 02.10.16.
 */
public class IOTests {
    SmartTrie trie = null;
    ArrayList<String> words = null;

    @Before
    public void initTest() throws FileNotFoundException {
        trie = new SmartTrie();

        words = new ArrayList<String>();
        words.add("Kolusha");
        words.add("Kolya");
        words.add("Nikolay");
        words.add("Nikolasha");
        words.add("Nik");
        words.add("Kol");

    }

    private void printTrie() throws IOException {
        for (String word : words) {
            trie.add(word);
        }
        FileOutputStream out = new FileOutputStream("src/test/resources/in.txt");
        trie.serialize(out);
        out.close();
    }

    private void checkDeserializedTrie() throws IOException {
        FileInputStream in = new FileInputStream("src/test/resources/in.txt");
        trie.deserialize(in);
        assertEquals(words.size(), trie.size());
        for (String word : words) {
            assertTrue(trie.contains(word));
        }
        in.close();
    }

    @Test
    public void ioTests0() throws IOException {
        // build trie with words from file (deserialize)
        FileOutputStream out = new FileOutputStream("src/test/resources/in.txt");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        writer.write((Integer.toString(words.size()) + '\n'));
        for (String word : words) {
            writer.write(word + '\n');
        }
        writer.flush();
        out.close();

        checkDeserializedTrie();
    }

    @Test
    public void ioTests1() throws IOException {
        // print trie(serialize)
        printTrie();

        FileInputStream in = new FileInputStream("src/test/resources/in.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        int cntNewWords = Integer.decode(reader.readLine());
        assertEquals(words.size(), cntNewWords);
        for (int i = 0; i < cntNewWords; i++) {
            String word = reader.readLine();
            assertTrue(words.contains(word));
        }
        reader.close();
    }

    @Test
    public void ioTests2() throws IOException {
        // serialize + deserialize
        printTrie();

        for (String word : words) {
            trie.remove(word);
        }

        checkDeserializedTrie();
    }

}
