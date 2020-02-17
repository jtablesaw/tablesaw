package tech.tablesaw.filtering;

import com.google.common.annotations.Beta;
import java.util.Collection;
import java.util.function.Function;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

@Beta
public class DeferredTextColumn extends DeferredColumn
    implements StringFilterSpec<Function<Table, Selection>> {

  public DeferredTextColumn(String columnName) {
    super(columnName);
  }

  @Override
  public Function<Table, Selection> isEmptyString() {
    return table -> table.textColumn(name()).isEmptyString();
  }

  @Override
  public Function<Table, Selection> startsWith(String string) {
    return table -> table.textColumn(name()).startsWith(string);
  }

  @Override
  public Function<Table, Selection> endsWith(String string) {
    return table -> table.textColumn(name()).endsWith(string);
  }

  @Override
  public Function<Table, Selection> containsString(String string) {
    return table -> table.textColumn(name()).containsString(string);
  }

  @Override
  public Function<Table, Selection> matchesRegex(String string) {
    return table -> table.textColumn(name()).matchesRegex(string);
  }

  @Override
  public Function<Table, Selection> isAlpha() {
    return table -> table.textColumn(name()).isAlpha();
  }

  @Override
  public Function<Table, Selection> isNumeric() {
    return table -> table.textColumn(name()).isNumeric();
  }

  @Override
  public Function<Table, Selection> isAlphaNumeric() {
    return table -> table.textColumn(name()).isAlphaNumeric();
  }

  @Override
  public Function<Table, Selection> isUpperCase() {
    return table -> table.textColumn(name()).isUpperCase();
  }

  @Override
  public Function<Table, Selection> isLowerCase() {
    return table -> table.textColumn(name()).isLowerCase();
  }

  @Override
  public Function<Table, Selection> lengthEquals(int stringLength) {
    return table -> table.textColumn(name()).lengthEquals(stringLength);
  }

  @Override
  public Function<Table, Selection> isShorterThan(int stringLength) {
    return table -> table.textColumn(name()).isShorterThan(stringLength);
  }

  @Override
  public Function<Table, Selection> isLongerThan(int stringLength) {
    return table -> table.textColumn(name()).isLongerThan(stringLength);
  }

  @Override
  public Function<Table, Selection> isIn(String... strings) {
    return table -> table.textColumn(name()).isIn(strings);
  }

  @Override
  public Function<Table, Selection> isIn(Collection<String> strings) {
    return table -> table.textColumn(name()).isIn(strings);
  }

  @Override
  public Function<Table, Selection> isNotIn(String... strings) {
    return table -> table.textColumn(name()).isNotIn(strings);
  }

  @Override
  public Function<Table, Selection> isNotIn(Collection<String> strings) {
    return table -> table.textColumn(name()).isNotIn(strings);
  }

  @Override
  public Function<Table, Selection> isEqualTo(Column<String> other) {
    return table -> table.textColumn(name()).isEqualTo(other);
  }

  @Override
  public Function<Table, Selection> isNotEqualTo(Column<String> other) {
    return table -> table.textColumn(name()).isNotEqualTo(other);
  }

  @Override
  public Function<Table, Selection> equalsIgnoreCase(Column<String> other) {
    return table -> table.textColumn(name()).equalsIgnoreCase(other);
  }

  @Override
  public Function<Table, Selection> startsWith(Column<String> other) {
    return table -> table.textColumn(name()).startsWith(other);
  }

  @Override
  public Function<Table, Selection> isEqualTo(String string) {
    return table -> table.textColumn(name()).isEqualTo(string);
  }

  @Override
  public Function<Table, Selection> isNotEqualTo(String string) {
    return table -> table.textColumn(name()).isNotEqualTo(string);
  }
}
