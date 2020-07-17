package tech.tablesaw.columns.dateranges;

import tech.tablesaw.columns.Column;

public interface DateRangeFunctions extends Column<DateRange> {
  DateRange min();

  DateRange max();
}
