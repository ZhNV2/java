import org.junit.Test;
import ru.spbau.zhidkov.Function1;
import static org.junit.Assert.*;
/**
 * Created by Nikolay on 23.10.16.
 */
public class Function1Test {

    @Test
    public void testCompose() {

        Function1<Integer, Integer> compPlus2Mul3 = TestConstants.MUL_3.compose(TestConstants.PLUS_2);

        assertEquals(compPlus2Mul3.apply(1), Integer.valueOf(5));
        assertEquals(compPlus2Mul3.apply(0), Integer.valueOf(2));
        assertEquals(compPlus2Mul3.apply(-2), Integer.valueOf(-4));

        Function1<Integer, Integer> compMul3Plus2 = TestConstants.PLUS_2.compose(TestConstants.MUL_3);
        assertEquals(compMul3Plus2.apply(1), Integer.valueOf(9));
        assertEquals(compMul3Plus2.apply(0), Integer.valueOf(6));
        assertEquals(compMul3Plus2.apply(-2), Integer.valueOf(0));

        Function1<Object, Integer> compPlus2hashCode = TestConstants.HASH_CODE.compose(TestConstants.PLUS_2);
        Object o = "absd";
        assertEquals(compPlus2hashCode.apply(o), Integer.valueOf(o.hashCode() + 2));
    }

}
