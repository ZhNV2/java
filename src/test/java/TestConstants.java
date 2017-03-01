import java.util.function.Supplier;


/**
 * Class that stores essential test constants
 */
@SuppressWarnings("WeakerAccess")
public class TestConstants {
    /**
     * Number of repeating iterations.
     */
    public static final int REPEAT_IT = 10;

    /**
     * Returns supplier that returns expanding integers staring from 0.
     *
     * @return supplier that returns expanding integers staring from 0
     */
    public static Supplier<Integer> getIncSupplier() {
        return new Supplier<Integer>() {
            int x = 0;

            @Override
            public Integer get() {
                return x++;
            }
        };
    }

    /**
     * Returns supplier that returns null at first usage. Further results are
     * expanding integers staring from 1.
     *
     * @return supplier that returns null at first usage. Further results are
     * expanding integers staring from 1.
     */
    public static Supplier<Integer> getNullSupplier() {
        return new Supplier<Integer>() {
            int x = 0;

            @Override
            public Integer get() {
                if (x++ == 0) return null;
                return x;
            }
        };
    }

    /**
     * Returns supplier that returns constant string.
     *
     */
    public static final Supplier<String> CONST_STRING_SUPPLIER = () -> "abacba";

}
