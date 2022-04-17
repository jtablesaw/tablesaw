package tech.tablesaw.validation;

import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

public interface Validator<T> {

  String name();

  Selection apply(Column<T> column);

  Validator<T> setName(String s);

  Validator<T> negate();
}
