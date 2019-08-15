package tech.tablesaw.filtering;

import java.util.Collection;
import java.util.function.Function;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

public class DeferredStringColumn extends DeferredColumn {

  public DeferredStringColumn(String columnName) {
    super(columnName);
  }

  public Function<Table, Selection> isEmptyString() {
    return table -> table.stringColumn(name()).isEmptyString();
  }

  public Function<Table, Selection> startsWith(String string) {
    return table -> table.stringColumn(name()).startsWith(string);
  }

  public Function<Table, Selection> endsWith(String string) {
    return table -> table.stringColumn(name()).endsWith(string);
  }

  public Function<Table, Selection> containsString(String string) {
    return table -> table.stringColumn(name()).containsString(string);
  }

  public Function<Table, Selection> matchesRegex(String string) {
    return table -> table.stringColumn(name()).matchesRegex(string);
  }

  public Function<Table, Selection> isAlpha() {
    return table -> table.stringColumn(name()).isAlpha();
  }

  public Function<Table, Selection> isNumeric() {
    return table -> table.stringColumn(name()).isNumeric();
  }

  public Function<Table, Selection> isAlphaNumeric() {
    return table -> table.stringColumn(name()).isAlphaNumeric();
  }

  public Function<Table, Selection> isUpperCase() {
    return table -> table.stringColumn(name()).isUpperCase();
  }

  public Function<Table, Selection> isLowerCase() {
    return table -> table.stringColumn(name()).isLowerCase();
  }

  public Function<Table, Selection> lengthEquals(int stringLength) {
    return table -> table.stringColumn(name()).lengthEquals(stringLength);
  }

  public Function<Table, Selection> isShorterThan(int stringLength) {
    return table -> table.stringColumn(name()).isShorterThan(stringLength);
  }

  public Function<Table, Selection> isLongerThan(int stringLength) {
    return table -> table.stringColumn(name()).isLongerThan(stringLength);
  }

  public Function<Table, Selection> isIn(String... strings) {
    return table -> table.stringColumn(name()).isIn(strings);
  }

  public Function<Table, Selection> isIn(Collection<String> strings) {
    return table -> table.stringColumn(name()).isIn(strings);
  }

  public Function<Table, Selection> isNotIn(String... strings) {
    return table -> table.stringColumn(name()).isNotIn(strings);
  }

  public Function<Table, Selection> isNotIn(Collection<String> strings) {
    return table -> table.stringColumn(name()).isNotIn(strings);
  }

  public Function<Table, Selection> isEqualTo(Column<String> other) {
    return table -> table.stringColumn(name()).isEqualTo(other);
  }

  public Function<Table, Selection> isNotEqualTo(Column<String> other) {
    return table -> table.stringColumn(name()).isNotEqualTo(other);
  }

  public Function<Table, Selection> equalsIgnoreCase(Column<String> other) {
    return table -> table.stringColumn(name()).equalsIgnoreCase(other);
  }

  public Function<Table, Selection> startsWith(Column<String> other) {
    return table -> table.stringColumn(name()).startsWith(other);
  }

  public Function<Table, Selection> isMissing() {
    return table -> table.stringColumn(name()).isMissing();
  }

  public Function<Table, Selection> isNotMissing() {
    return table -> table.stringColumn(name()).isNotMissing();
  }

  public Function<Table, Selection> isEqualTo(String string) {
    return table -> table.stringColumn(name()).isEqualTo(string);
  }

  public Function<Table, Selection> isNotEqualTo(String string) {
    return table -> table.stringColumn(name()).isNotEqualTo(string);
  }
}
