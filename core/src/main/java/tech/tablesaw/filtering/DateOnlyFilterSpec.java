package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;
import java.time.LocalDate;
import tech.tablesaw.api.DateColumn;

@Beta
public interface DateOnlyFilterSpec<T> extends FilterSpec<T> {

  T isBetweenExcluding(LocalDate lowValue, LocalDate highValue);

  T isBetweenIncluding(LocalDate lowValue, LocalDate highValue);

  T isEqualTo(LocalDate value);

  T isNotEqualTo(LocalDate value);

  T isEqualTo(DateColumn column);

  T isNotEqualTo(DateColumn column);

  T isOnOrBefore(DateColumn column);

  T isOnOrAfter(DateColumn column);

  T isAfter(DateColumn column);

  T isBefore(DateColumn column);
}
