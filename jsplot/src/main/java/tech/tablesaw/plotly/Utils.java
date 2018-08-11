package tech.tablesaw.plotly;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Utils {

    public static String dataAsString(double[] data) {
        return Arrays.toString(data);
    }

    public static String dataAsString(Object[] data) {
        return Arrays.stream(data)
                .map(d -> "'" + String.valueOf(d) + "'")
                .collect(Collectors.joining(",", "[", "]"));
    }

    public static String dataAsString(double[][] data) {
        return Arrays.stream(data)
                .map(doubles -> Arrays.stream(doubles)
                        .mapToObj(d -> "'" + String.valueOf(d) + "'")
                        .collect(Collectors.joining(",", "[", "]")))
                .collect(Collectors.joining(",", "[", "]"));
    }
}
