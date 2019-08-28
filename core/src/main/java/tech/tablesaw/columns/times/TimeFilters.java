package tech.tablesaw.columns.times;

import static tech.tablesaw.columns.DateAndTimePredicates.isGreaterThan;
import static tech.tablesaw.columns.DateAndTimePredicates.isLessThan;

import java.time.LocalTime;
import java.util.function.BiPredicate;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.filtering.predicates.IntBiPredicate;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

public interface TimeFilters extends Column<LocalTime> {

  TimeColumn where(Selection selection);

  default Selection eval(IntBiPredicate predicate, TimeColumn otherColumn) {
    Selection selection = new BitmapBackedSelection();
    for (int idx = 0; idx < size(); idx++) {
      if (predicate.test(getIntInternal(idx), otherColumn.getIntInternal(idx))) {
        selection.add(idx);
      }
    }
    return selection;
  }

  default Selection eval(IntPredicate predicate) {
    Selection selection = new BitmapBackedSelection();
    for (int idx = 0; idx < size(); idx++) {
      int next = getIntInternal(idx);
      if (predicate.test(next)) {
        selection.add(idx);
      }
    }
    return selection;
  }

  default Selection eval(IntBiPredicate predicate, int value) {
    Selection selection = new BitmapBackedSelection();
    for (int idx = 0; idx < size(); idx++) {
      int next = getIntInternal(idx);
      if (predicate.test(next, value)) {
        selection.add(idx);
      }
    }
    return selection;
  }

  default Selection eval(BiPredicate<LocalTime, LocalTime> predicate, LocalTime valueToCompare) {
    Selection selection = new BitmapBackedSelection();
    for (int idx = 0; idx < size(); idx++) {
      if (predicate.test(get(idx), valueToCompare)) {
        selection.add(idx);
      }
    }
    return selection;
  }

  default Selection eval(Predicate<LocalTime> predicate) {
    Selection selection = new BitmapBackedSelection();
    for (int idx = 0; idx < size(); idx++) {
      if (predicate.test(get(idx))) {
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
   * Applies a function to every value in this column that returns true if the time is in the AM or
   * "before noon". Note: we follow the convention that 12:00 NOON is PM and 12 MIDNIGHT is AM
   */
  default Selection isBeforeNoon() {
    return eval(PackedLocalTime::AM);
  }

  /**
   * Applies a function to every value in this column that returns true if the time is in the PM or
   * "after noon". Note: we follow the convention that 12:00 NOON is PM and 12 MIDNIGHT is AM
   */
  default Selection isAfterNoon() {
    return eval(PackedLocalTime::PM);
  }

  default Selection isNotEqualTo(LocalTime value) {
    Selection results = new BitmapBackedSelection();
    int packedLocalTime = PackedLocalTime.pack(value);
    for (int i = 0; i < size(); i++) {
      if (packedLocalTime != getIntInternal(i)) {
        results.add(i);
      }
    }
    return results;
  }

  default Selection isEqualTo(LocalTime value) {
    Selection results = new BitmapBackedSelection();
    int packedLocalTime = PackedLocalTime.pack(value);
    for (int i = 0; i < size(); i++) {
      if (packedLocalTime == getIntInternal(i)) {
        results.add(i);
      }
    }
    return results;
  }

  /**
   * Returns a bitmap flagging the records for which the value in this column is equal to the value
   * in the given column Columnwise isEqualTo.
   */
  default Selection isEqualTo(TimeColumn column) {
    Selection results = new BitmapBackedSelection();
    for (int i = 0; i < size(); i++) {
      if (getIntInternal(i) == column.getIntInternal(i)) {
        results.add(i);
      }
    }
    return results;
  }

  /**
   * Returns a bitmap flagging the records for which the value in this column is before the value in
   * the given column Columnwise isEqualTo.
   */
  default Selection isBefore(TimeColumn column) {
    return eval(isLessThan, column);
  }

  /**
   * Returns a bitmap flagging the records for which the value in this column is after the value in
   * the given column Columnwise isEqualTo.
   */
  default Selection isAfter(TimeColumn column) {
    return eval(isGreaterThan, column);
  }

  /**
   * Returns a bitmap flagging the records for which the value in this column is NOT equal to the
   * value in the given column Columnwise isEqualTo.
   */
  default Selection isNotEqualTo(TimeColumn column) {
    return Selection.withRange(0, size()).andNot(isEqualTo(column));
  }

  LocalTime get(int index);

  /** Returns the packed time representation of the value at index */
  int getIntInternal(int index);
}
