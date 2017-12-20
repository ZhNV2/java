import org.junit.Test;
import ru.spbau.Function2;
import ru.spbau.Predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static ru.Collections.*;

/**
 * Created by Nikolay on 24.10.16.
 */
public class CollectionsTest {

    private final List<Integer> integers = Arrays.asList(2,4,2,3,5,5,2,3,4,1,7,3,4,5,6);

    @Test
    public void testMap() {
        List<Integer> resMap = map(TestConstants.MUL_3, integers);
        List<Integer> res = integers.stream()
                                .map(o -> o * 3)
                                .collect(Collectors.toList());
        assertEquals(resMap, res);

        List<Integer> hashCodes = map(TestConstants.HASH_CODE, integers);
        List<Integer> realHashCodes = integers.stream()
                                        .map(Object::hashCode)
                                        .collect(Collectors.toList());
        assertEquals(hashCodes, realHashCodes);
    }

    @Test
    public void testFilter() {
        List<Integer> evens = filter(TestConstants.IS_EVEN, integers);
        List<Integer> realEvens = integers.stream()
                                    .filter(o -> o % 2 == 0)
                                    .collect(Collectors.toList());
        assertEquals(evens, realEvens);

        List<Integer> hashCodeEvens = filter(TestConstants.IS_HASH_CODE_EVEN, integers);
        List<Integer> realHashCodeEvens = integers.stream()
                                            .filter(o -> o.hashCode() % 2 == 0)
                                            .collect(Collectors.toList());
        assertEquals(hashCodeEvens, realHashCodeEvens);
    }

    @Test
    public void testTakeWhile() {
        List<Integer> take = takeWhile(TestConstants.IS_EVEN, integers);
        List<Integer> realTake = new ArrayList<Integer>();
        for (Integer elem : integers) {
            if (elem % 2 == 0) {
                realTake.add(elem);
            } else {
                System.out.println("e boy");
                break;
            }
        }
        assertEquals(take, realTake);

        List<Integer> take1 = takeWhile(new Predicate<Object>() {
            @Override
            public Boolean apply(Object o) {
                return o.hashCode() != 0;
            }
        }, integers);
        assertEquals(take1, integers);
    }

    @Test
    public void testTakeUnless() {
        List<Integer> take = takeUnless(new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer arg) {
                return arg > 6;
            }
        }, integers);
        List<Integer> realTake = new ArrayList<Integer>();
        for (Integer elem : integers) {
            if (elem <= 6) {
                realTake.add(elem);
            } else {
                break;
            }
        }
        assertEquals(take, realTake);

        List<Integer> take1 = takeUnless(new Predicate<Object>() {
            @Override
            public Boolean apply(Object o) {
                return o.hashCode() == 0;
            }
        }, integers);
        assertEquals(take1, integers);
    }

    @Test
    public void testFoldl() {
        Function2<String, Integer, String> concat = new Function2<String, Integer, String>() {
            @Override
            public String apply(String arg1, Integer arg2) {
                return arg1 + Integer.toString(arg2);
            }
        };
        String foldl = foldl(concat, "begin", integers);
        StringBuilder builder = new StringBuilder();
        builder.append("begin");
        for (Integer elem : integers) {
            builder.append(elem);
        }
        assertEquals(foldl, builder.toString());

        Function2<Integer, Object, Integer> subHashCode = new Function2<Integer, Object, Integer>() {
            @Override
            public Integer apply(Integer prev, Object o) {
                return prev - o.hashCode();
            }
        };
        Integer foldl1 = foldl(subHashCode, 71, integers);
        Integer res = 71;
        for (Integer x : integers) {
            res -= x;
        }
        assertEquals(foldl1, res);
    }

    @Test
    public void testFoldr() {
        Function2<Integer, String, String> concat = new Function2<Integer, String, String>() {
            @Override
            public String apply(Integer arg1, String arg2) {
                return Integer.toString(arg1) + arg2;
            }
        };
        String foldr = foldr(concat, "end", integers);
        StringBuilder builder = new StringBuilder();
        for (Integer elem : integers) {
            builder.append(elem);
        }
        builder.append("end");
        assertEquals(foldr, builder.toString());

        Function2<Object, Integer, Integer> subHashCode = new Function2<Object, Integer, Integer>() {
            @Override
            public Integer apply(Object o, Integer prev) {
                return o.hashCode() - prev;
            }
        };
        Integer foldr1 = foldr(subHashCode, 71, integers);
        Collections.reverse(integers);
        Integer res = 71;
        for (Integer x : integers) {
            res = x - res;
        }
        assertEquals(foldr1, res);
    }

}
