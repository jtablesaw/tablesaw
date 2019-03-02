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

import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class XlsxReaderTest {

    private List<Table> readN(final String name, final int expectedCount) {
        try {
            String fileName = name + ".xlsx";
            final List<Table> tables = Table.read().xlsx(XlsxReadOptions.builder("../data/" + fileName));
            Assert.assertNotNull("No tables read from " + fileName, tables);
            Assert.assertEquals("Wrong number of tables in " + fileName, expectedCount, tables.size());
            return tables;
        } catch (final IOException e) {
            fail(e.getMessage());
        }
        return null;
    }
    private Table read1(final String name, final int size, final String... columnNames) {
        final Table table = readN(name, 1).get(0);
        int colNum = 0;
        for (final Column<?> column : table.columns()) {
            Assert.assertEquals("Wrong column name", columnNames[colNum], column.name());
            Assert.assertEquals("Wrong size for column " + columnNames[colNum], size, column.size());
            colNum++;
        }
        return table;
    }

    private <T> void assertColumnValues(final Column<T> column, final T... ts) {
        for (int i = 0; i < column.size(); i++) {
            if (ts[i] == null) {
                Assert.assertTrue("Should be missing value in row " + i + " of column " + column.name() + ", but it was " + column.get(i), column.isMissing(i));
            } else {
                Assert.assertEquals("Wrong value in row " + i + " of column " + column.name(), ts[i], column.get(i));
            }
        }
    }

    @Test
    public void testColumns() {
        final Table table = read1("columns", 2, "stringcol", "shortcol", "intcol", "longcol", "doublecol", "booleancol", "datecol");
        //        stringcol   shortcol    intcol  longcol doublecol   booleancol  datecol
        //        Hallvard    123 12345678    12345678900 12,34   TRUE    22/02/2019 20:54:09
        //        Marit       124 12345679    12345678901 13,35   FALSE   23/03/2020 21:55:10
        assertColumnValues((Column<String>) table.column("stringcol"), "Hallvard", "Marit");
        assertColumnValues((Column<Integer>) table.column("shortcol"), 123, 124);
        assertColumnValues((Column<Integer>) table.column("intcol"), 12345678, 12345679);
        assertColumnValues((Column<Long>) table.column("longcol"), 12345678900L, 12345678901L);
        assertColumnValues((Column<Double>) table.column("doublecol"), 12.34, 13.35);
        assertColumnValues((Column<Boolean>) table.column("booleancol"), true, false);
        assertColumnValues((Column<LocalDateTime>) table.column("datecol"), LocalDateTime.of(2019, 2, 22, 20, 54, 9), LocalDateTime.of(2020, 3, 23, 21, 55, 10));
    }

    @Test
    public void testColumnsWithMissingValues() {
        final Table table = read1("columns-with-missing-values", 2, "stringcol", "shortcol", "intcol", "longcol", "doublecol", "booleancol", "datecol");
//        stringcol    shortcol    intcol        longcol        doublecol    booleancol    datecol
//        Hallvard                12345678    12345678900                TRUE        22/02/2019 20:54:09
//                    124            12345679                13,35
        assertColumnValues((Column<String>) table.column("stringcol"), "Hallvard", null);
        assertColumnValues((Column<Integer>) table.column("shortcol"), null, 124);
        assertColumnValues((Column<Integer>) table.column("intcol"), 12345678, 12345679);
        assertColumnValues((Column<Long>) table.column("longcol"), 12345678900L, null);
        assertColumnValues((Column<Double>) table.column("doublecol"), null, 13.35);
        assertColumnValues((Column<Boolean>) table.column("booleancol"), true, null);
        assertColumnValues((Column<LocalDateTime>) table.column("datecol"), LocalDateTime.of(2019, 2, 22, 20, 54, 9), null);
    }
}
