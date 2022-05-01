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

package tech.tablesaw.conversion.smile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.regression.LinearModel;
import smile.regression.OLS;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.InstantColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;

public class SmileConverterTest {

  @Test
  public void regression() throws IOException {
    Table moneyball = Table.read().csv("../data/baseball.csv");
    moneyball.addColumns(
        moneyball.numberColumn("RS").subtract(moneyball.numberColumn("RA")).setName("RD"));

    LinearModel winsModel =
        OLS.fit(Formula.lhs("RD"), moneyball.selectColumns("W", "RD").smile().toDataFrame());
    assertNotNull(winsModel.toString());
  }

  @Test
  public void allColumnTypes() throws IOException {
    Table table = Table.create();
    table.addColumns(BooleanColumn.create("boolean", new boolean[] {true, false}));
    table.addColumns(DoubleColumn.create("double", new double[] {1.2, 3.4}));
    table.addColumns(FloatColumn.create("float", new float[] {5.6f, 7.8f}));
    table.addColumns(
        InstantColumn.create(
            "instant",
            new Instant[] {
              Instant.ofEpochMilli(1578452479123l), Instant.ofEpochMilli(1578451111111l)
            }));
    table.addColumns(IntColumn.create("int", new int[] {8, 9}));
    table.addColumns(
        DateColumn.create(
            "date", new LocalDate[] {LocalDate.of(2020, 01, 01), LocalDate.of(2020, 01, 07)}));
    table.addColumns(
        DateTimeColumn.create(
            "datetime",
            new LocalDateTime[] {
              LocalDateTime.ofInstant(Instant.ofEpochMilli(1333352479123l), ZoneOffset.UTC),
              LocalDateTime.ofInstant(Instant.ofEpochMilli(1333333333333l), ZoneOffset.UTC)
            }));
    table.addColumns(
        TimeColumn.create(
            "time", new LocalTime[] {LocalTime.of(8, 37, 48), LocalTime.of(8, 59, 06)}));
    table.addColumns(LongColumn.create("long", new long[] {3l, 4l}));
    table.addColumns(ShortColumn.create("short", new short[] {1, 2}));
    table.addColumns(StringColumn.create("string", new String[] {"james", "bond"}));
    table.addColumns(StringColumn.create("text", new String[] {"foo", "bar"}));
    DataFrame dataframe = table.smile().toDataFrame();
    assertEquals(2, dataframe.nrows());
  }
}
