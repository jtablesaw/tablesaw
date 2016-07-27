package com.github.lwhite1.tablesaw.io.csv;

import java.io.PrintStream;

/**
 * Created by Richard on 26/07/2016.
 */
public class AddCellToColumnException extends RuntimeException {

    private final int columnIndex;
    private final long rowNumber;
    private final String[] columnNames;
    private final String[] line;

    public AddCellToColumnException(Exception e, int columnIndex, long rowNumber, String[] columnNames, String[] line) {
        super("Error while addding cell from row "+rowNumber+" and column "+columnNames[columnIndex]+ "(position:"+columnIndex+"): "+e.getMessage(), e);
        this.columnIndex = columnIndex;
        this.rowNumber = rowNumber;
        this.columnNames = columnNames;
        this.line = line;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public long getRowNumber() {
        return rowNumber;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public String getColumnName() {
        return columnNames[columnIndex];
    }

    public void dumpRow(PrintStream out) {
        for (int i=0;i<columnNames.length;i++) {
            out.print("Column ");
            out.print(i);
            out.print(" ");
            out.print(columnNames[i]);
            out.print(" : ");
            try {
                out.println(line[i]);
            } catch (ArrayIndexOutOfBoundsException aioobe) {
                out.println("aioobe");
            }
        }
    }
}
