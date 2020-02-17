package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;

@Beta
public interface TimeAndDateTimeFilterSpec<T> extends FilterSpec<T> {

  T isMidnight();

  T isNoon();

  T isBeforeNoon();

  T isAfterNoon();
}
