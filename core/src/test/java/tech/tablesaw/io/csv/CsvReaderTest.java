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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static tech.tablesaw.api.ColumnType.LOCAL_DATE;
import static tech.tablesaw.api.ColumnType.LOCAL_DATE_TIME;
import static tech.tablesaw.api.ColumnType.NUMBER;
import static tech.tablesaw.api.ColumnType.SKIP;
import static tech.tablesaw.api.ColumnType.STRING;
import static tech.tablesaw.io.csv.CsvReader.detectColumnTypes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.Test;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;

/**
 * Tests for CSV Reading
 */
public class CsvReaderTest {

    private final ColumnType[] bus_types = {NUMBER, STRING, STRING, NUMBER, NUMBER};
    private final ColumnType[] bus_types_with_SKIP = {NUMBER, STRING, SKIP, NUMBER, NUMBER};

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
        ColumnType[] types = {LOCAL_DATE, NUMBER, STRING};
        Table table = Table.read().csv(
                CsvReadOptions.builder("../data/bush.csv")
                        .columnTypes(types));

        assertEquals(323, table.rowCount());

        // Look at the column names
        assertEquals("[date, approval, who]", table.columnNames().toString());
    }

    @Test
    public void testBushDataWithoutSamplingForTypeDetection() throws Exception {
        // Read the CSV file
        Table table = Table.read().csv(CsvReadOptions
                .builder("../data/bush.csv")
                .sample(false));

        assertEquals(323, table.rowCount());

        // Look at the column names
        assertEquals("[date, approval, who]", table.columnNames().toString());
    }


    @Test
    public void testDataTypeDetection() throws Exception {
        InputStream stream = new FileInputStream(new File("../data/bus_stop_test.csv"));
        ColumnType[] columnTypes = detectColumnTypes(stream, true, ',', false, Locale.getDefault());
        assertTrue(Arrays.equals(bus_types, columnTypes));
    }
    
    @Test
    public void testLocalDateDetectionEnglish() throws Exception {

        final InputStream stream = new ByteArrayInputStream((
                "Date\n"
            + "\"Nov 1, 2017\"\n"
            + "\"Oct 1, 2017\"\n"
            + "\"Sep 1, 2017\"\n"
            + "\"Aug 1, 2017\"\n"
            + "\"Jul 1, 2017\"\n"
            + "\"Jun 1, 2017\"\n").getBytes());

        final boolean header = true;
        final char delimiter = ',';
        final boolean useSampling = true;

        final List<ColumnType> actual = asList(detectColumnTypes(stream, header, delimiter, useSampling, Locale.ENGLISH));

        assertThat(actual, is(equalTo(asList(LOCAL_DATE))));

    }
    
    @Test
    public void testLocalDateTimeDetectionEnglish() throws Exception {

        final InputStream stream = new ByteArrayInputStream((
              "Date\n"
            + "09-Nov-2014 13:03\n"
            + "09-Oct-2014 13:03\n"
            + "09-Sep-2014 13:03\n"
            + "09-Aug-2014 13:03\n"
            + "09-Jul-2014 13:03\n"
            + "09-Jun-2014 13:03\n").getBytes());

        final boolean header = true;
        final char delimiter = ',';
        final boolean useSampling = true;

        final List<ColumnType> actual = asList(detectColumnTypes(stream, header, delimiter, useSampling, Locale.ENGLISH));

        assertThat(actual, is(equalTo(asList(LOCAL_DATE_TIME))));

    }

    @Test
    public void testLocalDateDetectionFrench() throws Exception {

        final InputStream stream = new ByteArrayInputStream((
                "Date\n"
            + "\"nov. 1, 2017\"\n"
            + "\"oct. 1, 2017\"\n"
            + "\"sept. 1, 2017\"\n"
            + "\"août 1, 2017\"\n"
            + "\"juil. 1, 2017\"\n"
            + "\"juin 1, 2017\"\n").getBytes());
        
        final boolean header = true;
        final char delimiter = ',';
        final boolean useSampling = true;

        final List<ColumnType> actual = asList(detectColumnTypes(stream, header, delimiter, useSampling, Locale.FRENCH));

        assertThat(actual, is(equalTo(asList(LOCAL_DATE))));

    }

    @Test
    public void testLocalDateTimeDetectionFrench() throws Exception {

        final InputStream stream = new ByteArrayInputStream((
              "Date\n"
            + "09-nov.-2014 13:03\n"
            + "09-oct.-2014 13:03\n"
            + "09-sept.-2014 13:03\n"
            + "09-août-2014 13:03\n"
            + "09-juil.-2014 13:03\n"
            + "09-juin-2014 13:03\n").getBytes());

        final boolean header = true;
        final char delimiter = ',';
        final boolean useSampling = true;

        final List<ColumnType> actual = asList(detectColumnTypes(stream, header, delimiter, useSampling, Locale.FRENCH));

        assertThat(actual, is(equalTo(asList(LOCAL_DATE_TIME))));

    }

    @Test
    public void testPrintStructure() throws Exception {
        String output =
                "ColumnType[] columnTypes = {\n" +
                        "LOCAL_DATE, // 0.0   date        \n" +
                        "NUMBER,     // 1.0   approval    \n" +
                        "STRING,     // 2.0   who         \n" +
                        "}\n";
        assertEquals(output, CsvReader.printColumnTypes("../data/bush.csv", true, ',', Locale.getDefault()));
    }

    @Test
    public void testDataTypeDetection2() throws Exception {
        InputStream stream = new FileInputStream(new File("../data/bush.csv"));
        ColumnType[] columnTypes = detectColumnTypes(stream, true, ',', false, Locale.getDefault());
        assertEquals(LOCAL_DATE, columnTypes[0]);
        assertEquals(NUMBER, columnTypes[1]);
        assertEquals(STRING, columnTypes[2]);
    }

    @Ignore
    @Test
    public void testLoadFromUrl() throws Exception {
        ColumnType[] types = {LOCAL_DATE, NUMBER, STRING};
        String location = "https://raw.githubusercontent.com/jAirframe/Airframe/master/data/bush.csv";
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
        NumberColumn test = table1.numberColumn("Test");
        //TODO(lwhite): Better tests
        assertNotNull(test.summary());
    }

    @Test
    public void testReadFailure2() throws Exception {
        Table table1 = Table.read().csv("../data/read_failure_test2.csv");
        table1.structure(); // just make sure the import completed
        NumberColumn test = table1.numberColumn("Test");

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
