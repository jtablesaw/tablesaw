package tech.tablesaw.columns.times;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.filtering.predicates.IntBiPredicate;
import tech.tablesaw.filtering.predicates.IntPredicate;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.time.LocalTime;

public interface TimeFilters extends Column {

    TimeColumn selectWhere(Selection selection);

    default Selection eval(IntPredicate predicate) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            int next = data().getInt(idx);
            if (predicate.test(next)) {
                selection.add(idx);
            }
        }
        return selection;
    }

    default Selection eval(IntBiPredicate predicate, int value) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            int next = data().getInt(idx);
            if (predicate.test(next, value)) {
                selection.add(idx);
            }
        }
        return selection;
    }

    default Selection isMidnight() {
        return eval(PackedLocalTime::isMidnight);
    }

    default Selection isNoon() {
        return eval(PackedLocalTime::isNoon);
    }

    default Selection isBefore(LocalTime time) {
        return isBefore(PackedLocalTime.pack(time));
    }

    default Selection isBefore(int packedTime) {
        return eval(PackedLocalTime::isBefore, packedTime);
    }

    default Selection isAfter(LocalTime time) {
        return isAfter(PackedLocalTime.pack(time));
    }

    default Selection isAfter(int packedTime) {
        return eval(PackedLocalTime::isAfter, packedTime);
    }

    default Selection isOnOrAfter(LocalTime time) {
        int packed = PackedLocalTime.pack(time);
        return isOnOrAfter(packed);
    }

    default Selection isOnOrAfter(int packed) {
        return eval(PackedLocalTime::isOnOrAfter, packed);
    }

    default Selection isOnOrBefore(LocalTime value) {
        int packed = PackedLocalTime.pack(value);
        return isOnOrBefore(packed);
    }

    default Selection isOnOrBefore(int packed) {
        return eval(PackedLocalTime::isOnOrBefore, packed);
    }

    /**
     * Applies a function to every value in this column that returns true if the time is in the AM or "before noon".
     * Note: we follow the convention that 12:00 NOON is PM and 12 MIDNIGHT is AM
     */
    default Selection isBeforeNoon() {
        return eval(PackedLocalTime::AM);
    }

    /**
     * Applies a function to every value in this column that returns true if the time is in the PM or "after noon".
     * Note: we follow the convention that 12:00 NOON is PM and 12 MIDNIGHT is AM
     */
    default Selection isAfterNoon() {
        return eval(PackedLocalTime::PM);
    }

    IntArrayList data();
}
