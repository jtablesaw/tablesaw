package com.github.lwhite1.tablesaw.index;

import com.github.lwhite1.tablesaw.api.DateColumn;
import com.github.lwhite1.tablesaw.columns.packeddata.PackedLocalDate;
import com.github.lwhite1.tablesaw.util.Selection;

import java.time.LocalDate;

/**
 * An index for four-byte integer and Date columns
 */
public class DateIndex {

    private final IntIndex index;

    public DateIndex(DateColumn column) {
        index = new IntIndex(column);
    }

    /**
     * Returns a bitmap containing row numbers of all cells matching the given int
     *
     * @param value This is a 'key' from the index perspective, meaning it is a value from the standpoint of the column
     */
    public Selection get(LocalDate value) {
        return index.get(PackedLocalDate.pack(value));
    }

    public Selection atLeast(LocalDate value) {
        return index.atLeast(PackedLocalDate.pack(value));
    }

    public Selection greaterThan(LocalDate value) {
        return index.greaterThan(PackedLocalDate.pack(value));
    }

    public Selection atMost(LocalDate value) {
        return index.atMost(PackedLocalDate.pack(value));
    }

    public Selection lessThan(LocalDate value) {
        return index.lessThan(PackedLocalDate.pack(value));
    }
}