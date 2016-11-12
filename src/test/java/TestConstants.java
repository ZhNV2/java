import ru.spbau.Function2;
import ru.spbau.Predicate;
import ru.spbau.zhidkov.Function1;

/**
 * Created by Nikolay on 23.10.16.
 */
public class TestConstants {
    public static final Function1<Object, Integer> HASH_CODE = new Function1<Object, Integer>() {
        @Override
        public Integer apply(Object o) {
            return o.hashCode();
        }
    };

    public static final Function1<Integer, Integer> PLUS_2 = new Function1<Integer, Integer>() {
        @Override
        public Integer apply(Integer arg) {
            return arg + 2;
        }
    };

    public static final Function1<Integer, Integer> MUL_3 = new Function1<Integer, Integer>() {
        @Override
        public Integer apply(Integer arg) {
            return arg * 3;
        }
    };

    public static final Function2<Integer, Integer, Integer> SUM = new Function2<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer arg1, Integer arg2) {
            return arg1 + arg2;
        }
    };

    public static final Function2<String, String, String> CONCAT = new Function2<String, String, String>() {
        @Override
        public String apply(String arg1, String arg2) {
            return arg1 + arg2;
        }
    };

    public static final Predicate<Integer> IS_ODD = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer arg) {
            return arg % 2 == 1;
        }
    };

    public static final Predicate<Integer> IS_EVEN = new Predicate<Integer>() {
        @Override
        public Boolean apply(Integer arg) {
            return arg % 2 == 0;
        }
    };

    public static final Predicate<Object> IS_HASH_CODE_EVEN = new Predicate<Object>() {
        @Override
        public Boolean apply(Object arg) {
            return arg.hashCode() % 2 == 0;
        }
    };




}
