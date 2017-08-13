package tech.tablesaw.io.csv;

import org.junit.Ignore;
import org.junit.Test;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReader;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static tech.tablesaw.api.ColumnType.*;
import static tech.tablesaw.api.QueryHelper.column;

/**
 * Tests for CSV Reading
 */
public class CsvReaderTest {

    private final ColumnType[] bus_types = {SHORT_INT, CATEGORY, CATEGORY, FLOAT, FLOAT};
    private final ColumnType[] bus_types_with_SKIP = {SHORT_INT, CATEGORY, SKIP, FLOAT, FLOAT};

    @Test
    public void testWithBusData() throws Exception {
        // Read the CSV file
        Table table = CsvReader.read(bus_types, true, ',', "../data/bus_stop_test.csv");

        // Look at the column names
        assertEquals("[stop_id, stop_name, stop_desc, stop_lat, stop_lon]", table.columnNames().toString());

        table = table.sortDescendingOn("stop_id");
        table.removeColumns("stop_desc");

        Column c = table.floatColumn("stop_lat");
        Table v = table.selectWhere(column("stop_lon").isGreaterThan(-0.1f));
    }

    @Test
    public void testWithColumnSKIP() throws Exception {
        // Read the CSV file
        Table table = CsvReader.read(bus_types_with_SKIP, true, ',', "../data/bus_stop_test.csv");

        assertEquals(4, table.columnCount());
        // Look at the column names
        assertEquals("[stop_id, stop_name, stop_lat, stop_lon]", table.columnNames().toString());
    }

    @Test
    public void testWithBushData() throws Exception {

        // Read the CSV file
        ColumnType[] types = {LOCAL_DATE, SHORT_INT, CATEGORY};
        Table table = CsvReader.read(types, "../data/BushApproval.csv");

        assertEquals(323, table.rowCount());

        // Look at the column names
        assertEquals("[date, approval, who]", table.columnNames().toString());
    }

    @Test
    public void testBushDataWithoutSamplingForTypeDetection() throws Exception {

        // Read the CSV file
        File file = new File("../data/BushApproval.csv");
        Table table = CsvReader.read(new FileReader(file), file.getName(), true, ',', true);

        assertEquals(323, table.rowCount());

        // Look at the column names
        assertEquals("[date, approval, who]", table.columnNames().toString());
    }


    @Test
    public void testDataTypeDetection() throws Exception {
        Reader reader = new FileReader(new File("../data/bus_stop_test.csv"));
        char delimiter = ',';
        List<String[]> rows = CsvReader.parseCsv(reader, delimiter);
        ColumnType[] columnTypes = CsvReader.detectColumnTypes(rows, true, delimiter, false);
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
        Reader reader = new FileReader(new File("../data/BushApproval.csv"));
        char delimiter = ',';
        List<String[]> rows = CsvReader.parseCsv(reader, delimiter);
        ColumnType[] columnTypes = CsvReader.detectColumnTypes(rows, true, ',', false);
        assertEquals(ColumnType.LOCAL_DATE, columnTypes[0]);
        assertEquals(ColumnType.SHORT_INT, columnTypes[1]);
        assertEquals(ColumnType.CATEGORY, columnTypes[2]);
    }

    @Ignore
    @Test
    public void testLoadFromUrl() throws Exception {
        ColumnType[] types = {LOCAL_DATE, SHORT_INT, CATEGORY};
        String location = "https://raw.githubusercontent.com/jtablesaw/tablesaw/master/data/BushApproval.csv";
        Table table;
        try (Reader input = new InputStreamReader(new URL(location).openStream())) {
            table = Table.createFromReader(input, "Bush approval ratings", types, true, ',');
        }
        assertNotNull(table);
    }

    @Test
    public void testBoundary1() throws Exception {
        Table table1 = Table.createFromCsv("../data/boundaryTest1.csv");
        table1.structure();  // just make sure the import completed
    }

    @Test
    public void testBoundary2() throws Exception {
        Table table1 = Table.createFromCsv("../data/boundaryTest2.csv");
        table1.structure(); // just make sure the import completed
    }

    @Test
    public void testReadFailure() throws Exception {
        Table table1 = Table.createFromCsv("../data/read_failure_test.csv");
        table1.structure(); // just make sure the import completed
        ShortColumn test = table1.shortColumn("Test");
        System.out.println(test.summary().print());
    }

    @Test
    public void testReadFailure2() throws Exception {
        Table table1 = Table.createFromCsv("../data/read_failure_test2.csv");
        table1.structure(); // just make sure the import completed
        ShortColumn test = table1.shortColumn("Test");
        System.out.println(test.summary().print());
    }
}
