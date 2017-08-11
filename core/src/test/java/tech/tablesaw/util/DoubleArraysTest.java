package tech.tablesaw.util;

import org.junit.Test;

import tech.tablesaw.api.Table;
import tech.tablesaw.table.ViewGroup;
import tech.tablesaw.util.DoubleArrays;

/**
 *
 */
public class DoubleArraysTest {

    @Test
    public void testTo2dArray() throws Exception {
        Table table = Table.createFromCsv("../data/tornadoes_1950-2014.csv");
        ViewGroup viewGroup = table.splitOn(table.shortColumn("Scale"));
        int columnNuumber = table.columnIndex("Injuries");
        double[][] results = DoubleArrays.to2dArray(viewGroup, columnNuumber);
    }

}