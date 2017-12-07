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

package tech.tablesaw.splitting.dates;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.function.Function;

import tech.tablesaw.columns.packeddata.PackedLocalDate;


public class SplitUtils {

    public static LocalDateSplitter byYear = PackedLocalDate::getYear;

    public static LocalDateSplitter byMonth = PackedLocalDate::getMonthValue;

    public static LocalDateSplitter byDayOfMonth = PackedLocalDate::getDayOfMonth;

    public static LocalDateSplitter byDayOfYear = PackedLocalDate::getDayOfYear;

    public static LocalDateSplitter byDayOfWeek = packedLocalDate ->
            PackedLocalDate.getDayOfWeek(packedLocalDate).getValue();

    public static LocalDateSplitter byQuarter = PackedLocalDate::getQuarter;


    public static Function<Comparable<?>, Object> byWeek = comparable -> {
        if (comparable instanceof LocalDate) {
            return ((LocalDate) comparable).get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        } else if (comparable instanceof LocalDateTime) {
            return ((LocalDateTime) comparable).get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        } else {
            throw new IllegalArgumentException("Date function called on non-date column");
        }
    };

    public static Function<Comparable<?>, Object> byHour = comparable -> {
        if (comparable instanceof LocalDateTime) {
            return ((LocalDateTime) comparable).get(ChronoField.HOUR_OF_DAY);
        } else {
            throw new IllegalArgumentException("Time function called on non-time column");
        }
    };

    public static Function<Comparable<?>, Object> bySecondOfMinute = comparable -> {
        if (comparable instanceof LocalDateTime) {
            return ((LocalDateTime) comparable).get(ChronoField.SECOND_OF_MINUTE);
        } else {
            throw new IllegalArgumentException("Time function called on non-time column");
        }
    };

    public static Function<Comparable<?>, Object> bySecondOfDay = comparable -> {
        if (comparable instanceof LocalDateTime) {
            return ((LocalDateTime) comparable).get(ChronoField.SECOND_OF_DAY);
        } else {
            throw new IllegalArgumentException("Time function called on non-time column");
        }
    };

    public static Function<Comparable<?>, Object> byMinuteOfHour = comparable -> {
        if (comparable instanceof LocalDateTime) {
            return ((LocalDateTime) comparable).get(ChronoField.MINUTE_OF_HOUR);
        } else {
            throw new IllegalArgumentException("Time function called on non-time column");
        }
    };

    public static Function<Comparable<?>, Object> byMinuteOfDay = comparable -> {
        if (comparable instanceof LocalDateTime) {
            return ((LocalDateTime) comparable).get(ChronoField.MINUTE_OF_HOUR);
        } else {
            throw new IllegalArgumentException("Time function called on non-time column");
        }
    };

/*
  BY_QUARTER_AND_YEAR,  // 1974-Q1; 1974-Q2; etc.
  BY_MONTH_AND_YEAR,    // 1974-01; 1974-02; 1974-03; etc.
  BY_WEEK_AND_YEAR,     // 1956-51; 1956-52;
  BY_DAY_AND_YEAR,      // 1990-364; 1990-365;
  BY_DAY_AND_MONTH,           // 12-03
  BY_DAY_AND_MONTH_AND_YEAR,  // 2003-04-15
  BY_DAY_AND_WEEK_AND_YEAR,   // 1993-48-6
  BY_DAY_AND_WEEK,            // 52-1 to 52-7
  BY_HOUR_AND_DAY,            //
  BY_MINUTE_AND_HOUR,         // 23-49
*/

}
