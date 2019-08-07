package tech.tablesaw.filtering.deferred;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.BitmapBackedSelection;

import static org.junit.jupiter.api.Assertions.*;
import static tech.tablesaw.filtering.deferred.Q.*;

class DeferredColumnTest {

  @Test
  void testExecution() throws Exception {
      Table table = Table.read().csv("../data/Bush.csv");
      BooleanColumn b = BooleanColumn.create("test", new BitmapBackedSelection().addRange(0, table.rowCount()), table.rowCount()).setName("b");
      assertTrue(b.get(0));
      table.addColumns(b);
      Table t = table.where(booleanColumn("b").isTrue());
      assertEquals(table.rowCount(), t.rowCount());

      t = table.where(stringColumn("who").isNotEqualTo("fox"));
      assertNotEquals(t.stringColumn("who").get(10), "fox");

      t = table.where(numberColumn("approval").isLessThan(55));
      assertTrue(t.intColumn("approval").get(10) < 55);

      t = table.where(dateColumn("date").isInApril());
      assertEquals(4, t.dateColumn("date").get(10).getMonthValue());

      t = table.where(not(dateColumn("date").isInApril()));
      assertFalse(t.dateColumn("date").monthValue().contains(4));

      t = table.where(dateColumn("date").isInApril());
      assertEquals(4, t.dateColumn("date").get(10).getMonthValue());
  }
}