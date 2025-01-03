package tech.tablesaw.aggregate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import java.util.List;

public class PivotTableTest {

  /**
   * Illustrate usage of pivot function with a single grouping, pivot and aggregated columns
   * @throws Exception
   */
  @Test
  public void pivotSingle() throws Exception {
    Table t =
        Table.read()
            .csv(CsvReadOptions.builder("../data/bush.csv").missingValueIndicator(":").build());
    t.addColumns(t.dateColumn("date").year());

    Table pivot =
        PivotTable.pivot(
            t,
            t.categoricalColumn("who"),
            t.categoricalColumn("date year"),
            t.numberColumn("approval"),
            AggregateFunctions.mean);
    assertTrue(pivot.columnNames().contains("who"));
    assertTrue(pivot.columnNames().contains("2001"));
    assertTrue(pivot.columnNames().contains("2002"));
    assertTrue(pivot.columnNames().contains("2003"));
    assertTrue(pivot.columnNames().contains("2004"));
    assertEquals(6, pivot.rowCount());
  }
  

  @Test
  public void pivotMultipleGroupAndAggregate() throws Exception {
    Table t =
        Table.read()
            .csv(CsvReadOptions.builder("../data/baseball.csv").build());

    Table pivot =
        t.pivot(
            List.of("Team","League"),
            "Year",
            List.of("RS","RA","W"),
            AggregateFunctions.mean);

    assertTrue(pivot.columnNames().contains("Team"));
    assertTrue(pivot.columnNames().contains("League"));
    assertTrue(pivot.columnNames().contains("2001.RS"));
    assertTrue(pivot.columnNames().contains("2001.RA"));
    assertTrue(pivot.columnNames().contains("2001.W"));
    assertEquals(143, pivot.columnCount());
    assertEquals(40, pivot.rowCount());
  }

  @Test
  public void pivotMultipleGroup() throws Exception {
    Table t =
        Table.read()
            .csv(CsvReadOptions.builder("../data/baseball.csv").build());

    Table pivot =
        t.pivot(
            List.of("Team","League"),
            "Year",
            List.of("RS"),
            AggregateFunctions.mean);

    assertTrue(pivot.columnNames().contains("Team"));
    assertTrue(pivot.columnNames().contains("League"));
    assertTrue(pivot.columnNames().contains("2001"));
    assertTrue(pivot.columnNames().contains("2002"));
    assertTrue(pivot.columnNames().contains("2003"));
    assertEquals(49, pivot.columnCount());
    assertEquals(40, pivot.rowCount());
  }

  @Test
  public void pivotMultipleAggregate() throws Exception {
    Table t =
        Table.read()
            .csv(CsvReadOptions.builder("../data/baseball.csv").build());

    Table pivot =
        t.pivot(
            List.of("League"),
            "Year",
            List.of("RS","RA","W"),
            AggregateFunctions.mean);

    assertTrue(!pivot.columnNames().contains("Team"));
    assertTrue(pivot.columnNames().contains("League"));
    assertTrue(pivot.columnNames().contains("2001.RS"));
    assertTrue(pivot.columnNames().contains("2001.RA"));
    assertTrue(pivot.columnNames().contains("2001.W"));
    assertEquals(142, pivot.columnCount());
    assertEquals(2, pivot.rowCount());
  }

}
