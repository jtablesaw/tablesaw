package tech.tablesaw.columns.datetimes;

import static tech.tablesaw.columns.datetimes.DateTimePredicates.isEqualTo;
import static tech.tablesaw.columns.datetimes.DateTimePredicates.isGreaterThan;
import static tech.tablesaw.columns.datetimes.DateTimePredicates.isGreaterThanOrEqualTo;
import static tech.tablesaw.columns.datetimes.DateTimePredicates.isInYear;
import static tech.tablesaw.columns.datetimes.DateTimePredicates.isLessThan;
import static tech.tablesaw.columns.datetimes.DateTimePredicates.isLessThanOrEqualTo;
import static tech.tablesaw.columns.datetimes.DateTimePredicates.isMissing;
import static tech.tablesaw.columns.datetimes.DateTimePredicates.isNotEqualTo;
import static tech.tablesaw.columns.datetimes.DateTimePredicates.isNotMissing;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.BiPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.filtering.predicates.LongBiPredicate;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

public interface DateTimeFilters extends Column<LocalDateTime> {

    default Selection isAfter(LocalDateTime value) {
        return eval(isGreaterThan, PackedLocalDateTime.pack(value));
    }

    default Selection isAfter(LocalDate value) {
        return isOnOrAfter(value.plusDays(1).atStartOfDay());
    }

    default Selection isOnOrAfter(LocalDate value) {
        return isOnOrAfter(value.atStartOfDay());
    }

    default Selection isOnOrAfter(LocalDateTime value) {
        return eval(isGreaterThanOrEqualTo, PackedLocalDateTime.pack(value));
    }

    default Selection isBefore(LocalDateTime value) {
        return eval(isLessThan, PackedLocalDateTime.pack(value));
    }

    default Selection isBefore(LocalDate value) {
        return isBefore(value.atStartOfDay());
    }

    default Selection isOnOrBefore(LocalDate value) {
        return isOnOrBefore(value.atStartOfDay());
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

    default Selection isNotEqualTo(DateTimeColumn column) {
        Selection results = Selection.withRange(0, size());
        return results.andNot(isEqualTo(column));
    }

    default Selection isOnOrAfter(DateTimeColumn column) {
        Selection results = Selection.withRange(0, size());
        return results.andNot(isBefore(column));
    }

    default Selection isOnOrBefore(DateTimeColumn column) {
        Selection results = Selection.withRange(0, size());
        return results.andNot(isAfter(column));
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

    default Selection eval(LongBiPredicate predicate, DateTimeColumn otherColumn) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            if (predicate.test(this.getLongInternal(idx), otherColumn.getLongInternal(idx))) {
                selection.add(idx);
            }
        }
        return selection;
    }

    default Selection eval(BiPredicate<LocalDateTime, LocalDateTime> predicate, LocalDateTime valueToCompare) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            if (predicate.test(get(idx), valueToCompare)) {
                selection.add(idx);
            }
        }
        return selection;
    }

    default Selection eval(Predicate<LocalDateTime> predicate) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            if (predicate.test(get(idx))) {
                selection.add(idx);
            }
        }
        return selection;
    }


    default Selection isBetweenExcluding(LocalDateTime lowValue, LocalDateTime highValue) {
        return isBetweenExcluding(PackedLocalDateTime.pack(lowValue), PackedLocalDateTime.pack(highValue));
    }

    default Selection isBetweenIncluding(LocalDateTime lowValue, LocalDateTime highValue) {
        return isBetweenIncluding(PackedLocalDateTime.pack(lowValue), PackedLocalDateTime.pack(highValue));
    }

    default Selection isBetweenExcluding(long lowPackedDateTime, long highPackedDateTime) {
        return eval(PackedLocalDateTime::isAfter, lowPackedDateTime)
                .and(eval(PackedLocalDateTime::isBefore, highPackedDateTime));
    }

    default Selection isBetweenIncluding(long lowPackedDateTime, long highPackedDateTime) {
        return eval(PackedLocalDateTime::isOnOrAfter, lowPackedDateTime)
                .and(eval(PackedLocalDateTime::isOnOrBefore, highPackedDateTime));
    }

    default Selection isInYear(int year) {
        return eval(isInYear, year);
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

    LocalDateTime get(int index);
}
