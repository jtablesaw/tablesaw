package tech.tablesaw.columns.datetimes;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.filtering.predicates.LongBiPredicate;
import tech.tablesaw.filtering.predicates.LongPredicate;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.time.LocalDateTime;

import static tech.tablesaw.columns.datetimes.DateTimePredicates.*;

public interface DateTimeFilters extends Column {

    default Selection isAfter(LocalDateTime value) {
        return eval(isGreaterThan, PackedLocalDateTime.pack(value));
    }

    default Selection isAfter(Long packedDateTime) {
        return eval(isGreaterThan, packedDateTime);
    }

    default Selection isOnOrAfter(long value) {
        return eval(isGreaterThanOrEqualTo, value);
    }

    default Selection isOnOrAfter(LocalDateTime value) {
        return eval(isGreaterThanOrEqualTo, PackedLocalDateTime.pack(value));
    }

    default Selection isBefore(LocalDateTime value) {
        return eval(isLessThan, PackedLocalDateTime.pack(value));
    }

    default Selection isBefore(Long packedDateTime) {
        return eval(isLessThan, packedDateTime);
    }

    default Selection isOnOrBefore(long value) {
        return eval(isLessThanOrEqualTo, value);
    }

    default Selection isOnOrBefore(LocalDateTime value) {
        return eval(isLessThanOrEqualTo, PackedLocalDateTime.pack(value));
    }

    default Selection isAfter(DateTimeColumn column) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        LongIterator intIterator = column.longIterator();
        for (long next : data()) {
            if (next > intIterator.nextLong()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection isBefore(DateTimeColumn column) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        LongIterator intIterator = column.longIterator();
        for (long next : data()) {
            if (next < intIterator.nextLong()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection isEqualTo(LocalDateTime value) {
        long packed = PackedLocalDateTime.pack(value);
        return eval(isEqualTo, packed);
    }

    default Selection isNotEqualTo(LocalDateTime value) {
        long packed = PackedLocalDateTime.pack(value);
        return eval(isNotEqualTo, packed);
    }

    default Selection isEqualTo(DateTimeColumn column) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        LongIterator intIterator = column.longIterator();
        for (long next : data()) {
            if (next == intIterator.nextLong()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    default Selection isMonday() {
        return eval(PackedLocalDateTime::isMonday);
    }

    default Selection isTuesday() {
        return eval(PackedLocalDateTime::isTuesday);
    }

    default Selection isWednesday() {
        return eval(PackedLocalDateTime::isWednesday);
    }

    default Selection isThursday() {
        return eval(PackedLocalDateTime::isThursday);
    }

    default Selection isFriday() {
        return eval(PackedLocalDateTime::isFriday);
    }

    default Selection isSaturday() {
        return eval(PackedLocalDateTime::isSaturday);
    }

    default Selection isSunday() {
        return eval(PackedLocalDateTime::isSunday);
    }

    default Selection isInJanuary() {
        return eval(PackedLocalDateTime::isInJanuary);
    }

    default Selection isInFebruary() {
        return eval(PackedLocalDateTime::isInFebruary);
    }

    default Selection isInMarch() {
        return eval(PackedLocalDateTime::isInMarch);
    }

    default Selection isInApril() {
        return eval(PackedLocalDateTime::isInApril);
    }

    default Selection isInMay() {
        return eval(PackedLocalDateTime::isInMay);
    }

    default Selection isInJune() {
        return eval(PackedLocalDateTime::isInJune);
    }

    default Selection isInJuly() {
        return eval(PackedLocalDateTime::isInJuly);
    }

    default Selection isInAugust() {
        return eval(PackedLocalDateTime::isInAugust);
    }

    default Selection isInSeptember() {
        return eval(PackedLocalDateTime::isInSeptember);
    }

    default Selection isInOctober() {
        return eval(PackedLocalDateTime::isInOctober);
    }

    default Selection isInNovember() {
        return eval(PackedLocalDateTime::isInNovember);
    }

    default Selection isInDecember() {
        return eval(PackedLocalDateTime::isInDecember);
    }

    default Selection isFirstDayOfMonth() {
        return eval(PackedLocalDateTime::isFirstDayOfMonth);
    }

    default Selection isLastDayOfMonth() {
        return eval(PackedLocalDateTime::isLastDayOfMonth);
    }

    default Selection isInQ1() {
        return eval(PackedLocalDateTime::isInQ1);
    }

    default Selection isInQ2() {
        return eval(PackedLocalDateTime::isInQ2);
    }

    default Selection isInQ3() {
        return eval(PackedLocalDateTime::isInQ3);
    }

    default Selection isInQ4() {
        return eval(PackedLocalDateTime::isInQ4);
    }

    default Selection isNoon() {
        return eval(PackedLocalDateTime::isNoon);
    }

    default Selection isMidnight() {
        return eval(PackedLocalDateTime::isMidnight);
    }

    default Selection isBeforeNoon() {
        return eval(PackedLocalDateTime::AM);
    }

    default Selection isAfterNoon() {
        return eval(PackedLocalDateTime::PM);
    }

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

    default boolean contains(LocalDateTime dateTime) {
        long dt = PackedLocalDateTime.pack(dateTime);
        return data().contains(dt);
    }

    default Selection isInYear(int year) {
        return eval(i -> PackedLocalDateTime.isInYear(i, year));
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
}
