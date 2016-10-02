import org.junit.Before;
import org.junit.Test;
import ru.spbau.SmartTrie;

import static org.junit.Assert.*;

/**
 * Created by Nikolay on 02.10.16.
 */
public class SpecialCasesTests {
    SmartTrie trie = null;

    @Before
    public void initTrie() {
        trie = new SmartTrie();
    }

    @Test
    public void specialCasesTests0() {
        // delete word, but not prefix
        trie.add("ab");
        trie.add("abb");
        assertEquals(2, trie.howManyStartsWithPrefix("a"));
        assertEquals(2, trie.howManyStartsWithPrefix("ab"));
        assertEquals(1, trie.howManyStartsWithPrefix("abb"));
        trie.remove("abb");
        assertEquals(1, trie.howManyStartsWithPrefix("a"));
        assertEquals(1, trie.howManyStartsWithPrefix("ab"));
        assertEquals(0, trie.howManyStartsWithPrefix("abb"));
    }

    @Test
    public void specialCasesTests1() {
        // multiple add
        trie.add("test");
        assertEquals(1, trie.size());
        assertEquals(1, trie.howManyStartsWithPrefix("test"));
        trie.add("test");
        trie.add("test");
        assertEquals(1, trie.size());
        assertEquals(1, trie.howManyStartsWithPrefix("test"));
    }

    @Test
    public void specialCasesTests2() {
        // multiple delete
        trie.add("Nikolay");
        trie.remove("Nikolay");
        assertEquals(0, trie.size());
        assertEquals(0, trie.howManyStartsWithPrefix("Nik"));
        trie.remove("Nikolay");
        assertEquals(0, trie.size());
        assertEquals(0, trie.howManyStartsWithPrefix("Nik"));
    }

    @Test
    public void specialCasesTests3() {
        // add word, but not prefix

        trie.add("ZhNV");
        assertEquals(1, trie.howManyStartsWithPrefix("Z"));
        assertEquals(1, trie.howManyStartsWithPrefix("Zh"));
        assertEquals(1, trie.howManyStartsWithPrefix("ZhN"));
        assertEquals(1, trie.howManyStartsWithPrefix("ZhNV"));

        assertFalse(trie.contains("Z"));
        assertFalse(trie.contains("Zh"));
        assertFalse(trie.contains("ZhN"));
        assertTrue(trie.contains("ZhNV"));

    }

    @Test
    public void specialCasesTests4() {
        // multiple add, prefix amount
        trie.add("a");
        trie.add("a");
        trie.add("ab");
        trie.add("a");
        trie.add("ab");
        assertEquals(2, trie.howManyStartsWithPrefix("a"));
        assertEquals(1, trie.howManyStartsWithPrefix("ab"));
    }

    @Test
    public void specialCasesTests5() {
        // small changes in word
        assertTrue(trie.add("Ilya"));
        assertTrue(trie.add("Andrey"));

        assertFalse(trie.contains("Andrei"));
        assertFalse(trie.contains("Bndrey"));
        assertFalse(trie.contains("aAndrey"));
        assertFalse(trie.contains("Andreyt"));

        assertFalse(trie.contains("Ilyya"));
        assertFalse(trie.contains("Ilyb"));
        assertFalse(trie.contains("Illa"));
        assertFalse(trie.contains("lya"));
    }

    @Test
    public void specialCasesTests6() {
        // upper/lower case
        assertTrue(trie.add("Tanya"));
        assertTrue(trie.add("Nastya"));

        assertFalse(trie.contains("tanya"));
        assertFalse(trie.contains("tanYa"));
        assertFalse(trie.contains("tanyA"));
        assertFalse(trie.contains("TanyA"));

        assertFalse(trie.contains("nastyA"));
        assertFalse(trie.contains("nastya"));
        assertFalse(trie.contains("NASTYA"));
    }


}
