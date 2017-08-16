package tech.tablesaw.api;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static tech.tablesaw.api.QueryHelper.column;

import static org.junit.Assert.assertEquals;

public class DateTimeColumnTest {

    private DateTimeColumn column1;

    @Before
    public void setUp() throws Exception {
        Table table = Table.create("Test");
        column1 = new DateTimeColumn("Game date");
        table.addColumn(column1);
    }

    @Test
    public void testAddCell() throws Exception {
        column1.appendCell("1923-10-20T10:15:30");
        column1.appendCell("1924-12-10T10:15:30");
        column1.appendCell("2015-12-05T10:15:30");
        column1.appendCell("2015-12-20T10:15:30");
        assertEquals(4, column1.size());
        LocalDateTime date = LocalDateTime.now();
        column1.append(date);
        assertEquals(5, column1.size());
    }

    @Test
    public void testAfter() throws Exception {
        Table t = Table.create("test");
        t.addColumn(column1);
        column1.appendCell("2015-12-03T10:15:30");
        column1.appendCell("2015-01-03T10:15:30");
        Table result = t.selectWhere(column("Game date")
                .isAfter(LocalDateTime.of(2015, 2, 2, 0, 0)));
        assertEquals(result.rowCount(), 1);
    }

}
