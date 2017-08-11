package tech.tablesaw.index;

import java.time.LocalDateTime;

import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.columns.packeddata.PackedLocalDateTime;
import tech.tablesaw.util.Selection;

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