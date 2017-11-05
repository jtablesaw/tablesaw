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

package tech.tablesaw.mapping;

import org.junit.Test;

import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.columns.packeddata.PackedLocalDateTime;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;

/**
 * Tests for DateTimeMapUtils
 */
public class DateTimeMapUtilsTest {

    private DateTimeColumn startCol = new DateTimeColumn("start");
    private DateTimeColumn stopCol = new DateTimeColumn("stop");
    private LocalDateTime start = LocalDateTime.now();


    @Test
    public void testDifferenceInMilliseconds() throws Exception {
        long pStart = PackedLocalDateTime.pack(start);
        LocalDateTime stop = start.plus(100_000L, ChronoUnit.MILLIS);
        long pStop = PackedLocalDateTime.pack(stop);

        startCol.add(start);
        stopCol.add(stop);

        assertEquals(100_000L, startCol.difference(pStart, pStop, ChronoUnit.MILLIS));
        LongColumn result = startCol.differenceInMilliseconds(stopCol);
        assertEquals(100_000L, result.firstElement());
    }

    @Test
    public void testDifferenceInSeconds() throws Exception {
        LocalDateTime stop = start.plus(100_000L, ChronoUnit.SECONDS);

        startCol.add(start);
        stopCol.add(stop);

        LongColumn result = startCol.differenceInSeconds(stopCol);
        assertEquals(100_000L, result.firstElement());
    }

    @Test
    public void testDifferenceInMinutes() throws Exception {
        LocalDateTime stop = start.plus(100_000L, ChronoUnit.MINUTES);

        startCol.add(start);
        stopCol.add(stop);

        LongColumn result = startCol.differenceInMinutes(stopCol);
        assertEquals(100_000L, result.firstElement());
    }

    @Test
    public void testDifferenceInHours() throws Exception {
        LocalDateTime stop = start.plus(100_000L, ChronoUnit.HOURS);

        startCol.add(start);
        stopCol.add(stop);

        LongColumn result = startCol.differenceInHours(stopCol);
        assertEquals(100_000L, result.firstElement());

    }

    @Test
    public void testDifferenceInDays() throws Exception {
        LocalDateTime stop = start.plus(100_000L, ChronoUnit.DAYS);

        startCol.add(start);
        stopCol.add(stop);

        LongColumn result = startCol.differenceInDays(stopCol);
        assertEquals(100_000L, result.firstElement());
    }

    @Test
    public void testDifferenceInYears() throws Exception {

        LocalDateTime stop = start.plus(10_000L, ChronoUnit.YEARS);
        startCol.add(start);
        stopCol.add(stop);

        LongColumn result = startCol.differenceInYears(stopCol);
        assertEquals(10_000L, result.firstElement());
    }

    @Test
    public void testHour() throws Exception {
        startCol.add(LocalDateTime.of(1984, 12, 12, 7, 30));
        ShortColumn hour = startCol.hour();
        assertEquals(7, hour.firstElement());
    }
}