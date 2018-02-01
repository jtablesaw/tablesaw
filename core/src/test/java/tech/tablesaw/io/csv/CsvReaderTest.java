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

package tech.tablesaw.io.csv;

import static org.junit.Assert.*;
import static tech.tablesaw.api.ColumnType.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;

/**
 * Tests for CSV Reading
 */
public class CsvReaderTest {

    private final ColumnType[] bus_types = {SHORT_INT, CATEGORY, CATEGORY, FLOAT, FLOAT};
    private final ColumnType[] bus_types_with_SKIP = {SHORT_INT, CATEGORY, SKIP, FLOAT, FLOAT};

    @Test
    public void testWithBusData() throws Exception {
        // Read the CSV file
        Table table = Table.read().csv(CsvReadOptions
            .builder("../data/bus_stop_test.csv")
            .columnTypes(bus_types));

        // Look at the column names
        assertEquals("[stop_id, stop_name, stop_desc, stop_lat, stop_lon]", table.columnNames().toString());

        table = table.sortDescendingOn("stop_id");
        table.removeColumns("stop_desc");
    }

    @Test
    public void testWithColumnSKIP() throws Exception {
        // Read the CSV file
        Table table = Table.read().csv(CsvReadOptions
            .builder("../data/bus_stop_test.csv")
            .columnTypes(bus_types_with_SKIP));

        assertEquals(4, table.columnCount());
        // Look at the column names
        assertEquals("[stop_id, stop_name, stop_lat, stop_lon]", table.columnNames().toString());
    }

    @Test
    public void testWithColumnSKIPWithoutHeader() throws Exception {
        // Read the CSV file
        Table table = Table.read().csv(CsvReadOptions
            .builder("../data/bus_stop_noheader_test.csv")
            .header(false)
            .columnTypes(bus_types_with_SKIP));

        assertEquals(4, table.columnCount());
        // Look at the column names
        assertEquals("[C0, C1, C3, C4]", table.columnNames().toString());
    }

    @Test
    public void testWithBushData() throws Exception {
        // Read the CSV file
        ColumnType[] types = {LOCAL_DATE, SHORT_INT, CATEGORY};
        Table table = Table.read().csv(
                CsvReadOptions.builder("../data/BushApproval.csv")
            .columnTypes(types));

        assertEquals(323, table.rowCount());

        // Look at the column names
        assertEquals("[date, approval, who]", table.columnNames().toString());
    }

    @Test
    public void testBushDataWithoutSamplingForTypeDetection() throws Exception {
        // Read the CSV file
        Table table = Table.read().csv(CsvReadOptions
            .builder("../data/BushApproval.csv")
            .sample(false));

        assertEquals(323, table.rowCount());

        // Look at the column names
        assertEquals("[date, approval, who]", table.columnNames().toString());
    }


    @Test
    public void testDataTypeDetection() throws Exception {
        InputStream stream = new FileInputStream(new File("../data/bus_stop_test.csv"));
        ColumnType[] columnTypes = CsvReader.detectColumnTypes(stream, true, ',', false);
        assertTrue(Arrays.equals(bus_types, columnTypes));
    }

    @Test
    public void testPrintStructure() throws Exception {
        String output =
                "ColumnType[] columnTypes = {\n" +
                        "LOCAL_DATE, // 0     date        \n" +
                        "SHORT_INT,  // 1     approval    \n" +
                        "CATEGORY,   // 2     who         \n" +
                        "}\n";
        assertEquals(output, CsvReader.printColumnTypes("../data/BushApproval.csv", true, ','));
    }

    @Test
    public void testDataTypeDetection2() throws Exception {
        InputStream stream = new FileInputStream(new File("../data/BushApproval.csv"));
        ColumnType[] columnTypes = CsvReader.detectColumnTypes(stream, true, ',', false);
        assertEquals(LOCAL_DATE, columnTypes[0]);
        assertEquals(SHORT_INT, columnTypes[1]);
        assertEquals(CATEGORY, columnTypes[2]);
    }

    @Ignore
    @Test
    public void testLoadFromUrl() throws Exception {
        ColumnType[] types = {LOCAL_DATE, SHORT_INT, CATEGORY};
        String location = "https://raw.githubusercontent.com/jtablesaw/tablesaw/master/data/BushApproval.csv";
        Table table;
        try (InputStream input = new URL(location).openStream()) {
            table = Table.read().csv(CsvReadOptions
                .builder(input, "Bush approval ratings")
                .columnTypes(types));
        }
        assertNotNull(table);
    }

    @Test
    public void testBoundary1() throws Exception {
        Table table1 = Table.read().csv("../data/boundaryTest1.csv");
        table1.structure();  // just make sure the import completed
    }

    @Test
    public void testBoundary2() throws Exception {
        Table table1 = Table.read().csv("../data/boundaryTest2.csv");
        table1.structure(); // just make sure the import completed
    }

    @Test
    public void testReadFailure() throws Exception {
        Table table1 = Table.read().csv("../data/read_failure_test.csv");
        table1.structure(); // just make sure the import completed
        ShortColumn test = table1.shortColumn("Test");
        //TODO(lwhite): Better tests
        assertNotNull(test.summary());
    }

    @Test
    public void testReadFailure2() throws Exception {
        Table table1 = Table.read().csv("../data/read_failure_test2.csv");
        table1.structure(); // just make sure the import completed
        ShortColumn test = table1.shortColumn("Test");

        //TODO(lwhite): Better tests
        assertNotNull(test.summary());
    }

    @Test
    public void testEmptyFileHeaderEnabled() throws Exception {
        Table table1 = Table.read().csv(CsvReadOptions
                .builder("../data/empty_file.csv")
                .header(true));
        assertEquals("0 rows X 0 cols", table1.shape().toString());
    }

    @Test
    public void testEmptyFileHeaderDisabled() throws Exception {
        Table table1 = Table.read().csv(CsvReadOptions
                .builder("../data/empty_file.csv")
                .header(false));
        assertEquals("0 rows X 0 cols", table1.shape().toString());
    }

}
