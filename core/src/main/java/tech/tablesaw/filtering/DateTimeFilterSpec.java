package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;
import java.time.LocalDateTime;
import tech.tablesaw.api.DateTimeColumn;

@Beta
public interface DateTimeFilterSpec<T>
    extends DateAndDateTimeFilterSpec<T>, TimeAndDateTimeFilterSpec<T> {

  T isBetweenExcluding(LocalDateTime lowValue, LocalDateTime highValue);

  T isBetweenIncluding(LocalDateTime lowValue, LocalDateTime highValue);

  T isBefore(LocalDateTime value);

  T isOnOrBefore(LocalDateTime value);

  T isOnOrAfter(LocalDateTime value);

  T isEqualTo(LocalDateTime value);

  T isEqualTo(DateTimeColumn column);

  T isNotEqualTo(DateTimeColumn column);

  T isOnOrBefore(DateTimeColumn column);

  T isOnOrAfter(DateTimeColumn column);

  T isAfter(DateTimeColumn column);

  T isBefore(DateTimeColumn column);

  T isAfter(LocalDateTime time);

  T isNotEqualTo(LocalDateTime value);
}
