import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.HashMap;
import static junit.framework.Assert.*;


public class GeneralTest {
    private HashMap hashMap;

    @Before
    public void makeGeneralTest() {
        hashMap = new HashMap();
    }

    @Test
    public void test0() {

        hashMap.put("1", "one");
        hashMap.put("3", "three");

        String result = hashMap.get("3");
        assertEquals("in GeneralTest, test0 " + result + " != three", result, "three");

        result = hashMap.get("1");
        assertEquals("in GeneralTest, test0 " + result + " != one", result, "one");
    }

    @Test
    public void test1() {
        hashMap.put("1", "one");
        hashMap.put("1", "new one");
        String result = hashMap.get("1");
        assertEquals("in GeneralTest, test1 " + result + " != new one", result, "new one");
        int size = hashMap.size();
        assertEquals("in GeneralTest, test1 " + size + " != 1", size, 1);
    }

    @Test
    public void test2() {
        hashMap.put("1", "one");
        String result = hashMap.remove("1");
        assertEquals("in GeneralTest, test2 " + result + " != one", result, "one");
        int size = hashMap.size();
        assertEquals("in GeneralTest, test2 " + size + " != 0", size, 0);
    }

    @Test
    public void test3() {
        int cnt = 50;
        for (int i = 0; i < cnt; i++) {
            if (i % 2 == 0) hashMap.put(Integer.toString(i), Integer.toString(i));
        }
        for (int i = 0; i < cnt; i++) {
            boolean contains = hashMap.contains(Integer.toString(i));
            String result = hashMap.get(Integer.toString(i));
            if (i % 2 == 0) {
                assertTrue("in GeneralTest, test3 " + Integer.toString(i) + " isn't in map", contains);
                assertEquals("in GeneralTest, test3 " + result + " != " + Integer.toString(i), result, Integer.toString(i));
            } else {
                assertFalse("in GeneralTest, test3 " + Integer.toString(i) + " is in map", contains);
                assertNull("in GeneralTest, test3 " + result + " != null", result);
            }
        }
        int size = hashMap.size();
        assertEquals(size + " != 25", size, 25);
        for (int i = 0; i < cnt; i++) {
            String result = hashMap.remove(Integer.toString(i));
            if (i % 2 == 0) {
                assertEquals("in GeneralTest, test3 " + result + " != " + Integer.toString(i), result, Integer.toString(i));
            } else {
                assertNull("in GeneralTest, test3 " + result + " != null", result);
            }
        }
        size = hashMap.size();
        assertEquals("in GeneralTest, test3 " + size + " != 0", size, 0);
    }

    @After
    public void afterTest() {
        hashMap.clear();
    }
}

