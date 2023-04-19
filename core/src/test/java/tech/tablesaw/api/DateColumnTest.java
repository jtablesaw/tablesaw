package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.dates.DateColumnType;
import tech.tablesaw.columns.dates.DateParser;
import tech.tablesaw.columns.datetimes.DateTimeParser;

public class DateColumnTest {
  private DateColumn column1;

  @BeforeEach
  public void setUp() {
    Table table = Table.create("Test");
    column1 = DateColumn.create("Game date");
    table.addColumns(column1);
  }

  @Test
  public void testCreateMissingValue() {
    LocalDate[] dates = new LocalDate[5];
    DateColumn column = DateColumn.create("Game date", dates);
    assertEquals(DateColumnType.missingValueIndicator(), column.getIntInternal(0));
  }

  @Test
  public void testAddCell() {
    column1.appendCell("2013-10-23");
    column1.appendCell("12/23/1924");
    column1.appendCell("12-May-2015");
    column1.appendCell("12-Jan-2015");
    assertEquals(4, column1.size());
    LocalDate date = LocalDate.now();
    column1.append(date);
    assertEquals(5, column1.size());
  }

  @Test
  public void testCustomParser() {
    // Just do enough to ensure the parser is wired up correctly
    DateParser customParser = new DateParser(ColumnType.LOCAL_DATE);
    customParser.setMissingValueStrings(Arrays.asList("not here"));
    column1.setParser(customParser);

    column1.appendCell("not here");
    assertTrue(column1.isMissing(column1.size() - 1));
    column1.appendCell("2013-10-23");
    assertFalse(column1.isMissing(column1.size() - 1));
  }

  @Test
  public void testPrint() {
    column1.appendCell("2013-10-23");
    column1.appendCell("12/23/1924");
    column1.appendCell("12-May-2015");
    column1.appendCell("12-Jan-2015");
    column1.setPrintFormatter(DateTimeParser.caseInsensitiveFormatter("MMM~dd~yyyy"), "");
    assertEquals(
        "Column: Game date"
            + System.lineSeparator()
            + "Oct~23~2013"
            + System.lineSeparator()
            + "Dec~23~1924"
            + System.lineSeparator()
            + "May~12~2015"
            + System.lineSeparator()
            + "Jan~12~2015"
            + System.lineSeparator(),
        column1.print());
  }

  @Test
  public void testPrint2() {
    column1.appendCell("2013-10-23");
    column1.appendCell("12/23/1924");
    column1.appendCell("12-May-2015");
    column1.appendCell("12-Jan-2015");
    column1.setPrintFormatter(DateTimeParser.caseInsensitiveFormatter("MMM~dd~yyyy"));
    assertEquals(
        "Column: Game date"
            + System.lineSeparator()
            + "Oct~23~2013"
            + System.lineSeparator()
            + "Dec~23~1924"
            + System.lineSeparator()
            + "May~12~2015"
            + System.lineSeparator()
            + "Jan~12~2015"
            + System.lineSeparator(),
        column1.print());
  }

  @Test
  public void testDayOfMonth() {
    column1.appendCell("2013-10-23");
    column1.appendCell("12/24/1924");
    column1.appendCell("12-May-2015");
    column1.appendCell("14-Jan-2015");
    IntColumn c2 = column1.dayOfMonth();
    assertEquals(23, c2.get(0), 0.0001);
    assertEquals(24, c2.get(1), 0.0001);
    assertEquals(12, c2.get(2), 0.0001);
    assertEquals(14, c2.get(3), 0.0001);
  }

  @Test
  public void testMonth() {
    column1.appendCell("2013-10-23");
    column1.appendCell("12/24/1924");
    column1.appendCell("12-May-2015");
    column1.appendCell("14-Jan-2015");
    IntColumn c2 = column1.monthValue();
    assertEquals(10, c2.get(0), 0.0001);
    assertEquals(12, c2.get(1), 0.0001);
    assertEquals(5, c2.get(2), 0.0001);
    assertEquals(1, c2.get(3), 0.0001);
  }

  @Test
  public void testYearMonthString() {
    column1.appendCell("2013-10-23");
    column1.appendCell("12/24/1924");
    column1.appendCell("12-May-2015");
    column1.appendCell("14-Jan-2015");
    StringColumn c2 = column1.yearMonth();
    assertEquals("2013-10", c2.get(0));
    assertEquals("1924-12", c2.get(1));
    assertEquals("2015-05", c2.get(2));
    assertEquals("2015-01", c2.get(3));
  }

  @Test
  public void testYear() {
    column1.appendCell("2013-10-23");
    column1.appendCell("12/24/1924");
    column1.appendCell("12-May-2015");
    IntColumn c2 = column1.year();
    assertEquals(2013, c2.get(0), 0.0001);
    assertEquals(1924, c2.get(1), 0.0001);
    assertEquals(2015, c2.get(2), 0.0001);
  }

  @Test
  public void testSummary() {
    column1.appendCell("2013-10-23");
    column1.appendCell("12/24/1924");
    column1.appendCell("12-May-2015");
    column1.appendCell("14-Jan-2015");
    Table summary = column1.summary();
    assertEquals(4, summary.rowCount());
    assertEquals(2, summary.columnCount());
    assertEquals("Measure", summary.column(0).name());
    assertEquals("Value", summary.column(1).name());
  }

  @Test
  public void testMin() {
    column1.appendInternal(DateColumnType.missingValueIndicator());
    column1.appendCell("2013-10-23");

    LocalDate actual = column1.min();

    assertEquals(DateColumnType.DEFAULT_PARSER.parse("2013-10-23"), actual);
  }

  @Test
  public void testSortOn() {
    Table unsorted =
        Table.read()
            .csv(
                "Date,1 Yr Treasury Rate"
                    + System.lineSeparator()
                    + "\"01-01-1871\",4.44%"
                    + System.lineSeparator()
                    + "\"01-01-1920\",8.83%"
                    + System.lineSeparator()
                    + "\"01-01-1921\",7.11%"
                    + System.lineSeparator()
                    + "\"01-01-1919\",7.85%",
                "1 Yr Treasury Rate");
    Table sorted = unsorted.sortOn("Date");
    assertEquals(
        sorted.dateColumn("Date").asList().stream().sorted().collect(Collectors.toList()),
        sorted.dateColumn("Date").asList());
  }

  @Test
  public void testCountUnique() {
    column1.append(LocalDate.of(2000, 1, 1));
    column1.append(LocalDate.of(2000, 1, 1));
    column1.append(LocalDate.of(2000, 2, 1));
    column1.appendMissing();

    assertEquals(3, column1.countUnique());
  }
}
