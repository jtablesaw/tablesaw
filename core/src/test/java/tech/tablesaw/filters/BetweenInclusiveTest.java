package tech.tablesaw.filters;

import org.junit.Test;
import tech.tablesaw.api.QueryHelper;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import static org.junit.Assert.assertEquals;

public class BetweenInclusiveTest {

    @Test
    public void apply() throws Exception {
        Table bush = Table.read().csv(CsvReadOptions.builder("../data/bush.csv"));

        Table result = bush.selectWhere(QueryHelper.numberColumn("approval").isBetweenInclusive(0, 49));
        assertEquals(10, result.rowCount());
    }
}