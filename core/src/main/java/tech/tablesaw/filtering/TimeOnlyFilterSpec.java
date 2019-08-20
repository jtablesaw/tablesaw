package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;
import java.time.LocalTime;
import tech.tablesaw.api.TimeColumn;

@Beta
public interface TimeOnlyFilterSpec<T> extends FilterSpec<T> {

  T isBefore(LocalTime time);

  T isAfter(LocalTime time);

  T isOnOrAfter(LocalTime time);

  T isOnOrBefore(LocalTime value);

  T isNotEqualTo(LocalTime value);

  T isEqualTo(LocalTime value);

  T isEqualTo(TimeColumn column);

  T isBefore(TimeColumn column);

  T isAfter(TimeColumn column);

  T isNotEqualTo(TimeColumn column);
}
