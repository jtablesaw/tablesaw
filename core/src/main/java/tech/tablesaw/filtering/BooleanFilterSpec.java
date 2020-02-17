package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;
import tech.tablesaw.api.BooleanColumn;

@Beta
public interface BooleanFilterSpec<T> extends FilterSpec<T> {

  T isFalse();

  T isTrue();

  T isEqualTo(BooleanColumn other);
}
