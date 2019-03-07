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

package tech.tablesaw.io;

import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.dates.DateParser;
import tech.tablesaw.columns.datetimes.DateTimeParser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TypeUtilsTest {

    @Test
    public void testDateFormaterWithLocaleEN() {
        String anotherDate = "12-May-2015";
        LocalDate result = LocalDate.parse(anotherDate, DateParser.DEFAULT_FORMATTER.withLocale(Locale.ENGLISH));
        assertNotNull(result);
    }

    @Test
    public void testDateFormater() {
        final DateTimeFormatter dtTimef8 =
                DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");

        String anotherDate = "10/2/2016 8:18:03 AM";
        dtTimef8.parse(anotherDate);
        LocalDateTime result = LocalDateTime.parse(anotherDate, DateTimeParser.DEFAULT_FORMATTER);
        assertNotNull(result);
    }
}