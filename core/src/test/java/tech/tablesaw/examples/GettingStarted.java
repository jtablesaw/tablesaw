package tech.tablesaw.examples;

import static tech.tablesaw.api.ColumnType.*;

import org.junit.Before;
import org.junit.Test;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReader;

/**
 * Basic example code
 */
public class GettingStarted {

    private ColumnType[] types = {
            LOCAL_DATE,     // date of poll
            INTEGER,        // approval rating (pct)
            CATEGORY        // polling org
    };

    private Table table;

    @Before
    public void setUp() throws Exception {
        table = CsvReader.read(types, "../data/BushApproval.csv");
    }

    @Test
    public void printStructure() throws Exception {
        out(table.structure());

        out(table.first(10));

        out(table.summary());

        out(table.columnNames());

        Column approval = table.column("approval");
        out(approval.summary());

        Column who = table.column("who");
        out(who.summary());

        Column date = table.column("date");
        out(date.summary());
    }

    private synchronized void out(Object obj) {
        System.out.println(String.valueOf(obj));
    }

}
