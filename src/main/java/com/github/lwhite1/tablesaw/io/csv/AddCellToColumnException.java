package com.github.lwhite1.tablesaw.io.csv;

/**
 * Created by Richard on 26/07/2016.
 */
public class AddCellToColumnException extends RuntimeException {

    private final int columnIndex;
    private final long rowNumber;
    public AddCellToColumnException(Exception e, int columnIndex, long rowNumber) {
        super(e);
        this.columnIndex = columnIndex;
        this.rowNumber = rowNumber;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public long getRowNumber() {
        return rowNumber;
    }
}
