package tech.tablesaw.columns.dates;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.DateAndTimePredicates;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.filtering.predicates.IntBiPredicate;
import tech.tablesaw.filtering.predicates.IntPredicate;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.time.LocalDate;

import static tech.tablesaw.columns.DateAndTimePredicates.*;

public interface DateFilters extends Column {

    DateColumn select(Filter filter);

    Selection eval(IntPredicate predicate);

    Selection eval(IntBiPredicate predicate, int value);

    default Selection isMonday() {
        return eval(PackedLocalDate::isMonday);
    }

    default Selection isTuesday() {
        return eval(PackedLocalDate::isTuesday);
    }

    default Selection isWednesday() {
        return eval(PackedLocalDate::isWednesday);
    }

    default Selection isThursday() {
        return eval(PackedLocalDate::isThursday);
    }

    default Selection isFriday() {
        return eval(PackedLocalDate::isFriday);
    }

    default Selection isSaturday() {
        return eval(PackedLocalDate::isSaturday);
    }

    default Selection isSunday() {
        return eval(PackedLocalDate::isSunday);
    }

    default Selection isInJanuary() {
        return eval(PackedLocalDate::isInJanuary);
    }

    default Selection isInFebruary() {
        return eval(PackedLocalDate::isInFebruary);
    }

    default Selection isInMarch() {
        return eval(PackedLocalDate::isInMarch);
    }

    default Selection isInApril() {
        return eval(PackedLocalDate::isInApril);
    }

    default Selection isInMay() {
        return eval(PackedLocalDate::isInMay);
    }

    default Selection isInJune() {
        return eval(PackedLocalDate::isInJune);
    }

    default Selection isInJuly() {
        return eval(PackedLocalDate::isInJuly);
    }

    default Selection isInAugust() {
        return eval(PackedLocalDate::isInAugust);
    }

    default Selection isInSeptember() {
        return eval(PackedLocalDate::isInSeptember);
    }

    default Selection isInOctober() {
        return eval(PackedLocalDate::isInOctober);
    }

    default Selection isInNovember() {
        return eval(PackedLocalDate::isInNovember);
    }

    default Selection isInDecember() {
        return eval(PackedLocalDate::isInDecember);
    }

    default Selection isFirstDayOfMonth() {
        return eval(PackedLocalDate::isFirstDayOfMonth);
    }

    default Selection isLastDayOfMonth() {
        return eval(PackedLocalDate::isLastDayOfMonth);
    }

    default Selection isInQ1() {
        return eval(PackedLocalDate::isInQ1);
    }

    default Selection isInQ2() {
        return eval(PackedLocalDate::isInQ2);
    }

    default Selection isInQ3() {
        return eval(PackedLocalDate::isInQ3);
    }

    default Selection isInQ4() {
        return eval(PackedLocalDate::isInQ4);
    }

    default Selection isInYear(int year) {
        return eval(PackedLocalDate::isInYear, year);
    }

    default Selection isAfter(int value) {
        return eval(PackedLocalDate::isAfter, value);
    }

    default Selection isAfter(LocalDate value) {
        int packed = PackedLocalDate.pack(value);
        return eval(PackedLocalDate::isAfter, packed);
    }

    default Selection isBefore(int value) {
        return eval(PackedLocalDate::isBefore, value);
    }

    default Selection isBefore(LocalDate value) {
        return isBefore(PackedLocalDate.pack(value));
    }

    default Selection isOnOrBefore(LocalDate value) {
        int packed = PackedLocalDate.pack(value);
        return eval(PackedLocalDate::isOnOrBefore, packed);
    }

    default Selection isOnOrAfter(LocalDate value) {
        int packed = PackedLocalDate.pack(value);
        return eval(DateAndTimePredicates.isGreaterThanOrEqualTo, packed);
    }

    default Selection isEqualTo(LocalDate value) {
        return eval(isEqualTo, PackedLocalDate.pack(value));
    }

    default Selection isEqualTo(int packedDate) {
        return eval(isEqualTo, packedDate);
    }

    /**
     * Returns a bitmap flagging the records for which the value in this column is equal to the value in the given
     * column
     * Columnwise isEqualTo.
     */
    default Selection isEqualTo(DateColumn column) {
        Selection results = new BitmapBackedSelection();
        int i = 0;
        IntIterator intIterator = column.intIterator();
        for (int next : data()) {
            if (next == intIterator.nextInt()) {
                results.add(i);
            }
            i++;
        }
        return results;
    }

    IntArrayList data();
}
