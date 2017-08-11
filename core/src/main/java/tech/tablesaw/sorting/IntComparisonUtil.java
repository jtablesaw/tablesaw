package tech.tablesaw.sorting;

/**
 *
 */
public class IntComparisonUtil {

    private static IntComparisonUtil instance = new IntComparisonUtil();

    private IntComparisonUtil() {
    }

    public static IntComparisonUtil getInstance() {
        return instance;
    }

    public int compare(int a, int b) {
        return a - b;
    }
}
