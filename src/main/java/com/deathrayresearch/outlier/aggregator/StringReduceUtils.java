package com.deathrayresearch.outlier.aggregator;

/**
 *
 */
public interface StringReduceUtils {

    boolean hasNext();

    String next();

    int size();

    default String appendAll(String lineBreak) {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        while (hasNext()) {
            builder.append(next());
            if (count < size() - 1) {
                builder.append(lineBreak);
                count++;
            } else {
                break;
            }
        }

        return builder.toString();
    }

    default String appendAll() {
        return appendAll(" ");
    }

}
