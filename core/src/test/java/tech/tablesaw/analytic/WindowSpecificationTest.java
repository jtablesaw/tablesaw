package tech.tablesaw.analytic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import tech.tablesaw.analytic.AnalyticQuery.Order;
import tech.tablesaw.analytic.WindowSpecification.OrderPair;

class WindowSpecificationTest {

  @Test
  public void testToSqlString() {
    WindowSpecification windowSpecification = WindowSpecification.builder()
      .setWindowName("mainWindow")
      .setOrderColumns(
        ImmutableList.of(
          OrderPair.of("col1", Order.ASC),
          OrderPair.of("col2", Order.DESC)
          )
      )
      .setPartitionColumns(
        ImmutableList.of("col1", "col2")
      )
      .build();

    String expected = "PARTITION BY col1, col2"
      + System.lineSeparator()
      + "ORDER BY col1 ASC, col2 DESC";

    assertEquals(expected, windowSpecification.toSqlString());
  }

  @Test
  public void orderDuplicates() {
    Throwable thrown = assertThrows(IllegalArgumentException.class, () -> WindowSpecification.builder()
      .setOrderColumns(
        ImmutableList.of(
          OrderPair.of("col1", Order.ASC),
          OrderPair.of("col1", Order.DESC)
        )
      ).build());

    assertTrue(thrown.getMessage().contains("duplicate columns"));
  }

  @Test
  public void partitionDuplicates() {
    Throwable thrown = assertThrows(IllegalArgumentException.class, () -> WindowSpecification.builder()
      .setPartitionColumns(
        ImmutableList.of("col1", "col1")
      ).build());

    assertTrue(thrown.getMessage().contains("duplicate columns"));

  }

}