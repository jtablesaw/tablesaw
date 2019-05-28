package tech.tablesaw.columns.temporal;

import static tech.tablesaw.columns.temporal.TemporalPredicates.isMissing;
import static tech.tablesaw.columns.temporal.TemporalPredicates.isNotMissing;

import java.time.temporal.Temporal;
import java.util.function.BiPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.instant.PackedInstant;
import tech.tablesaw.filtering.predicates.LongBiPredicate;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

public interface TemporalFilters<T extends Temporal> extends Column<T> {

    default Selection eval(LongPredicate predicate) {
        Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            long next = data().getLong(idx);
            if (predicate.test(next)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    default Selection eval(LongBiPredicate predicate, long value) {
        Selection bitmap = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            long next = data().getLong(idx);
            if (predicate.test(next, value)) {
                bitmap.add(idx);
            }
        }
        return bitmap;
    }

    default Selection eval(LongBiPredicate predicate, DateTimeColumn otherColumn) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            if (predicate.test(this.getLongInternal(idx), otherColumn.getLongInternal(idx))) {
                selection.add(idx);
            }
        }
        return selection;
    }

    default Selection eval(BiPredicate<T, T> predicate, T valueToCompare) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            if (predicate.test(get(idx), valueToCompare)) {
                selection.add(idx);
            }
        }
        return selection;
    }

    default Selection eval(Predicate<T> predicate) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            if (predicate.test(get(idx))) {
                selection.add(idx);
            }
        }
        return selection;
    }

    default Selection isBetweenExcluding(long lowPackedDateTime, long highPackedDateTime) {
        return eval(PackedInstant::isAfter, lowPackedDateTime)
                .and(eval(PackedInstant::isBefore, highPackedDateTime));
    }

    default Selection isBetweenIncluding(long lowPackedDateTime, long highPackedDateTime) {
        return eval(PackedInstant::isOnOrAfter, lowPackedDateTime)
                .and(eval(PackedInstant::isOnOrBefore, highPackedDateTime));
    }

    @Override
    default Selection isMissing() {
        return eval(isMissing);
    }

    @Override
    default Selection isNotMissing() {
        return eval(isNotMissing);
    }

    int size();

    LongArrayList data();

    long getLongInternal(int index);

    T get(int index);
}
