package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.io.CsvReader;
import org.junit.Before;
import org.junit.Test;

import static com.deathrayresearch.outlier.columns.ColumnType.CAT;
import static com.deathrayresearch.outlier.columns.ColumnType.INTEGER;
import static com.deathrayresearch.outlier.columns.ColumnType.LOCAL_DATE;

/**
 *
 */
public class ColumnTest {

    ColumnType[] types = {
            LOCAL_DATE,     // date of poll
            INTEGER,        // approval rating (pct)
            CAT             // polling org
    };

    Table table;

    @Before
    public void setUp() throws Exception {
        table = CsvReader.read("data/BushApproval.csv", types);
    }


    @Test
    public void testHead() throws Exception {
        System.out.println(table.column(0).head(5).print());
        System.out.println(table.column(1).head(5).print());
        System.out.println(table.column(2).head(5).print());
    }

    @Test
    public void testTail() throws Exception {
        System.out.println(table.column(0).tail(5).print());
        System.out.println(table.column(1).tail(5).print());
        System.out.println(table.column(2).tail(5).print());
    }
}
