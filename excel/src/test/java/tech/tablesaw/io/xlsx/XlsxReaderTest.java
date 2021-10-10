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

package tech.tablesaw.io.xlsx;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static tech.tablesaw.api.ColumnType.BOOLEAN;
import static tech.tablesaw.api.ColumnType.DOUBLE;
import static tech.tablesaw.api.ColumnType.FLOAT;
import static tech.tablesaw.api.ColumnType.INTEGER;
import static tech.tablesaw.api.ColumnType.LOCAL_DATE_TIME;
import static tech.tablesaw.api.ColumnType.LONG;
import static tech.tablesaw.api.ColumnType.STRING;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class XlsxReaderTest {

  private List<Table> readN(String name, int expectedCount) {
    try {
      String fileName = name + ".xlsx";
      List<Table> tables =
          new XlsxReader().readMultiple(XlsxReadOptions.builder("../data/" + fileName).build());
      assertNotNull(tables, "No tables read from " + fileName);
      assertEquals(expectedCount, tables.size(), "Wrong number of tables in " + fileName);
      return tables;
    } catch (final IOException e) {
      fail(e.getMessage());
    }
    return null;
  }

  private Table read1(String name, int size, String... columnNames) {
    Table table = readN(name, 1).get(0);
    int colNum = 0;
    for (final Column<?> column : table.columns()) {
      assertEquals(columnNames[colNum], column.name(), "Wrong column name");
      assertEquals(size, column.size(), "Wrong size for column " + columnNames[colNum]);
      colNum++;
    }
    return table;
  }

  @SafeVarargs
  private final <T> void assertColumnValues(Column<T> column, T... ts) {
    for (int i = 0; i < column.size(); i++) {
      if (ts[i] == null) {
        assertTrue(
            column.isMissing(i),
            "Should be missing value in row "
                + i
                + " of column "
                + column.name()
                + ", but it was "
                + column.get(i));
      } else {
        assertEquals(
            ts[i], column.get(i), "Wrong value in row " + i + " of column " + column.name());
      }
    }
  }

  @Test
  public void testColumns() {
    Table table =
        read1(
            "columns",
            3,
            "stringcol",
            "shortcol",
            "intcol",
            "longcol",
            "doublecol",
            "booleancol",
            "datecol",
            "formulacol",
            "mixed",
            "mixed2",
            "intcol2");
    //        stringcol   shortcol    intcol  longcol doublecol   booleancol  datecol
    // formulacol
    //        Hallvard    123 12345678    12345678900 12,34   TRUE    22/02/2019 20:54:09   135.34
    //        Marit       124 12345679    12345678901 13,35   FALSE   23/03/2020 21:55:10   137.35
    assertColumnValues(table.stringColumn("stringcol"), "Hallvard", "Marit", "Quentin");
    assertColumnValues(table.intColumn("shortcol"), 123, 124, 125);
    assertColumnValues(table.intColumn("intcol"), 12345678, 12345679, 12345679);
    assertColumnValues(table.longColumn("longcol"), 12345678900L, 12345678901L, 12345678901L);
    assertColumnValues(table.doubleColumn("doublecol"), 12.34, 13.35, 13.35);
    assertColumnValues(table.booleanColumn("booleancol"), true, false, false);
    assertColumnValues(
        table.dateTimeColumn("datecol"),
        LocalDateTime.of(2019, 2, 22, 20, 54, 9),
        LocalDateTime.of(2020, 3, 23, 21, 55, 10),
        LocalDateTime.of(2020, 3, 23, 21, 55, 10));
    assertColumnValues(table.doubleColumn("formulacol"), 135.34, 137.35, 138.35);
    assertEquals(table.column("mixed").asList(), Lists.newArrayList("123.00", "abc", ""));
    assertEquals(table.column("mixed2").asList(), Lists.newArrayList("abc", "123", ""));
    assertEquals(table.column("intcol2").asList(), Lists.newArrayList(null, 1234, 1234));
  }

  @Test
  public void testColumnsWithMissingValues() {
    Table table =
        read1(
            "columns-with-missing-values",
            2,
            "stringcol",
            "shortcol",
            "intcol",
            "longcol",
            "doublecol",
            "booleancol",
            "datecol",
            "formulacol");
    //        stringcol    shortcol    intcol        longcol        doublecol    booleancol
    // datecol
    //        Hallvard                12345678    12345678900                TRUE        22/02/2019
    // 20:54:09
    //                    124            12345679                13,35
    assertColumnValues(table.stringColumn("stringcol"), "Hallvard", null);
    assertColumnValues(table.intColumn("shortcol"), null, 124);
    assertColumnValues(table.intColumn("intcol"), 12345678, 12345679);
    assertColumnValues(table.longColumn("longcol"), 12345678900L, null);
    assertColumnValues(table.doubleColumn("doublecol"), null, 13.35);
    assertColumnValues(table.booleanColumn("booleancol"), true, null);
    assertColumnValues(
        table.dateTimeColumn("datecol"), LocalDateTime.of(2019, 2, 22, 20, 54, 9), null);
    assertColumnValues(table.doubleColumn("formulacol"), null, 137.35);
  }

  @Test
  public void testSheetIndex() throws IOException {
    Table table =
        new XlsxReader()
            .read(XlsxReadOptions.builder("../data/multiplesheets.xlsx").sheetIndex(1).build());
    assertNotNull(table, "No table read from multiplesheets.xlsx");
    assertColumnValues(table.stringColumn("stringcol"), "John", "Doe");
    assertEquals("multiplesheets.xlsx#Sheet2", table.name(), "table name is different");

    Table tableImplicit =
        new XlsxReader().read(XlsxReadOptions.builder("../data/multiplesheets.xlsx").build());
    // the table from the 2nd sheet should be picked up
    assertNotNull(tableImplicit, "No table read from multiplesheets.xlsx");

    try {
      new XlsxReader()
          .read(XlsxReadOptions.builder("../data/multiplesheets.xlsx").sheetIndex(0).build());
      fail("First sheet is empty, no table should be found");
    } catch (IllegalArgumentException iae) {
      // expected
    }

    try {
      new XlsxReader()
          .read(XlsxReadOptions.builder("../data/multiplesheets.xlsx").sheetIndex(5).build());
      fail("Only 2 sheets exist, no sheet 5");
    } catch (IndexOutOfBoundsException iobe) {
      // expected
    }
  }

  @Test
  public void testEmptyFileThrowsIllegalArgumentNoTableFound() throws IOException {
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          new XlsxReader().read(XlsxReadOptions.builder("../data/empty.xlsx").build());
        });
  }

  @Test
  public void testCustomizedColumnTypesMixedWithDetection() throws IOException {
    Table table =
        new XlsxReader()
            .read(
                XlsxReadOptions.builder("../data/columns.xlsx")
                    .columnTypesPartial(
                        ImmutableMap.of("shortcol", DOUBLE, "intcol", LONG, "formulacol", FLOAT))
                    .build());

    ColumnType[] columnTypes = table.typeArray();

    assertArrayEquals(
        columnTypes,
        new ColumnType[] {
          STRING,
          DOUBLE,
          LONG,
          LONG,
          DOUBLE,
          BOOLEAN,
          LOCAL_DATE_TIME,
          FLOAT,
          STRING,
          STRING,
          INTEGER
        });

    assertEquals(table.column("mixed").asList(), Lists.newArrayList("123.00", "abc", ""));
    assertEquals(table.column("mixed2").asList(), Lists.newArrayList("abc", "123", ""));
    assertEquals(table.column("intcol2").asList(), Lists.newArrayList(null, 1234, 1234));
  }

  @Test
  public void testCustomizedColumnTypeAllCustomized() throws IOException {
    Table table =
        new XlsxReader()
            .read(
                XlsxReadOptions.builder("../data/columns.xlsx")
                    .columnTypes(columName -> STRING)
                    .build());

    ColumnType[] columnTypes = table.typeArray();

    assertTrue(Arrays.stream(columnTypes).allMatch(columnType -> columnType.equals(STRING)));
  }

  @Test
  public void testCustomizedEmptyColumnsArePreserved() throws IOException {
    Table table =
        new XlsxReader()
            .read(
                XlsxReadOptions.builder("../data/columns.xlsx")
                    .columnTypes(columName -> STRING)
                    .build());

    assertEquals(
        table.column("empty").type(),
        STRING,
        "Empty column must be preserved as it's type is specified");
  }

  @Test
  public void testCustomizedColumnStringShouldTryToPreserveValuesFromOtherExcelTypes()
      throws IOException {
    Table table =
        new XlsxReader()
            .read(
                XlsxReadOptions.builder("../data/columns.xlsx")
                    .columnTypes(columName -> STRING)
                    .build());

    assertEquals(
        table.column("stringcol").asList(), Lists.newArrayList("Hallvard", "Marit", "Quentin"));
    assertEquals(
        table.column("intcol").asList(), Lists.newArrayList("12345678", "12345679", "12345679"));
    // Not ideal, format viewed in excel is without E+10 notation
    assertEquals(
        table.column("longcol").asList(),
        Lists.newArrayList("1.23457E+10", "1.23457E+10", "1.23457E+10"));
    assertEquals(table.column("doublecol").asList(), Lists.newArrayList("12.34", "13.35", "13.35"));
    assertEquals(table.column("booleancol").asList(), Lists.newArrayList("TRUE", "FALSE", "FALSE"));
    assertEquals(
        table.column("datecol").asList(),
        Lists.newArrayList("22/02/2019 20:54:09", "23/03/2020 21:55:10", "23/03/2020 21:55:10"));
    assertEquals(
        table.column("formulacol").asList(), Lists.newArrayList("135.34", "137.35", "138.35"));
    assertEquals(table.column("empty").asList(), Lists.newArrayList("", "", ""));
    assertEquals(table.column("mixed").asList(), Lists.newArrayList("123.00", "abc", ""));
    assertEquals(table.column("mixed2").asList(), Lists.newArrayList("abc", "123", ""));
    assertEquals(table.column("intcol2").asList(), Lists.newArrayList("", "1234", "1234"));
  }
}
