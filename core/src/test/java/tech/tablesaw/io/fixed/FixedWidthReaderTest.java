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

package tech.tablesaw.io.fixed;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.tablesaw.api.ColumnType.FLOAT;
import static tech.tablesaw.api.ColumnType.SHORT;
import static tech.tablesaw.api.ColumnType.SKIP;
import static tech.tablesaw.api.ColumnType.STRING;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.univocity.parsers.fixed.FixedWidthFields;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;

/**
 * Tests for CSV Reading
 */
public class FixedWidthReaderTest {

    private final FixedWidthFields car_fields_specs = new FixedWidthFields(4, 5, 40, 40, 8);
    private final ColumnType[] car_types = {SHORT, STRING, STRING, STRING, FLOAT};
    private final ColumnType[] car_types_with_SKIP = {SHORT, STRING, STRING, SKIP, FLOAT};

    @Test
    public void testWithCarsData() throws Exception {

        Table table = Table.read().fixedWidth(FixedWidthReadOptions
                .builder("../data/fixed_width_cars_test.txt")
                .header(true)
                .columnTypes(car_types)
                .columnSpecs(car_fields_specs)
                .padding('_')
                .lineEnding("\n")
                .build()
        );

        assertEquals("[Year, Make, Model, Description, Price]", table.columnNames().toString());

        table = table.sortDescendingOn("Year");
        table.removeColumns("Description");

        assertEquals("[Year, Make, Model, Price]", table.columnNames().toString());

    }

    @Test
    public void testWithColumnSKIP() throws Exception {

        Table table = Table.read().fixedWidth(FixedWidthReadOptions
                .builder("../data/fixed_width_cars_test.txt")
                .header(true)
                .columnTypes(car_types_with_SKIP)
                .columnSpecs(car_fields_specs)
                .padding('_')
                .lineEnding("\n")
                .build());

        assertEquals(4, table.columnCount());

        assertEquals("[Year, Make, Model, Price]", table.columnNames().toString());
    }

    @Test
    public void testWithColumnSKIPWithoutHeader() throws Exception {

        Table table = Table.read().fixedWidth(FixedWidthReadOptions
                .builder("../data/fixed_width_cars_no_header_test.txt")
                .header(false)
                .columnTypes(car_types_with_SKIP)
                .columnSpecs(car_fields_specs)
                .padding('_')
                .lineEnding("\n")
                .skipTrailingCharsUntilNewline(true)
                .build());


        assertEquals(4, table.columnCount());

        assertEquals("[C0, C1, C2, C4]", table.columnNames().toString());
    }

    @Test
    public void testDataTypeDetection() throws Exception {

        InputStream stream = new FileInputStream(new File("../data/fixed_width_cars_test.txt"));
        FixedWidthReadOptions options = FixedWidthReadOptions.builder(stream, "")
                .header(true)
                .columnSpecs(car_fields_specs)
                .padding('_')
                .lineEnding("\n")
                .sample(false)
                .locale(Locale.getDefault())
                .minimizeColumnSizes(true)
                .build();

        Reader reader = new FileReader("../data/fixed_width_missing_values.txt");
        ColumnType[] columnTypes = new FixedWidthReader().detectColumnTypes(reader, options);
        assertArrayEquals(car_types, columnTypes);
    }

    @Test
    public void testWithMissingValue() throws Exception {

        Reader reader = new FileReader("../data/fixed_width_missing_values.txt");
        FixedWidthReadOptions options = FixedWidthReadOptions.builder(reader, "")
                .header(true)
                .columnSpecs(car_fields_specs)
                .padding('_')
                .lineEnding("\n")
                .missingValueIndicator("null")
                .minimizeColumnSizes(true)
                .sample(false)
                .build();

        Table t = Table.read().fixedWidth(options);

        assertEquals(2, t.shortColumn(0).countMissing());
        assertEquals(2, t.stringColumn(1).countMissing());
        assertEquals(1, t.stringColumn(2).countMissing());
        assertEquals(3, t.stringColumn(3).countMissing());
    }


    @Test
    public void testWithSkipTrailingCharsUntilNewline() throws Exception {

        Table table = Table.read().fixedWidth(FixedWidthReadOptions
                .builder("../data/fixed_width_wrong_line_length.txt")
                .header(true)
                .columnTypes(car_types)
                .columnSpecs(car_fields_specs)
                .padding('_')
                .lineEnding("\n")
                .skipTrailingCharsUntilNewline(true)
                .build()
        );

        assertEquals("[Year, Make, Model, Description, Price]", table.columnNames().toString());

        table = table.sortDescendingOn("Year");
        table.removeColumns("Price");

        assertEquals("[Year, Make, Model, Description]", table.columnNames().toString());
    }

}
