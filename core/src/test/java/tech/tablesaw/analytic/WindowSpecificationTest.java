package tech.tablesaw.analytic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import tech.tablesaw.sorting.Sort;

class WindowSpecificationTest {

  @Test
  public void testToSqlString() {
    WindowSpecification windowSpecification =
        WindowSpecification.builder()
            .setWindowName("mainWindow")
            .setSort(Sort.on("col1", Sort.Order.ASCEND).next("col2", Sort.Order.DESCEND))
            .setPartitionColumns(ImmutableList.of("col1", "col2"))
            .build();

    String expected =
        "PARTITION BY col1, col2" + System.lineSeparator() + "ORDER BY col1 ASC, col2 DESC";

    assertEquals(expected, windowSpecification.toSqlString());
  }

  @Test
  public void partitionDuplicates() {
    Throwable thrown =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                WindowSpecification.builder()
                    .setPartitionColumns(ImmutableList.of("col1", "col1"))
                    .build());

    assertTrue(thrown.getMessage().contains("duplicate columns"));
  }
}
