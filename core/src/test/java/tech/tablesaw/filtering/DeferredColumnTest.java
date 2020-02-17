package tech.tablesaw.filtering;

import static org.junit.jupiter.api.Assertions.*;
import static tech.tablesaw.api.QuerySupport.*;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.BitmapBackedSelection;

class DeferredColumnTest {

  @Test
  void testExecution() throws Exception {
    Table table = Table.read().csv("../data/bush.csv");
    BooleanColumn b =
        BooleanColumn.create(
                "test", new BitmapBackedSelection().addRange(0, table.rowCount()), table.rowCount())
            .setName("b");
    assertTrue(b.get(0));
    table.addColumns(b);
    Table t = table.where(booleanColumn("b").isTrue());
    assertEquals(table.rowCount(), t.rowCount());

    t = table.where(stringColumn("who").isNotEqualTo("fox"));
    assertNotEquals("fox", t.stringColumn("who").get(10));

    t = table.where(num("approval").isLessThan(55));
    assertTrue(t.intColumn("approval").get(10) < 55);

    t = table.where(date("date").isInApril());
    assertEquals(4, t.dateColumn("date").get(10).getMonthValue());

    t = table.where(not(dateColumn("date").isInApril()));
    assertFalse(t.dateColumn("date").monthValue().contains(4));

    t = table.where(date("date").isInApril());
    assertEquals(4, t.dateColumn("date").get(10).getMonthValue());
  }

  @Test
  void testLogicalOperators() {

    boolean[] aList = {true, false, true, false, true, false, true, false};
    boolean[] bList = {true, true, true, true, false, false, false, false};
    Table t =
        Table.create(
            "t",
            IntColumn.indexColumn("index", aList.length, 1),
            BooleanColumn.create("A", aList),
            BooleanColumn.create("B", bList));

    assertTrue(t.where(booleanColumn("A").isTrue()).intColumn(0).contains(1));
    assertTrue(t.where(booleanColumn("A").isTrue()).intColumn(0).contains(3));
    assertTrue(t.where(booleanColumn("A").isTrue()).intColumn(0).contains(5));
    assertTrue(t.where(booleanColumn("A").isTrue()).intColumn(0).contains(7));

    assertTrue(t.where(not(booleanColumn("A").isTrue())).intColumn(0).contains(2));
    assertTrue(t.where(not(booleanColumn("A").isTrue())).intColumn(0).contains(4));
    assertTrue(t.where(not(booleanColumn("A").isTrue())).intColumn(0).contains(6));
    assertTrue(t.where(not(booleanColumn("A").isTrue())).intColumn(0).contains(8));

    assertTrue(t.where(any(booleanColumn("A").isTrue())).intColumn(0).contains(1));
    assertTrue(t.where(any(booleanColumn("A").isTrue())).intColumn(0).contains(3));
    assertTrue(t.where(any(booleanColumn("A").isTrue())).intColumn(0).contains(5));
    assertTrue(t.where(any(booleanColumn("A").isTrue())).intColumn(0).contains(7));

    assertTrue(
        t.where(either(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(1));
    assertTrue(
        t.where(either(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(3));
    assertFalse(
        t.where(either(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(6));
    assertFalse(
        t.where(either(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(8));

    assertFalse(
        t.where(neither(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(1));
    assertFalse(
        t.where(neither(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(2));
    assertTrue(
        t.where(neither(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(6));
    assertTrue(
        t.where(neither(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(8));

    assertFalse(
        t.where(notAny(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(1));
    assertFalse(
        t.where(notAny(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(2));
    assertTrue(
        t.where(notAny(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(6));
    assertTrue(
        t.where(notAny(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(8));

    assertFalse(
        t.where(notBoth(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(1));
    assertTrue(
        t.where(notBoth(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(2));
    assertTrue(
        t.where(notBoth(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(6));
    assertTrue(
        t.where(notBoth(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(8));

    assertTrue(
        t.where(both(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(1));
    assertFalse(
        t.where(both(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(2));
    assertFalse(
        t.where(both(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(5));
    assertFalse(
        t.where(both(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(8));

    assertTrue(
        t.where(all(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(1));
    assertFalse(
        t.where(all(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(2));
    assertFalse(
        t.where(all(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(5));
    assertFalse(
        t.where(all(booleanColumn("A").isTrue(), booleanColumn("B").isTrue()))
            .intColumn(0)
            .contains(8));

    assertTrue(t.where(notAll(booleanColumn("A").isTrue())).intColumn(0).contains(2));
    assertTrue(t.where(notAll(booleanColumn("A").isTrue())).intColumn(0).contains(4));
    assertTrue(t.where(notAll(booleanColumn("A").isTrue())).intColumn(0).contains(6));
    assertTrue(t.where(notAll(booleanColumn("A").isTrue())).intColumn(0).contains(8));
  }
}
