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

import tech.tablesaw.columns.packeddata.PackedLocalDate;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class PackedLocalDateTest {

    @Test
    public void testGetDayOfMonth() {
        LocalDate today = LocalDate.now();
        assertEquals(today.getDayOfMonth(),
                PackedLocalDate.getDayOfMonth(PackedLocalDate.pack(today)));
    }

    @Test
    public void testGetYear() {
        LocalDate today = LocalDate.now();
        assertEquals(today.getYear(), PackedLocalDate.getYear(PackedLocalDate.pack(today)));
    }

    @Test
    public void testGetMonthValue() {
        int dateTime = PackedLocalDate.pack(LocalDate.of(2015, 12, 25));
        assertEquals(12, PackedLocalDate.getMonthValue(dateTime));
    }

    @Test
    public void testGetDayOfWeek() {
        LocalDate date = LocalDate.of(2015, 12, 25);
        int dateTime = PackedLocalDate.pack(date);
        assertEquals(date.getDayOfWeek(), PackedLocalDate.getDayOfWeek(dateTime));
    }
}