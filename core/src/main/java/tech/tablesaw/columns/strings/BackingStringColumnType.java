package tech.tablesaw.columns.strings;

/** A type indicator for a column "backing" a StringColumn */
public enum BackingStringColumnType {
  // A dictionary encoded representation for categorical data
  BACKING_STRING,
  // A List<String> representation for non-categorical strings
  BACKING_TEXT,
  // For legacy TextColumn
  NONE
}
