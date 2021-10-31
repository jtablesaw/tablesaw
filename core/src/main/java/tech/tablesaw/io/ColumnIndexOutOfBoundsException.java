package tech.tablesaw.io;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * This Exception wraps another Exception thrown while adding a cell to a column.
 *
 * <p>The methods of this exception allow the causing Exception, row number, column index,
 * columnNames and line to be retrieved.
 *
 * <p>The dumpRow method allows the row in question to be printed to a a PrintStream such as
 * System.out
 */
public class ColumnIndexOutOfBoundsException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /** The number of the row that caused the exception to be thrown */
  private final long rowNumber;

  private final String[] line;

  public ColumnIndexOutOfBoundsException(
      IndexOutOfBoundsException e, long rowNumber, String[] line) {
    super(
        "An IndexOutOfBoundsException occurred while detecting column types from row "
            + rowNumber
            + " with values: "
            + Arrays.toString(line)
            + ": "
            + e.getMessage(),
        e);
    this.rowNumber = rowNumber;
    this.line = line;
  }

  /** Returns the number of the row that caused the Exception to be thrown */
  public long getRowNumber() {
    return rowNumber;
  }

  /** Returns the array of values in the row that caused the Exception as a comma-separated list */
  public String[] getLine() {
    return line;
  }

  /**
   * Dumps to a PrintStream the information relative to the row that caused the problem
   *
   * @param out The PrintStream to output to
   */
  public void dumpRow(PrintStream out) {
    out.println(Arrays.toString(line));
  }
}
