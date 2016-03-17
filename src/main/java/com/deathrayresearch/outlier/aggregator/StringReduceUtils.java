package com.deathrayresearch.outlier.aggregator;

import com.deathrayresearch.outlier.columns.StringColumnUtils;

/**
 *
 */
public interface StringReduceUtils extends StringColumnUtils {

    int size();

    default String appendAll(String lineBreak) {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (String next : data()) {
            builder.append(next);
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
