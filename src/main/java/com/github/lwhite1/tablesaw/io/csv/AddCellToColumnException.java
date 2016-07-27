package com.github.lwhite1.tablesaw.io.csv;

/**
 * Created by Richard on 26/07/2016.
 */
public class AddCellToColumnException extends RuntimeException {

    private final int columnIndex;
    private final long rowNumber;
    private final String columnName;
    public AddCellToColumnException(Exception e, String columnName, int columnIndex, long rowNumber) {
        super("Error while addding cell at from row "+rowNumber+" and column "+columnName+ "(position:"+columnIndex+"): "+e.getMessage(), e);
        this.columnIndex = columnIndex;
        this.rowNumber = rowNumber;
        this.columnName = columnName;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public long getRowNumber() {
        return rowNumber;
    }

    public String getColumnName() {
        return columnName;
    }
}
