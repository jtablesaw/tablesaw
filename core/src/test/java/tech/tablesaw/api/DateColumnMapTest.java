package tech.tablesaw.api;

import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import static tech.tablesaw.columns.dates.PackedLocalDate.asLocalDate;
import static tech.tablesaw.columns.dates.PackedLocalDate.pack;
import static org.junit.Assert.assertEquals;

public class DateColumnMapTest {
    private DateColumn column1;

    @Before
    public void setUp() {
        Table table = Table.create("Test");
        column1 = DateColumn.create("Game date", Locale.ENGLISH);
        table.addColumn(column1);
    }

    @Test
    public void testGetDayOfYear() {
        int day1 = pack(2011, 12,31);
        int day2 = pack(2012, 1,1);

        column1.appendInternal(day1);
        column1.appendInternal(day2);

        assertEquals(365, column1.dayOfYear().get(0), 0.001);
        assertEquals(1, column1.dayOfYear().get(1), 0.001);
    }

    @Test
    public void testDayOfWeek() {
        int day1 = pack(2018, 3, 30);
        column1.appendInternal(day1);
        assertEquals(DayOfWeek.FRIDAY.name(), column1.dayOfWeek().get(0));
        assertEquals(DayOfWeek.FRIDAY.getValue(), column1.dayOfWeekValue().get(0), 0.01);
    }

    @Test
    public void testDifference() {
        int day1 = pack(2018, 3, 30);
        column1.appendInternal(day1);
        int day2 = pack(2018, 11, 23);
        DateColumn column2 = DateColumn.create("foo");
        column2.appendInternal(day2);
        NumberColumn days = column1.daysUntil(column2);
        NumberColumn weeks = column1.weeksUntil(column2);
        NumberColumn months = column1.monthsUntil(column2);
        NumberColumn years = column1.yearsUntil(column2);
        assertEquals(asLocalDate(day1).until(asLocalDate(day2), ChronoUnit.DAYS), days.get(0), 0.01);
        assertEquals(asLocalDate(day1).until(asLocalDate(day2), ChronoUnit.WEEKS), weeks.get(0), 0.01);
        assertEquals(asLocalDate(day1).until(asLocalDate(day2), ChronoUnit.MONTHS), months.get(0), 0.01);
        assertEquals(asLocalDate(day1).until(asLocalDate(day2), ChronoUnit.YEARS), years.get(0), 0.01);
    }

        @Test
    public void testPlus() {
        int day1 = pack(2011, 12,19);
        int day2 = pack(2012, 1,1);
        int day3 = pack(2012, 1,10);

        column1.appendInternal(day1);
        column1.appendInternal(day2);
        column1.appendInternal(day3);
        // plus days
        assertEquals(pack(2011, 12,21), column1.plusDays(2).getPackedDate(0), 0.001);
        assertEquals(pack(2012, 1,3), column1.plusDays(2).getPackedDate(1), 0.001);
        assertEquals(pack(2011, 12,30), column1.minusDays(2).getPackedDate(1), 0.001);

        // plus weeks
        assertEquals(pack(2012, 1,8), column1.plusWeeks(1).getPackedDate(1), 0.001);
        assertEquals(pack(2012, 1,3), column1.minusWeeks(1).getPackedDate(2), 0.001);

        // plus months
        assertEquals(pack(2012, 2,19), column1.plusMonths(2).getPackedDate(0), 0.001);
        assertEquals(pack(2012, 3,1), column1.plusMonths(2).getPackedDate(1), 0.001);
        assertEquals(pack(2011, 11,1), column1.minusMonths(2).getPackedDate(1), 0.001);

        // plus years
        assertEquals(pack(2013, 12,19), column1.plusYears(2).getPackedDate(0), 0.001);
        assertEquals(pack(2015, 1,1), column1.plusYears(3).getPackedDate(1), 0.001);
        assertEquals(pack(2011, 1,1), column1.minusYears(1).getPackedDate(1), 0.001);
    }
}
