package com.github.lwhite1.tablesaw;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.io.CsvReader;

import java.io.IOException;

import static com.github.lwhite1.tablesaw.api.ColumnType.CATEGORY;
import static com.github.lwhite1.tablesaw.api.ColumnType.FLOAT;
import static com.github.lwhite1.tablesaw.api.ColumnType.INTEGER;
import static com.github.lwhite1.tablesaw.api.ColumnType.LOCAL_DATE;
import static com.github.lwhite1.tablesaw.api.ColumnType.LOCAL_TIME;

/**
 * This class setup tablesaw Relation from test data sources.
 * It purpose is to make easy for tests or example code get data to work with.
 */
public enum TestData {

    BUSH_APPROVAL(new ColumnType[]{LOCAL_DATE, INTEGER, CATEGORY}, "data/BushApproval.csv"),

    TORNADOES(
            new ColumnType[]{LOCAL_DATE, LOCAL_TIME, CATEGORY, INTEGER, INTEGER, INTEGER, INTEGER, FLOAT, FLOAT, FLOAT,
                    FLOAT}, "data/1950-2014_torn.csv");


    private Table table;
    private ColumnType[] columnTypes;

    /**
     * Creates a Relation from the specified daa.
     *
     * @param columnTypes the data in each column of the specified CSV
     * @param csvSource the CSV data
     */
    TestData(ColumnType[] columnTypes, String csvSource) {
        try {
            this.table = CsvReader.read(columnTypes, "data/BushApproval.csv");
            this.columnTypes = columnTypes;
        } catch (IOException e) {
            throw new IllegalStateException("IO error creating tablesaw from: " + csvSource, e);
        }
    }

    /**
     * @return The TableSaw instance for a specific data set
     */
    public Table getTable() {
        return table;
    }

    /**
     *
     * @return the column types for the data set.
     */
    public ColumnType[] getColumnTypes() {
        return columnTypes;
    }

}
