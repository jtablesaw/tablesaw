package tech.tablesaw.filtering;

import java.util.Collection;
import java.util.function.Function;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

public class DeferredTextColumn extends DeferredColumn {

  public DeferredTextColumn(String columnName) {
    super(columnName);
  }

  public Function<Table, Selection> isEmptyString() {
    return table -> table.textColumn(name()).isEmptyString();
  }

  public Function<Table, Selection> startsWith(String string) {
    return table -> table.textColumn(name()).startsWith(string);
  }

  public Function<Table, Selection> endsWith(String string) {
    return table -> table.textColumn(name()).endsWith(string);
  }

  public Function<Table, Selection> containsString(String string) {
    return table -> table.textColumn(name()).containsString(string);
  }

  public Function<Table, Selection> matchesRegex(String string) {
    return table -> table.textColumn(name()).matchesRegex(string);
  }

  public Function<Table, Selection> isAlpha() {
    return table -> table.textColumn(name()).isAlpha();
  }

  public Function<Table, Selection> isNumeric() {
    return table -> table.textColumn(name()).isNumeric();
  }

  public Function<Table, Selection> isAlphaNumeric() {
    return table -> table.textColumn(name()).isAlphaNumeric();
  }

  public Function<Table, Selection> isUpperCase() {
    return table -> table.textColumn(name()).isUpperCase();
  }

  public Function<Table, Selection> isLowerCase() {
    return table -> table.textColumn(name()).isLowerCase();
  }

  public Function<Table, Selection> lengthEquals(int stringLength) {
    return table -> table.textColumn(name()).lengthEquals(stringLength);
  }

  public Function<Table, Selection> isShorterThan(int stringLength) {
    return table -> table.textColumn(name()).isShorterThan(stringLength);
  }

  public Function<Table, Selection> isLongerThan(int stringLength) {
    return table -> table.textColumn(name()).isLongerThan(stringLength);
  }

  public Function<Table, Selection> isIn(String... strings) {
    return table -> table.textColumn(name()).isIn(strings);
  }

  public Function<Table, Selection> isIn(Collection<String> strings) {
    return table -> table.textColumn(name()).isIn(strings);
  }

  public Function<Table, Selection> isNotIn(String... strings) {
    return table -> table.textColumn(name()).isNotIn(strings);
  }

  public Function<Table, Selection> isNotIn(Collection<String> strings) {
    return table -> table.textColumn(name()).isNotIn(strings);
  }

  public Function<Table, Selection> isEqualTo(Column<String> other) {
    return table -> table.textColumn(name()).isEqualTo(other);
  }

  public Function<Table, Selection> isNotEqualTo(Column<String> other) {
    return table -> table.textColumn(name()).isNotEqualTo(other);
  }

  public Function<Table, Selection> equalsIgnoreCase(Column<String> other) {
    return table -> table.textColumn(name()).equalsIgnoreCase(other);
  }

  public Function<Table, Selection> startsWith(Column<String> other) {
    return table -> table.textColumn(name()).startsWith(other);
  }

  public Function<Table, Selection> isMissing() {
    return table -> table.textColumn(name()).isMissing();
  }

  public Function<Table, Selection> isNotMissing() {
    return table -> table.textColumn(name()).isNotMissing();
  }

  public Function<Table, Selection> isEqualTo(String string) {
    return table -> table.textColumn(name()).isEqualTo(string);
  }

  public Function<Table, Selection> isNotEqualTo(String string) {
    return table -> table.textColumn(name()).isNotEqualTo(string);
  }
}
