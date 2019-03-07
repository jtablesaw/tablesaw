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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class XlsxReaderTest {

    private List<Table> readN(String name, int expectedCount) {
        try {
            String fileName = name + ".xlsx";
            List<Table> tables = Table.read().xlsx(XlsxReadOptions.builder("../data/" + fileName));
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
                assertTrue(column.isMissing(i), "Should be missing value in row " + i + " of column " + column.name() + ", but it was " + column.get(i));
            } else {
                assertEquals(ts[i], column.get(i), "Wrong value in row " + i + " of column " + column.name());
            }
        }
    }

    @Test
    public void testColumns() {
        Table table = read1("columns", 2, "stringcol", "shortcol", "intcol", "longcol", "doublecol", "booleancol", "datecol");
        //        stringcol   shortcol    intcol  longcol doublecol   booleancol  datecol
        //        Hallvard    123 12345678    12345678900 12,34   TRUE    22/02/2019 20:54:09
        //        Marit       124 12345679    12345678901 13,35   FALSE   23/03/2020 21:55:10
        assertColumnValues(table.stringColumn("stringcol"), "Hallvard", "Marit");
        assertColumnValues(table.intColumn("shortcol"), 123, 124);
        assertColumnValues(table.intColumn("intcol"), 12345678, 12345679);
        assertColumnValues(table.longColumn("longcol"), 12345678900L, 12345678901L);
        assertColumnValues(table.doubleColumn("doublecol"), 12.34, 13.35);
        assertColumnValues(table.booleanColumn("booleancol"), true, false);
        assertColumnValues(table.dateTimeColumn("datecol"), LocalDateTime.of(2019, 2, 22, 20, 54, 9), LocalDateTime.of(2020, 3, 23, 21, 55, 10));
    }

    @Test
    public void testColumnsWithMissingValues() {
        Table table = read1("columns-with-missing-values", 2, "stringcol", "shortcol", "intcol", "longcol", "doublecol", "booleancol", "datecol");
//        stringcol    shortcol    intcol        longcol        doublecol    booleancol    datecol
//        Hallvard                12345678    12345678900                TRUE        22/02/2019 20:54:09
//                    124            12345679                13,35
        assertColumnValues(table.stringColumn("stringcol"), "Hallvard", null);
        assertColumnValues(table.intColumn("shortcol"), null, 124);
        assertColumnValues(table.intColumn("intcol"), 12345678, 12345679);
        assertColumnValues(table.longColumn("longcol"), 12345678900L, null);
        assertColumnValues(table.doubleColumn("doublecol"), null, 13.35);
        assertColumnValues(table.booleanColumn("booleancol"), true, null);
        assertColumnValues(table.dateTimeColumn("datecol"), LocalDateTime.of(2019, 2, 22, 20, 54, 9), null);
    }
}
