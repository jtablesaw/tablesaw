package tech.tablesaw.columns.dateranges;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

class DateRangeTest {

  @Test
  void parse() {
    DateRange dateRange =
        DateRange.parse("2010-04-20/2010-05-11", "/", DateTimeFormatter.ISO_LOCAL_DATE);
    assertEquals(dateRange, new DateRange(LocalDate.of(2010, 4, 20), LocalDate.of(2010, 5, 11)));
  }

  @Test
  void getFrom() {}

  @Test
  void getTo() {}

  @Test
  void testToString() {}
}
