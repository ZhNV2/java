import org.junit.Test;
import ru.spbau.Function2;
import ru.spbau.zhidkov.Function1;

import static org.junit.Assert.*;

/**
 * Created by Nikolay on 23.10.16.
 */
public class Function2Test {

    @Test
    public void testCompose() {


        Function2<Integer, Integer, Integer> comp = TestConstants.sum.compose(TestConstants.mul3);
        assertEquals(comp.apply(3, 4), Integer.valueOf(21));
        assertEquals(comp.apply(0, -1), Integer.valueOf(-3));

        Function2<String, String, Integer> comp2 = TestConstants.concat.compose(TestConstants.hashCode);
        String a = "Kolya";
        String b = "Olya";
        assertEquals(comp2.apply(a, b), Integer.valueOf((a + b).hashCode()));
        assertEquals(comp2.apply(b, a), Integer.valueOf((b + a).hashCode()));
    }

    @Test
    public void testBind1() {
        Function1<Integer, Integer> bind1 = TestConstants.sum.bind1(3);
        assertEquals(bind1.apply(4), Integer.valueOf(7));

        Function1<String, String> bind1_2 = TestConstants.concat.bind1("Nikolay");
        assertEquals(bind1_2.apply("Kolya"), "NikolayKolya");
    }

    @Test
    public void testBind2() {
        Function1<Integer, Integer> bind2 = TestConstants.sum.bind2(3);
        assertEquals(bind2.apply(4), Integer.valueOf(7));

        Function1<String, String> bind2_2 = TestConstants.concat.bind2("Nikolay");
        assertEquals(bind2_2.apply("Kolya"), "KolyaNikolay");
    }

    @Test
    public void testCurry() {
        Function1<Integer, Function1<Integer, Integer>> curry = TestConstants.sum.curry();
        assertEquals(curry.apply(2).apply(3), Integer.valueOf(5));
        assertEquals(curry.apply(4).apply(-4), Integer.valueOf(0));

        Function1<String, Function1<String, String>> curry_2 = TestConstants.concat.curry();
        assertEquals(curry_2.apply("edu").apply("domoy"), "edudomoy");
        assertEquals(curry_2.apply("domoy").apply("edu"), "domoyedu");
    }

}
