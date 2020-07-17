package tech.tablesaw.columns.dates;

import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.temporal.DateRange;

public interface DateRangeFunctions extends Column<DateRange> {
  DateRange min();

  DateRange max();
}
