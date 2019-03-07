package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.tablesaw.columns.dates.PackedLocalDate.asLocalDate;
import static tech.tablesaw.columns.dates.PackedLocalDate.pack;

import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DateColumnMapTest {
    private DateColumn column1;

    @BeforeEach
    public void setUp() {
        Table table = Table.create("Test");
        column1 = DateColumn.create("Game date");
        table.addColumns(column1);
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
    public void testTimeWindow() {
        int day1 = pack(2011, 12,31);
        int day2 = pack(2012, 1,1);
        int day3 = pack(2012, 1,9);
        int day4 = pack(2012, 1,15);
        int day5 = pack(2012, 2,15);

        column1.appendInternal(day1);
        column1.appendInternal(day2);
        column1.appendInternal(day3);
        column1.appendInternal(day4);
        column1.appendInternal(day5);

        IntColumn group = column1.timeWindow(ChronoUnit.DAYS, 7);
        assertEquals(0, group.getInt(0));
        assertEquals(0, group.getInt(1));
        assertEquals(1, group.getInt(2));
        assertEquals(2, group.getInt(3));

        IntColumn group2 = column1.timeWindow(ChronoUnit.WEEKS, 1);
        assertEquals(0, group2.getInt(0));
        assertEquals(0, group2.getInt(1));
        assertEquals(1, group2.getInt(2));
        assertEquals(2, group2.getInt(3));

        IntColumn group3 = column1.timeWindow(ChronoUnit.MONTHS, 1);
        assertEquals(0, group3.getInt(0));
        assertEquals(0, group3.getInt(1));
        assertEquals(0, group3.getInt(2));
        assertEquals(0, group3.getInt(3));
        assertEquals(1, group3.getInt(4));
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
        IntColumn days = column1.daysUntil(column2);
        IntColumn weeks = column1.weeksUntil(column2);
        IntColumn months = column1.monthsUntil(column2);
        IntColumn years = column1.yearsUntil(column2);
        assertEquals(asLocalDate(day1).until(asLocalDate(day2), ChronoUnit.DAYS), days.getInt(0));
        assertEquals(asLocalDate(day1).until(asLocalDate(day2), ChronoUnit.WEEKS), weeks.getInt(0));
        assertEquals(asLocalDate(day1).until(asLocalDate(day2), ChronoUnit.MONTHS), months.getInt(0));
        assertEquals(asLocalDate(day1).until(asLocalDate(day2), ChronoUnit.YEARS), years.getInt(0));
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
        assertEquals(pack(2011, 12,21), column1.plusDays(2).getPackedDate(0));
        assertEquals(pack(2012, 1,3), column1.plusDays(2).getPackedDate(1));
        assertEquals(pack(2011, 12,30), column1.minusDays(2).getPackedDate(1));

        // plus weeks
        assertEquals(pack(2012, 1,8), column1.plusWeeks(1).getPackedDate(1));
        assertEquals(pack(2012, 1,3), column1.minusWeeks(1).getPackedDate(2));

        // plus months
        assertEquals(pack(2012, 2,19), column1.plusMonths(2).getPackedDate(0));
        assertEquals(pack(2012, 3,1), column1.plusMonths(2).getPackedDate(1));
        assertEquals(pack(2011, 11,1), column1.minusMonths(2).getPackedDate(1));

        // plus years
        assertEquals(pack(2013, 12,19), column1.plusYears(2).getPackedDate(0));
        assertEquals(pack(2015, 1,1), column1.plusYears(3).getPackedDate(1));
        assertEquals(pack(2011, 1,1), column1.minusYears(1).getPackedDate(1));
    }
}
