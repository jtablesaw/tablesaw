/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.io;

import java.io.PrintStream;
import java.util.List;

/**
 * This Exception wraps another Exception thrown while adding a cell to a column.
 *
 * <p>The methods of this exception allow the causing Exception, row number, column index,
 * columnNames and line to be retrieved.
 *
 * <p>The dumpRow method allows the row in question to be printed to a a PrintStream such as
 * System.out
 */
public class AddCellToColumnException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /** The index of the column that threw the Exception */
  private final int columnIndex;

  /** The number of the row that caused the exception to be thrown */
  private final long rowNumber;

  /** The column names stored as an array */
  private final List<String> columnNames;

  /** The original line that caused the Exception */
  private final String[] line;

  /**
   * Creates a new instance of this Exception
   *
   * @param e The Exception that caused adding to fail
   * @param columnIndex The index of the column that threw the Exception
   * @param rowNumber The number of the row that caused the Exception to be thrown
   * @param columnNames The column names stored as an array
   * @param line The original line that caused the Exception
   */
  public AddCellToColumnException(
      Exception e, int columnIndex, long rowNumber, List<String> columnNames, String[] line) {
    super(
        "Error while adding cell from row "
            + rowNumber
            + " and column "
            + columnNames.get(columnIndex)
            + ""
            + "(position:"
            + columnIndex
            + "): "
            + e.getMessage(),
        e);
    this.columnIndex = columnIndex;
    this.rowNumber = rowNumber;
    this.columnNames = columnNames;
    this.line = line;
  }

  /** Returns the index of the column that threw the Exception */
  public int getColumnIndex() {
    return columnIndex;
  }

  /** Returns the number of the row that caused the Exception to be thrown */
  public long getRowNumber() {
    return rowNumber;
  }

  /** Returns the column names array */
  public List<String> getColumnNames() {
    return columnNames;
  }

  /** Returns the name of the column that caused the Exception */
  public String getColumnName() {
    return columnNames.get(columnIndex);
  }

  /**
   * Dumps to a PrintStream the information relative to the row that caused the problem
   *
   * @param out The PrintStream to output to
   */
  public void dumpRow(PrintStream out) {
    for (int i = 0; i < columnNames.size(); i++) {
      out.print("Column ");
      out.print(i);
      out.print(" ");
      out.print(columnNames.get(columnIndex));
      out.print(" : ");
      try {
        out.println(line[i]);
      } catch (ArrayIndexOutOfBoundsException aioobe) {
        out.println("Unable to get cell " + i + " of this line");
      }
    }
  }
}
