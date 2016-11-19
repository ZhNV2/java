package sp;

import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static sp.SecondPartTasks.*;

public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() throws IOException {

        List<String> paths = Arrays.asList("src/test/resources/a.txt",
                                            "src/test/resources/b.txt",
                                            "src/test/resources/c.txt",
                                            "src/test/resources/d.txt"
        );
        FileWriter fileWriter = new FileWriter(paths.get(0));
        fileWriter.write("abacaba");
        fileWriter.close();

        fileWriter = new FileWriter(paths.get(2));
        fileWriter.write("bada");
        fileWriter.close();

        fileWriter = new FileWriter(paths.get(3));
        fileWriter.write("te\nfdsbacd\ndf");
        fileWriter.close();

        assertEquals(
                Arrays.asList(paths.get(0), paths.get(3)),
                findQuotes(paths, "bac"));
    }

    @Test
    public void testPiDividedBy4() {
        assertEquals(
                Math.PI / 4,
                piDividedBy4(),
                1e-3);
    }

    @Test
    public void testFindPrinter() {
        Map<String, List<String> > compositions = new HashMap<>();
        compositions.put("me", null);
        compositions.put("Pushkin", Arrays.asList("a", "b", "c"));
        compositions.put("Tolstoy", Arrays.asList("sdjflkjasdfklkajsdflsjdf", "dasfasdfsadfasdfadsfjsdafjasd"));
        compositions.put("Dontsova", Arrays.asList("asdfadsfdsafadsf", "djaflsjdfalksjdfadskjfkadsjf;lasdjf;alsdfjas;dlfjsdlajfasld;fjadsl;fjasd;lfjadsl;fjasd;fj"));
        assertEquals(
                "Dontsova",
                findPrinter(compositions));
    }

    @Test
    public void testCalculateGlobalOrder() {
        Map<String, Integer> a = new HashMap<String, Integer>();
        a.put("a", null);
        a.put("b", 0);
        a.put("c", 1);
        a.put("d", 2);

        Map<String, Integer> b = new HashMap<String, Integer>();
        b.put("c", 10);
        b.put("d", null);

        Map<String, Integer> c = new HashMap<String, Integer>();
        c.put("a", 11);
        c.put("d", 22);

        Map<String, Integer> res = new HashMap<String, Integer>();
        res.put("a", 11);
        res.put("b", 0);
        res.put("c", 11);
        res.put("d", 24);

        assertEquals(
                res,
                calculateGlobalOrder(Arrays.asList(a, b, c)));

    }
}