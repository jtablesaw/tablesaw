package tech.tablesaw.columns.dateranges;

import java.util.function.Predicate;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

public interface DateRangeFilters {

  int size();

  DateRange get(int index);

  default Selection eval(Predicate<DateRange> predicate) {
    Selection selection = new BitmapBackedSelection();
    for (int idx = 0; idx < size(); idx++) {
      DateRange next = get(idx);
      if (predicate.test(next)) {
        selection.add(idx);
      }
    }
    return selection;
  }
}
