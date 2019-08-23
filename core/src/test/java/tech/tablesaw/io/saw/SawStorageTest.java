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

package tech.tablesaw.io.saw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tech.tablesaw.api.ColumnType.*;

import com.google.common.base.Stopwatch;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.*;
import tech.tablesaw.io.csv.CsvReadOptions;

/** Tests for reading and writing saw files */
class SawStorageTest {

  private static final int COUNT = 5;
  // column types for the tornado table
  private static final ColumnType[] COLUMN_TYPES = {
    FLOAT, // number by year
    FLOAT, // year
    FLOAT, // month
    FLOAT, // day
    LOCAL_DATE, // date
    LOCAL_TIME, // time
    STRING, // tz
    STRING, // st
    STRING, // state fips
    FLOAT, // state torn number
    FLOAT, // scale
    FLOAT, // injuries
    FLOAT, // fatalities
    STRING, // loss
    FLOAT, // crop loss
    FLOAT, // St. Lat
    FLOAT, // St. Lon
    FLOAT, // End Lat
    FLOAT, // End Lon
    FLOAT, // length
    FLOAT, // width
    FLOAT, // NS
    FLOAT, // SN
    FLOAT, // SG
    STRING, // Count FIPS 1-4
    STRING,
    STRING,
    STRING
  };
  private static String tempDir = System.getProperty("java.io.tmpdir");
  private Table table = Table.create("t");
  private FloatColumn floatColumn = FloatColumn.create("float");
  private StringColumn categoryColumn = StringColumn.create("cat");
  private DateColumn localDateColumn = DateColumn.create("date");
  private LongColumn longColumn = LongColumn.create("long");

  static void main(String[] args) throws Exception {

    Stopwatch stopwatch = Stopwatch.createStarted();
    System.out.println("loading");
    Table tornados =
        Table.read()
            .csv(CsvReadOptions.builder("../data/1950-2014_torn.csv").columnTypes(COLUMN_TYPES));

    tornados.setName("tornados");
    System.out.println(
        String.format(
            "loaded %d records in %d seconds",
            tornados.rowCount(), stopwatch.elapsed(TimeUnit.SECONDS)));
    System.out.println(tornados.shape());
    System.out.println(tornados.columnNames().toString());
    System.out.println(tornados.first(10));
    stopwatch.reset().start();
    SawWriter.saveTable(tempDir + "/tablesaw/testdata", tornados);
    stopwatch.reset().start();
    tornados = SawReader.readTable(tempDir + "/tablesaw/testdata/tornados.saw");
    System.out.println(tornados.first(5));
  }

  @BeforeEach
  void setUp() {

    for (int i = 0; i < COUNT; i++) {
      floatColumn.append((float) i);
      localDateColumn.append(LocalDate.now());
      categoryColumn.append("Category " + i);
      longColumn.append(i);
    }
    table.addColumns(floatColumn);
    table.addColumns(localDateColumn);
    table.addColumns(categoryColumn);
    table.addColumns(longColumn);
  }

  @Test
  void testWriteTable() {
    SawWriter.saveTable(tempDir + "/zeta", table);
    Table t = SawReader.readTable(tempDir + "/zeta/t.saw");
    assertEquals(table.columnCount(), t.columnCount());
    assertEquals(table.rowCount(), t.rowCount());
    for (int i = 0; i < table.rowCount(); i++) {
      assertEquals(categoryColumn.get(i), t.stringColumn("cat").get(i));
    }
    t.sortOn("cat"); // exercise the column a bit
  }

  @Test
  void testWriteTableTwice() {

    SawWriter.saveTable(tempDir + "/mytables2", table);
    Table t = SawReader.readTable(tempDir + "/mytables2/t.saw");
    t.floatColumn("float").setName("a float column");

    SawWriter.saveTable(tempDir + "/mytables2", table);
    t = SawReader.readTable(tempDir + "/mytables2/t.saw");

    assertEquals(table.name(), t.name());
    assertEquals(table.rowCount(), t.rowCount());
    assertEquals(table.columnCount(), t.columnCount());
  }

  @Test
  void testSeparator() {
    assertNotNull(SawUtils.separator());
  }
}
