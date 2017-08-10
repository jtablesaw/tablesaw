package com.github.lwhite1.tablesaw.index;

import com.github.lwhite1.tablesaw.api.DateTimeColumn;
import com.github.lwhite1.tablesaw.columns.packeddata.PackedLocalDateTime;
import com.github.lwhite1.tablesaw.util.Selection;

import java.time.LocalDateTime;

/**
 * An index for four-byte integer and Date columns
 */
public class DateTimeIndex {

    private final LongIndex index;

    public DateTimeIndex(DateTimeColumn column) {
        index = new LongIndex(column);
    }

    /**
     * Returns a bitmap containing row numbers of all cells matching the given int
     *
     * @param value This is a 'key' from the index perspective, meaning it is a value from the standpoint of the column
     */
    public Selection get(LocalDateTime value) {
        return index.get(PackedLocalDateTime.pack(value));
    }

    public Selection atLeast(LocalDateTime value) {
        return index.atLeast(PackedLocalDateTime.pack(value));
    }

    public Selection greaterThan(LocalDateTime value) {
        return index.greaterThan(PackedLocalDateTime.pack(value));
    }

    public Selection atMost(LocalDateTime value) {
        return index.atMost(PackedLocalDateTime.pack(value));
    }

    public Selection lessThan(LocalDateTime value) {
        return index.lessThan(PackedLocalDateTime.pack(value));
    }
}