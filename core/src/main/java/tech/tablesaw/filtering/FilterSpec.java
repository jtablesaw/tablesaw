package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;

@Beta
public interface FilterSpec<T> {

  T isNotMissing();

  T isMissing();
}
