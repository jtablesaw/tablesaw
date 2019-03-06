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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.io.csv.CsvReadOptions;

/**
 * Tests for filtering on the T class
 */
public class TableFilteringTest {

    private Table table;

    @BeforeEach
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").minimizeColumnSizes(true));
    }

    @Test
    public void testFilter1() {
        Table result = table.where(table.numberColumn("approval").isLessThan(53));
        ShortColumn a = result.shortColumn("approval");
        for (double v : a) {
            assertTrue(v < 53);
        }
    }

    @Test
    public void testReject() {
        Table result = table.dropWhere(table.numberColumn("approval").isLessThan(70));
        ShortColumn a = result.shortColumn("approval");
        for (double v : a) {
            assertFalse(v < 70);
        }
    }

    @Test
    public void testRejectWithMissingValues() {

        String[] values = {"a", "b", "", "d"};
        double[] values2 = {1, Double.NaN, 3, 4};
        StringColumn sc = StringColumn.create("s", values);
        DoubleColumn nc = DoubleColumn.create("n", values2);
        Table test = Table.create("test", sc, nc);
        Table result = test.dropRowsWithMissingValues();
        assertEquals(2, result.rowCount());
        assertEquals("a", result.stringColumn("s").get(0));
        assertEquals("d", result.stringColumn("s").get(1));
    }

    @Test
    public void testSelectRange() {
        Table result = table.inRange(20, 30);
        assertEquals(10, result.rowCount());
        for (Column<?> c: result.columns()) {
            for (int r = 0; r < result.rowCount(); r++) {
                assertEquals(table.getString(r+20, c.name()), result.getString(r, c.name()));
            }
        }
    }

    @Test
    public void testSelectRows() {
        Table result = table.rows(20, 30);
        assertEquals(2, result.rowCount());
        for (Column<?> c: result.columns()) {
            assertEquals(table.getString(20, c.name()), result.getString(0, c.name()));
            assertEquals(table.getString(30, c.name()), result.getString(1, c.name()));
        }
    }

    @Test
    public void testSampleRows() {
        Table result = table.sampleN(20);
        assertEquals(20, result.rowCount());
    }

    @Test
    public void testSampleProportion() {
        Table result = table.sampleX(.1);
        assertEquals(32, result.rowCount());
    }

    @Test
    public void testRejectRows() {
        Table result = table.dropRows(20, 30);
        assertEquals(table.rowCount() - 2, result.rowCount());
        for (Column<?> c: result.columns()) {
            assertEquals(table.getString(21, c.name()), result.getString(20, c.name()));
            assertEquals(table.getString(32, c.name()), result.getString(30, c.name()));
        }
    }

    @Test
    public void testRejectRange() {
        Table result = table.dropRange(20, 30);
        assertEquals(table.rowCount() - 10, result.rowCount());
        for (Column<?> c: result.columns()) {
            for (int r = 30; r < result.rowCount(); r++) {
                assertEquals(result.getString(r, c.name()), table.getString(r + 10, c.name()));
            }
        }
    }

    @Test
    public void testFilter2() {
        Table result = table.where(table.dateColumn("date").isInApril());
        DateColumn d = result.dateColumn("date");
        for (LocalDate v : d) {
            assertTrue(PackedLocalDate.isInApril(PackedLocalDate.pack(v)));
        }
    }

    @Test
    public void testFilter3() {
        Table result = table.where(
                table.dateColumn("date").isInApril().and(
                        table.numberColumn("approval").isGreaterThan(70)));

        DateColumn dates = result.dateColumn("date");
        ShortColumn approval = result.shortColumn("approval");
        for (int row = 0; row < result.rowCount(); row++) {
            assertTrue(PackedLocalDate.isInApril(dates.getIntInternal(row)));
            assertTrue(approval.get(row) > 70);
        }
    }

    @Test
    public void testFilter4() {
        Table result =
                table.where(
                        table.dateColumn("date").isInApril().and(
                                        table.numberColumn("approval").isGreaterThan(70)))
                    .retainColumns("who", "approval");
        assertEquals(2, result.columnCount());
        assertTrue(result.columnNames().contains("who"));
        assertTrue(result.columnNames().contains("approval"));
    }
}
