package tech.tablesaw.filtering;

import org.junit.Test;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.QueryHelper;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import static org.junit.Assert.assertEquals;

public class IntBetweenInclusiveTest {

    @Test
    public void apply() throws Exception {
        Table bush = Table.read().csv(CsvReadOptions.builder("../data/BushApproval.csv"));
        bush.addColumn((IntColumn) bush.shortColumn("approval").add(0));
        Table result = bush.selectWhere(QueryHelper.column("approval + 0").isBetweenIncluding(0, 49));
        assertEquals(10, result.rowCount());
    }
}