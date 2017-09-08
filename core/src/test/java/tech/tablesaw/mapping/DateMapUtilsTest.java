package tech.tablesaw.mapping;

import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;

import java.time.LocalTime;
import java.util.Locale;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DateMapUtilsTest {

    private DateColumn column1;

    @Before
    public void setUp() throws Exception {
        Table table = Table.create("Test");
        column1 = new DateColumn("Game date");
        column1.setLocale(Locale.ENGLISH);
        table.addColumn(column1);
    }

    @Test
    public void testAtTimeColumn() throws Exception {
        column1.appendCell("2013-10-23");
        column1.appendCell("12/24/1924");
        column1.appendCell("12-May-2015");
        column1.appendCell("14-Jan-2015");

        TimeColumn timeColumn = new TimeColumn("times");
        timeColumn.append(LocalTime.NOON);
        timeColumn.append(LocalTime.NOON);
        timeColumn.append(LocalTime.NOON);
        timeColumn.append(LocalTime.NOON);
        DateTimeColumn dateTimes = column1.atTime(timeColumn);
        assertNotNull(dateTimes);
        assertTrue(dateTimes.get(0).toLocalTime().equals(LocalTime.NOON));
    }

    @Test
    public void testAtTime() throws Exception {
        column1.appendCell("2013-10-23");
        column1.appendCell("12/24/1924");
        column1.appendCell("12-May-2015");
        column1.appendCell("14-Jan-2015");

        DateTimeColumn dateTimes = column1.atTime(LocalTime.NOON);
        assertNotNull(dateTimes);
        assertTrue(dateTimes.get(0).toLocalTime().equals(LocalTime.NOON));
    }
}
