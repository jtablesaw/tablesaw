package tech.tablesaw.columns.strings;

/** A type indicator for a column "backing" a StringColumn */
public enum StringDataType {
  // A dictionary encoded representation for categorical data
  CATEGORICAL,
  // A List<String> representation for non-categorical strings
  TEXTUAL,
}
