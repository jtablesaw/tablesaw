package tech.tablesaw.validation;

import java.util.function.Predicate;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

public class StringValidator extends AbstractValidator<String> {

  private Predicate<String> predicate;

  public StringValidator(String name, Predicate<String> predicate) {
    super(name);
    this.predicate = predicate;
  }

  Selection validate(StringColumn column) {
    return column.eval(predicate);
  }

  @Override
  public Selection apply(Column<String> column) {
    return ((StringColumn) column).eval(predicate.negate());
  }

  /** {@inheritDoc} */
  @Override
  public StringValidator negate() {
    predicate = predicate.negate();
    return this;
  }
}
