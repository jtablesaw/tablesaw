package tech.tablesaw.validation;

import java.util.function.BiPredicate;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.strings.StringPredicates;
import tech.tablesaw.selection.Selection;

public class StringStringValidator extends AbstractValidator<String> {

  // standard validators
  public static final StringStringValidator endsWith =
      new StringStringValidator("Values end with", StringPredicates.endsWith);

  /** A BiPredicate that defines how to match each string in the column to a given value */
  private BiPredicate<String, String> predicate;

  /** The value to match strings against, according to the rules defined in the predicate */
  String value;

  public StringStringValidator(String name, BiPredicate<String, String> predicate, String value) {
    super(name.endsWith(" ") ? name + "'" + value + "'" : name + " '" + value + "'");
    this.predicate = predicate;
    this.value = value;
  }

  public StringStringValidator(String name, BiPredicate<String, String> predicate) {
    super(name);
    this.predicate = predicate;
  }

  /** Sets the value to compare against and returns this validator */
  public StringStringValidator value(String value) {
    this.value = value;
    super.setName(name().endsWith(" ") ? name() + "'" + value + "'" : name() + " '" + value + "'");
    return this;
  }

  Selection validate(StringColumn column) {
    return column.eval(predicate, value);
  }

  /** {@inheritDoc} */
  @Override
  public Selection apply(Column<String> column) {
    return ((StringColumn) column).eval(predicate.negate(), value);
  }

  /** {@inheritDoc} */
  @Override
  public Validator<String> negate() {
    predicate = predicate.negate();
    return this;
  }
}
