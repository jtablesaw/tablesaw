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

package tech.tablesaw.columns;

import org.junit.Test;

import tech.tablesaw.columns.packeddata.PackedLocalDateTime;
import tech.tablesaw.columns.packeddata.PackedLocalTime;

import java.time.LocalTime;
import java.time.temporal.ChronoField;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Tests for PackedLocalTime
 */
public class PackedLocalTimeTest {

    @Test
    public void testGetHour() {
        LocalTime now = LocalTime.now();
        assertEquals(now.getHour(), PackedLocalTime.getHour(PackedLocalTime.pack(now)));
    }

    @Test
    public void testGetMinute() {
        LocalTime now = LocalTime.now();
        assertEquals(now.getMinute(), PackedLocalTime.getMinute(PackedLocalTime.pack(now)));
    }

    @Test
    public void testGetSecond() {
        LocalTime now = LocalTime.now();
        assertEquals(now.getSecond(), PackedLocalTime.getSecond(PackedLocalTime.pack(now)));
    }

    @Test
    public void testGetSecondOfDay() {
        LocalTime now = LocalTime.now();
        assertEquals(now.get(ChronoField.SECOND_OF_DAY), PackedLocalTime.getSecondOfDay(PackedLocalTime.pack(now)));
    }

    @Test
    public void testGetMinuteOfDay() {
        LocalTime now = LocalTime.now();
        assertEquals(now.get(ChronoField.MINUTE_OF_DAY), PackedLocalTime.getMinuteOfDay(PackedLocalTime.pack(now)));
    }

    @Test
    public void testGetMillisecondOfDay() {
        LocalTime now = LocalTime.now();
        assertEquals(now.get(ChronoField.MILLI_OF_DAY), PackedLocalTime.getMillisecondOfDay(PackedLocalTime.pack(now)));
    }

    @Test
    public void testPack() {
        LocalTime time = LocalTime.now();
        int packed = PackedLocalTime.pack(time);

        LocalTime t1 = PackedLocalTime.asLocalTime(PackedLocalDateTime.time(packed));
        assertNotNull(t1);
        assertEquals(time.getHour(), t1.getHour());
        assertEquals(time.getMinute(), t1.getMinute());
        assertEquals(time.getSecond(), t1.getSecond());
        assertEquals(time.get(ChronoField.MILLI_OF_SECOND), t1.get(ChronoField.MILLI_OF_SECOND));
    }
}