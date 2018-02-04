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

package tech.tablesaw.store;

import com.google.common.base.Stopwatch;

import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static tech.tablesaw.api.ColumnType.*;

/**
 * Tests for StorageManager
 */
public class StorageManagerTest {

    private static final int COUNT = 5;
    // column types for the tornado table
    private static final ColumnType[] COLUMN_TYPES = {
            FLOAT,   // number by year
            FLOAT,   // year
            FLOAT,   // month
            FLOAT,   // day
            LOCAL_DATE,  // date
            LOCAL_TIME,  // time
            CATEGORY, // tz
            CATEGORY, // st
            CATEGORY, // state fips
            FLOAT,    // state torn number
            FLOAT,    // scale
            FLOAT,    // injuries
            FLOAT,    // fatalities
            CATEGORY, // loss
            FLOAT,   // crop loss
            FLOAT,   // St. Lat
            FLOAT,   // St. Lon
            FLOAT,   // End Lat
            FLOAT,   // End Lon
            FLOAT,   // length
            FLOAT,   // width
            FLOAT,   // NS
            FLOAT,   // SN
            FLOAT,   // SG
            CATEGORY,  // Count FIPS 1-4
            CATEGORY,
            CATEGORY,
            CATEGORY};
    private Table table = Table.create("t");
    private FloatColumn floatColumn = new FloatColumn("float");
    private CategoryColumn categoryColumn = new CategoryColumn("cat");
    private DateColumn localDateColumn = new DateColumn("date");
    private LongColumn longColumn = new LongColumn("long");
    private static String tempDir = System.getProperty("java.io.tmpdir");

    public static void main(String[] args) throws Exception {

        Stopwatch stopwatch = Stopwatch.createStarted();
        System.out.println("loading");
        Table tornados = Table.read()
            .csv(CsvReadOptions.builder("../data/1950-2014_torn.csv").columnTypes(COLUMN_TYPES));

        tornados.setName("tornados");
        System.out.println(String.format("loaded %d records in %d seconds",
                tornados.rowCount(),
                stopwatch.elapsed(TimeUnit.SECONDS)));
        System.out.println(tornados.shape());
        System.out.println(tornados.columnNames().toString());
        System.out.println(tornados.first(10));
        stopwatch.reset().start();
        StorageManager.saveTable(tempDir + "/tablesaw/testdata", tornados);
        stopwatch.reset().start();
        tornados = StorageManager.readTable(tempDir + "/tablesaw/testdata/tornados.saw");
        System.out.println(tornados.first(5));
    }

    @Before
    public void setUp() throws Exception {

        for (int i = 0; i < COUNT; i++) {
            floatColumn.append((float) i);
            localDateColumn.append(LocalDate.now());
            categoryColumn.append("Category " + i);
            longColumn.append(i);
        }
        table.addColumn(floatColumn);
        table.addColumn(localDateColumn);
        table.addColumn(categoryColumn);
        table.addColumn(longColumn);
    }

    @Test
    public void testCatStorage() throws Exception {
        StorageManager.writeColumn(tempDir + "/cat_dogs", categoryColumn);
        CategoryColumn readCat = StorageManager.readCategoryColumn(tempDir + "/cat_dogs", categoryColumn.columnMetadata());
        for (int i = 0; i < categoryColumn.size(); i++) {
            assertEquals(categoryColumn.get(i), readCat.get(i));
        }
    }

    @Test
    public void testWriteTable() throws IOException {
        StorageManager.saveTable(tempDir + "/zeta", table);
        Table t = StorageManager.readTable(tempDir + "/zeta/t.saw");
        assertEquals(table.columnCount(), t.columnCount());
        assertEquals(table.rowCount(), t.rowCount());
        for (int i = 0; i < table.rowCount(); i++) {
            assertEquals(categoryColumn.get(i), t.categoryColumn("cat").get(i));
        }
        t.sortOn("cat"); // exercise the column a bit
    }

    @Test
    public void testWriteTableTwice() throws IOException {

        StorageManager.saveTable(tempDir + "/mytables2", table);
        Table t = StorageManager.readTable(tempDir + "/mytables2/t.saw");
        t.floatColumn("float").setName("a float column");

        StorageManager.saveTable(tempDir + "/mytables2", table);
        t = StorageManager.readTable(tempDir + "/mytables2/t.saw");

        assertEquals(table.name(), t.name());
        assertEquals(table.rowCount(), t.rowCount());
        assertEquals(table.columnCount(), t.columnCount());
    }

    @Test
    public void testSeparator() {
        assertNotNull(StorageManager.separator());
    }
}