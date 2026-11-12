package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class FloatListColumnVectorSortTest {

  @Test
  void tableSortUsesLexicographicVectorOrdering() {
    Table table =
        Table.create("vectors")
            .addColumns(
                IntColumn.create("id", 0, 1, 2, 3, 4, 5),
                FloatListColumn.create(
                    "vector",
                    Arrays.asList(
                        Arrays.asList(0.5f, 0.1f),
                        Arrays.asList(0.5f, 0.9f),
                        Arrays.asList(0.4f, 9.0f),
                        Arrays.asList(0.6f, 0.0f),
                        List.of(),
                        null)));

    assertArrayEquals(new int[] {4, 2, 0, 1, 3, 5}, table.sortAscendingOn("vector").intColumn("id").asIntArray());
    assertArrayEquals(new int[] {5, 3, 1, 0, 2, 4}, table.sortDescendingOn("vector").intColumn("id").asIntArray());
  }
}
