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

package tech.tablesaw.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static tech.tablesaw.api.QueryHelper.column;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

public class DateTimeColumnTest {

    private DateTimeColumn column1;

    @Before
    public void setUp() throws Exception {
        Table table = Table.create("Test");
        column1 = new DateTimeColumn("Game date");
        table.addColumn(column1);
    }

    @Test
    public void testAppendCell() throws Exception {
        column1.appendCell("1923-10-20T10:15:30");
        column1.appendCell("1924-12-10T10:15:30");
        column1.appendCell("2015-12-05T10:15:30");
        column1.appendCell("2015-12-20T10:15:30");
        assertEquals(4, column1.size());
        LocalDateTime date = LocalDateTime.now();
        column1.append(date);
        assertEquals(5, column1.size());
    }

    @Test
    public void testConvertMillisSinceEpoch() throws Exception {
        long millis = 1503952123189l;
        column1.appendCell(Long.toString(millis));
        assertEquals(1, column1.size());
        assertEquals(2017, column1.get(0).getYear());
        assertEquals(8, column1.get(0).getMonthValue());
        assertEquals(28, column1.get(0).getDayOfMonth());
        assertEquals(20, column1.get(0).getHour());

        long[] millisArr = column1.asEpochMillisArray();
        assertEquals(1, millisArr.length);
        assertEquals(millis, millisArr[0]);        
    }

    @Test
    public void testAfter() throws Exception {
        Table t = Table.create("test");
        t.addColumn(column1);
        column1.appendCell("2015-12-03T10:15:30");
        column1.appendCell("2015-01-03T10:15:30");
        Table result = t.selectWhere(column("Game date")
                .isAfter(LocalDateTime.of(2015, 2, 2, 0, 0)));
        assertEquals(result.rowCount(), 1);
    }

    @Test
    public void testNull() {
      DateTimeColumn col = new DateTimeColumn("Game date");
      col.appendCell(null);
      assertNull(col.get(0));
    }
}
