/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tech.tablesaw.TableAssertions.assertTableEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import tech.tablesaw.analytic.AnalyticQuery;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.sorting.Sort;

/** Verify sorting functions */
public class SortTest {

  private static final int IQ_INDEX = 1;
  private static final int DOB_INDEX = 3;
  // Name,IQ,City,DOB
  private static final String[] columnNames = TestData.SIMPLE_UNSORTED_DATA.getColumnNames();
  private Table unsortedTable;

  @BeforeEach
  public void setUp() {
    unsortedTable = TestData.SIMPLE_UNSORTED_DATA.getTable();
  }

  @Test
  public void sortAscending() {
    // sort ascending by date and then an integer
    Table sortedTable = unsortedTable.sortAscendingOn("IQ", "DOB");
    Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_ASCENDING.getTable();
    assertTableEquals(expectedResults, sortedTable);
  }

  /** Same as sortAscending but descending */
  @Test
  public void sortDescending() {
    unsortedTable = TestData.SIMPLE_UNSORTED_DATA.getTable();
    Table sortedTable = unsortedTable.sortDescendingOn("IQ", "DOB");
    Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_DESCENDING.getTable();
    assertTableEquals(expectedResults, sortedTable);
  }

  /**
   * Verify data that is not sorted descending does match data that has been (this test verifies the
   * accuracy of our positive tests)
   */
  @Disabled
  public void sortDescendingNegative() {
    Table sortedTable = unsortedTable.sortDescendingOn("IQ", "DOB");
    Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_ASCENDING.getTable();
    assertTableEquals(expectedResults, sortedTable);
  }

  @Test
  public void testMultipleSortOrdersVerifyMinus() {
    Table sortedTable =
        unsortedTable.sortOn("-" + columnNames[IQ_INDEX], "-" + columnNames[DOB_INDEX]);
    Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_DESCENDING.getTable();
    assertTableEquals(expectedResults, sortedTable);
  }

  @Test
  public void testAscendingAndDescending() {
    Table sortedTable =
        unsortedTable.sortOn("+" + columnNames[IQ_INDEX], "-" + columnNames[DOB_INDEX]);
    Table expectedResults =
        TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_ASCENDING_AND_THEN_DATE_DESCENDING.getTable();
    assertTableEquals(expectedResults, sortedTable);
  }

  @Test
  public void testMultipleSortOrdersVerifyPlus() {
    Table sortedTable =
        unsortedTable.sortOn("+" + columnNames[IQ_INDEX], "+" + columnNames[DOB_INDEX]);
    Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_ASCENDING.getTable();
    assertTableEquals(expectedResults, sortedTable);

    sortedTable = unsortedTable.sortOn(columnNames[IQ_INDEX], columnNames[DOB_INDEX]);
    expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_ASCENDING.getTable();
    assertTableEquals(expectedResults, sortedTable);
  }

  @Test
  public void testAscendingWithPlusSign() {
    Table sortedTable = unsortedTable.sortOn("+" + columnNames[IQ_INDEX]);
    Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_ASCENDING.getTable();
    assertTableEquals(expectedResults, sortedTable);
  }

  @Test
  public void testSortOnIndices() {
    Table sortedTable = unsortedTable.sortOn(IQ_INDEX, DOB_INDEX);
    Table expectedResults = TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_AND_DATE_ASCENDING.getTable();
    assertTableEquals(expectedResults, sortedTable);
  }

  @Test
  public void testSortOnIndicesAscendingAndDescending() {
    Table sortedTable = unsortedTable.sortOn(IQ_INDEX, -DOB_INDEX);
    Table expectedResults =
        TestData.SIMPLE_SORTED_DATA_BY_DOUBLE_ASCENDING_AND_THEN_DATE_DESCENDING.getTable();
    assertTableEquals(expectedResults, sortedTable);
  }

  @Disabled
  public void testAscendingWithPlusSignNegative() {
    Table sortedTable =
        unsortedTable.sortOn("+" + columnNames[IQ_INDEX], "-" + columnNames[DOB_INDEX]);
    Table expectedResults = TestData.SIMPLE_DATA_WITH_CANONICAL_DATE_FORMAT.getTable();
    assertTableEquals(expectedResults, sortedTable);
  }

  @Test
  public void createSortInvalidPrefixColumnExists() {
    Table table = Table.create("t", DoubleColumn.create("col1"));
    Throwable thrown = assertThrows(IllegalStateException.class, () -> Sort.create(table, "<col1"));

    assertEquals("Column prefix: < is unknown.", thrown.getMessage());
  }

  @Test
  public void createSortValidPrefixColumnDoesNotExist() {
    Table table = Table.create("t", DoubleColumn.create("col1"));
    Throwable thrown = assertThrows(IllegalStateException.class, () -> Sort.create(table, "+col2"));

    assertEquals("Column col2 does not exist in table t", thrown.getMessage());
  }

  @Test
  public void createSortInvalidPrefixColumnDoesNotExist() {
    Table table = Table.create("t", DoubleColumn.create("col1"));
    Throwable thrown = assertThrows(IllegalStateException.class, () -> Sort.create(table, ">col2"));

    assertEquals("Unrecognized Column: '>col2'", thrown.getMessage());
  }

  @Test
  public void testGetOrderAndCreate0() throws Exception {
    assertThrows(IllegalStateException.class, () -> {
      Table table = Table.create("table1", IntColumn.create(" ales"));
      AnalyticQuery.query()
          .from(table)
          .partitionBy("product", "region")
          .orderBy("sales")
          .rowsBetween()
          .unboundedPreceding()
          .andUnBoundedFollowing()
          .sum("sales").as("sumSales")
          .build();
    });
  }
}
