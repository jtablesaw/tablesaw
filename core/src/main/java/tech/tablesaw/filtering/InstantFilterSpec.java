package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;
import java.time.Instant;

@Beta
public interface InstantFilterSpec<T> extends FilterSpec<T> {

  T isEqualTo(Instant value);

  T isAfter(Instant value);

  T isBefore(Instant value);
}
