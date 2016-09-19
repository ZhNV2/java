import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static junit.framework.Assert.*;
import ru.spbau.LinkedList;


public class LinkedListTest {
    private LinkedList list;

    @Before
    public void makeList() {
        list = new LinkedList();
    }

    @Test
    public void test0() {
        list.put("1", "one");
        list.put("2", "two");
        String result1 = list.get("1");
        String result2 = list.get("2");
        String result3 = list.get("3");
        assertEquals("in LinkedListTest, test0 " + result1 + " != one", result1, "one");
        assertEquals("in LinkedListTest, test0 " + result2 + " != two", result2, "two");
        assertNull("in LinkedListTest, test0 " + result3 + "!= null", result3);
    }

    @Test
    public void test1() {
        list.put("1", "one");
        list.put("2", "two");
        list.put("3", "three");
        String result2 = list.remove("2");
        String result3 = list.remove("3");
        String result1 = list.remove("1");
        String result0 = list.remove("0");
        assertEquals("in LinkedListTest, test1 " + result2 + " != two", result2, "two");
        assertEquals("in LinkedListTest, test1 " + result3 + " != three", result3, "three");
        assertEquals("in LinkedListTest, test1 " + result1 + " != one", result1, "one");
        assertNull("in LinkedListTest, test1 " + result0 + " != null", result0);
    }

    @Test
    public void test2() {
        int cnt = 25;
        for (int i = 0; i < cnt; i++) {
            if (i % 3 == 0) list.put(Integer.toString(i), Integer.toString(i));
        }
        for (int i = 0; i < cnt; i++) {
            String number = Integer.toString(i);
            boolean contains = list.contains(number);
            String result = list.get(number);
            if (i % 3 == 0) {
                assertEquals("in LinkedListTest, test2 " + result + " != " + number, result, number);
                assertTrue("in LinkedListTest, test2 contains is false", contains);
                String prevValue = list.put(number, Integer.toString(i + 1));
                assertEquals("in LinkedListTest, test2 " + prevValue + " != " + number, prevValue, number);
            } else {
                assertNull("in LinkedListTest, test2 " + result + " != null", result);
                assertFalse("in LinkedListTest, test2 contains is true", contains);
            }
        }
        for (int i = cnt - 1; i >= 0; i--) {
            String number = Integer.toString(i);
            String nextNumber = Integer.toString(i + 1);
            String result = list.remove(number);
            if (i % 3 == 0) {
                assertEquals("in LinkedListTest, test2 " + result + " != " + nextNumber, result, nextNumber);
            } else {
                assertNull("in LinkedListTest, test2 " + result + " != null", result);
            }
        }
    }
    @After
    public void afterTest() {
        list.clear();
    }

}

