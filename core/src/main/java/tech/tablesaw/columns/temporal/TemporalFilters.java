package tech.tablesaw.columns.temporal;

import static tech.tablesaw.columns.temporal.TemporalPredicates.*;

import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.function.BiPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.instant.PackedInstant;
import tech.tablesaw.filtering.InstantFilterSpec;
import tech.tablesaw.filtering.predicates.LongBiPredicate;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

public interface TemporalFilters<T extends Temporal>
    extends Column<T>, InstantFilterSpec<Selection> {

  default Selection eval(LongPredicate predicate) {
    Selection bitmap = new BitmapBackedSelection();
    for (int idx = 0; idx < size(); idx++) {
      long next = getLongInternal(idx);
      if (predicate.test(next)) {
        bitmap.add(idx);
      }
    }
    return bitmap;
  }

  default Selection eval(LongBiPredicate predicate, long value) {
    Selection bitmap = new BitmapBackedSelection();
    for (int idx = 0; idx < size(); idx++) {
      long next = getLongInternal(idx);
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

  default Selection isAfter(Instant value) {
    return eval(isGreaterThan, PackedInstant.pack(value));
  }

  default Selection isBefore(Instant value) {
    return eval(isLessThan, PackedInstant.pack(value));
  }

  default Selection isEqualTo(Instant value) {
    return eval(isEqualTo, PackedInstant.pack(value));
  }

  int size();

  long getLongInternal(int index);

  T get(int index);

  @Override
  Selection isMissing();

  @Override
  Selection isNotMissing();
}
