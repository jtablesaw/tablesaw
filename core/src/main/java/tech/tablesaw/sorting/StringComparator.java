package tech.tablesaw.sorting;

/**
 *
 */
public class StringComparator {

    private static StringComparator instance = new StringComparator();

    private StringComparator() {
    }

    public static StringComparator getInstance() {
        return instance;
    }

    public int compare(String a, String b) {
        return a.compareTo(b);
    }
}
