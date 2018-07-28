package tech.tablesaw.plotly;

import java.util.Arrays;

public class Utils {

    public static String dataAsString(double[] data) {
        return Arrays.toString(data);
    }

    public static String dataAsString(Object[] data) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < data.length; i++) {
            Object o = data[i];
            builder.append("'");
            builder.append(String.valueOf(o));
            builder.append("'");
            if (i < data.length - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        return builder.toString();
    }

    public static String dataAsString(double[][] data) {
        StringBuilder builder = new StringBuilder("[");
        for (double[] row : data) {
            builder.append("[");
            for (double value : row) {
                builder.append(value);
                builder.append(",");
            }
            builder.append("],");
        }
        builder.append("]");
        return builder.toString();
    }
}
