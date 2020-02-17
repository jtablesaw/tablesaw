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

import static tech.tablesaw.api.ColumnType.INTEGER;
import static tech.tablesaw.api.ColumnType.SHORT;
import static tech.tablesaw.api.ColumnType.STRING;
import static tech.tablesaw.api.ColumnType.TEXT;

import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;

public class CsvReadPerformanceTest {

  private static final ColumnType[] types = {
    TEXT, // 0     ID
    STRING, // 1     CNTYFIPS
    TEXT, // 2     Ori
    STRING, // 3     State
    TEXT, // 4     Agency
    STRING, // 5     Agentype
    STRING, // 6     Source
    STRING, // 7     Solved
    SHORT, // 8     Year
    STRING, // 9     StateName
    STRING, // 10    Month
    SHORT, // 11    Incident
    STRING, // 12    ActionType
    STRING, // 13    Homicide
    STRING, // 14    Situation
    SHORT, // 15    VicAge
    STRING, // 16    VicSex
    STRING, // 17    VicRace
    STRING, // 18    VicEthnic
    SHORT, // 19    OffAge
    STRING, // 20    OffSex
    STRING, // 21    OffRace
    STRING, // 22    OffEthnic
    STRING, // 23    Weapon
    STRING, // 24    Relationship
    STRING, // 25    Circumstance
    STRING, // 26    Subcircum
    SHORT, // 27    VicCount
    SHORT, // 28    OffCount
    INTEGER, // 29    FileDate
    STRING, // 30    fstate
    STRING, // 31    MSA
  };

  /** Usage example using a Tornado data set */
  public static void main(String[] args) throws Exception {

    Stopwatch stopwatch = Stopwatch.createStarted();
    Table details = Table.read().csv("../data/SHR76_16.csv");
    stopwatch.stop();
    System.out.println(
        "Large file (752,313 rows) read: "
            + stopwatch.elapsed(TimeUnit.MILLISECONDS)
            + " ms with type detection.");
    System.out.println(details.shape());
    System.out.println(details.structure().printAll());
    System.out.println(details);

    stopwatch.reset();
    stopwatch.start();
    details =
        Table.read().csv(CsvReadOptions.builder("../data/SHR76_16.csv").columnTypes(types).build());
    stopwatch.stop();

    System.out.println(
        "Large file (752,313 rows) read: "
            + stopwatch.elapsed(TimeUnit.MILLISECONDS)
            + " ms without type detection.");
    System.out.println(details.shape());
  }
}
