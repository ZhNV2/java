import org.junit.Before;
import org.junit.Test;
import ru.spbau.SmartTrie;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by Nikolay on 02.10.16.
 */

public class TypicalCasesTests {

    private SmartTrie trie = null;

    @Before
    public void initTrie() {
        trie = new SmartTrie();
    }

    @Test
    public void typicalCasesTests0() {
        // just add-contains-remove

        final ArrayList<String> inWords = new ArrayList<String>();
        final ArrayList<String> outWords = new ArrayList<String>();

        inWords.add("a");
        inWords.add("ab");
        inWords.add("baba");
        inWords.add("bab");
        inWords.add("aa");

        outWords.add("b");
        outWords.add("bb");
        outWords.add("aaa");
        outWords.add("ba");
        outWords.add("baa");
        outWords.add("aab");
        outWords.add("babab");
        outWords.add("ababa");

        for (String word : inWords) {
            assertTrue(trie.add(word));
        }
        for (String word : inWords) {
            assertTrue(trie.contains(word));
            assertFalse(trie.add(word));
        }
        for (String word : outWords) {
            assertFalse(trie.contains(word));
        }
        for (String word : outWords) {
            assertFalse(trie.remove(word));
        }
        for (String word : inWords) {
            assertTrue(trie.remove(word));
        }
    }

    @Test
    public void typicalCasesTests1() {
        // add-contains-size-remove-size-contains
        final ArrayList<String> words = new ArrayList<String>();

        words.add("h");
        words.add("hg");
        words.add("ghg");
        words.add("gghg");
        words.add("hhh");
        words.add("gg");

        assertEquals(0, trie.size());
        for (String word : words) {
            assertTrue(trie.add(word));
            assertFalse(trie.add(word));
        }
        assertEquals(words.size(), trie.size());
        for (String word : words) {
            assertTrue(trie.contains(word));
        }
        for (String word : words) {
            assertTrue(trie.remove(word));
            assertFalse(trie.remove(word));
        }
        assertEquals(0, trie.size());
    }

    @Test
    public void typicalCasesTests2() {
        //add-contains-prefix-remove-size-prefix
        final ArrayList<String> words = new ArrayList<String>();

        words.add("ZPPZ");
        words.add("ZPP");
        words.add("PP");
        words.add("P");
        words.add("PZ");
        words.add("PPZ");

        for (String word : words) {
            assertTrue(trie.add(word));
            assertFalse(trie.add(word));
        }

        assertEquals(4, trie.howManyStartsWithPrefix("P"));
        assertEquals(0, trie.howManyStartsWithPrefix("p"));
        assertEquals(2, trie.howManyStartsWithPrefix("ZP"));
        assertEquals(2, trie.howManyStartsWithPrefix("PP"));
        assertEquals(0, trie.howManyStartsWithPrefix("ZZ"));
        assertEquals(1, trie.howManyStartsWithPrefix("PPZ"));

        for (String word : words) {
            assertTrue(trie.remove(word));
            assertFalse(trie.remove(word));
        }

        assertEquals(0, trie.size());
        assertEquals(0, trie.howManyStartsWithPrefix("P"));
        assertEquals(0, trie.howManyStartsWithPrefix("ZP"));
    }
}
