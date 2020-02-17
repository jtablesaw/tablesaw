package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;
import java.util.Collection;
import tech.tablesaw.columns.Column;

@Beta
public interface StringFilterSpec<T> extends FilterSpec<T> {

  T isEmptyString();

  T startsWith(String string);

  T endsWith(String string);

  T containsString(String string);

  T matchesRegex(String string);

  T isAlpha();

  T isNumeric();

  T isAlphaNumeric();

  T isUpperCase();

  T isLowerCase();

  T lengthEquals(int stringLength);

  T isShorterThan(int stringLength);

  T isLongerThan(int stringLength);

  T isIn(String... strings);

  T isIn(Collection<String> strings);

  T isNotIn(String... strings);

  T isNotIn(Collection<String> strings);

  T isEqualTo(Column<String> other);

  T isNotEqualTo(Column<String> other);

  T equalsIgnoreCase(Column<String> other);

  T startsWith(Column<String> other);

  T isEqualTo(String string);

  T isNotEqualTo(String string);
}
