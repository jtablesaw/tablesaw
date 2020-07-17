package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DateRangeColumnTest {

  private Table table1;

  @BeforeEach
  void setUp() throws Exception {
    table1 = Table.read().csv("../data/bush.csv");
    Random random = new Random();
    IntColumn days = IntColumn.create("days");
    DateColumn end = DateColumn.create("end");
    for (int i = 0; i < table1.rowCount(); i++) {
      days.append(random.nextInt(21));
      end.append(table1.dateColumn("date").get(i).plusDays(days.get(i)));
    }
    table1.column("date").setName("start");
    table1.addColumns(days, end);
    table1 = (Table) table1.removeColumns("approval");
  }

  @Test
  void instantiate() {
    DateRangeColumn range = table1.dateColumn("start").rangeTo(table1.dateColumn("end"));
    assertNotNull(range);
    table1.addColumns(range);
    System.out.println(table1.structure());
    System.out.println(table1);
  }

  @Test
  void leadAndLag() {
    DateRangeColumn range = table1.dateColumn("start").rangeTo(table1.dateColumn("end"));
    DateRangeColumn lead = range.lead(1);
    DateRangeColumn lag = range.lag(1);
    table1.addColumns(range, lead, lag);
    assertEquals(range.get(2), lead.get(1));
    assertEquals(range.get(2), lag.get(3));
  }
}
