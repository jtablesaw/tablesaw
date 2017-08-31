package tech.tablesaw.io.html;

import org.junit.Before;
import org.junit.Test;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.html.HtmlTableWriter;
import tech.tablesaw.reducing.NumericReduceUtils;
import tech.tablesaw.table.ViewGroup;

public class HtmlTableWriterTest {

    private static ColumnType[] types = {
            ColumnType.LOCAL_DATE,     // date of poll
            ColumnType.INTEGER,        // approval rating (pct)
            ColumnType.CATEGORY        // polling org
    };

    private Table table;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/BushApproval.csv").columnTypes(types));
    }

    @Test
    public void testWrite() {
        Column byColumn = table.column("who");
        ViewGroup group = new ViewGroup(table, byColumn);
        Table result = group.reduce("approval", NumericReduceUtils.mean);
        HtmlTableWriter.write(result);
    }

}
