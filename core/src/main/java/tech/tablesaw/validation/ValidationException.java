package tech.tablesaw.validation;

/** Indicates that validation failed */
public class ValidationException extends RuntimeException {
  private final String columnName;
  private final String validatorName;
  private final int columnIndex;
  private final String value;

  public ValidationException(
      String columnName, String validatorName, int columnIndex, String value) {
    super(
        "A ValidationException occurred: {"
            + "columnName='"
            + columnName
            + "'"
            + ", validatorName='"
            + validatorName
            + "'"
            + ", columnIndex="
            + columnIndex
            + ", value='"
            + value
            + "'}");
    this.columnName = columnName;
    this.validatorName = validatorName;
    this.columnIndex = columnIndex;
    this.value = value;
  }

  @Override
  public String toString() {
    return "ValidationException{"
        + "columnName='"
        + columnName
        + "'"
        + ", validatorName='"
        + validatorName
        + "'"
        + ", columnIndex="
        + columnIndex
        + ", value='"
        + value
        + "'}";
  }
}
