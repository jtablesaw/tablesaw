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

package tech.tablesaw.columns.dates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.tablesaw.columns.dates.PackedLocalDate.asLocalDate;
import static tech.tablesaw.columns.dates.PackedLocalDate.daysUntil;
import static tech.tablesaw.columns.dates.PackedLocalDate.getDayOfMonth;
import static tech.tablesaw.columns.dates.PackedLocalDate.getDayOfWeek;
import static tech.tablesaw.columns.dates.PackedLocalDate.getMonth;
import static tech.tablesaw.columns.dates.PackedLocalDate.getMonthValue;
import static tech.tablesaw.columns.dates.PackedLocalDate.getYear;
import static tech.tablesaw.columns.dates.PackedLocalDate.isAfter;
import static tech.tablesaw.columns.dates.PackedLocalDate.isBefore;
import static tech.tablesaw.columns.dates.PackedLocalDate.isEqualTo;
import static tech.tablesaw.columns.dates.PackedLocalDate.isFriday;
import static tech.tablesaw.columns.dates.PackedLocalDate.isInApril;
import static tech.tablesaw.columns.dates.PackedLocalDate.isInAugust;
import static tech.tablesaw.columns.dates.PackedLocalDate.isInDecember;
import static tech.tablesaw.columns.dates.PackedLocalDate.isInFebruary;
import static tech.tablesaw.columns.dates.PackedLocalDate.isInJanuary;
import static tech.tablesaw.columns.dates.PackedLocalDate.isInJuly;
import static tech.tablesaw.columns.dates.PackedLocalDate.isInJune;
import static tech.tablesaw.columns.dates.PackedLocalDate.isInMarch;
import static tech.tablesaw.columns.dates.PackedLocalDate.isInMay;
import static tech.tablesaw.columns.dates.PackedLocalDate.isInNovember;
import static tech.tablesaw.columns.dates.PackedLocalDate.isInOctober;
import static tech.tablesaw.columns.dates.PackedLocalDate.isInQ1;
import static tech.tablesaw.columns.dates.PackedLocalDate.isInQ2;
import static tech.tablesaw.columns.dates.PackedLocalDate.isInQ3;
import static tech.tablesaw.columns.dates.PackedLocalDate.isInQ4;
import static tech.tablesaw.columns.dates.PackedLocalDate.isInSeptember;
import static tech.tablesaw.columns.dates.PackedLocalDate.isMonday;
import static tech.tablesaw.columns.dates.PackedLocalDate.isSaturday;
import static tech.tablesaw.columns.dates.PackedLocalDate.isSunday;
import static tech.tablesaw.columns.dates.PackedLocalDate.isThursday;
import static tech.tablesaw.columns.dates.PackedLocalDate.isTuesday;
import static tech.tablesaw.columns.dates.PackedLocalDate.isWednesday;
import static tech.tablesaw.columns.dates.PackedLocalDate.lengthOfMonth;
import static tech.tablesaw.columns.dates.PackedLocalDate.lengthOfYear;
import static tech.tablesaw.columns.dates.PackedLocalDate.minusDays;
import static tech.tablesaw.columns.dates.PackedLocalDate.minusMonths;
import static tech.tablesaw.columns.dates.PackedLocalDate.minusWeeks;
import static tech.tablesaw.columns.dates.PackedLocalDate.minusYears;
import static tech.tablesaw.columns.dates.PackedLocalDate.pack;
import static tech.tablesaw.columns.dates.PackedLocalDate.plusDays;
import static tech.tablesaw.columns.dates.PackedLocalDate.plusMonths;
import static tech.tablesaw.columns.dates.PackedLocalDate.plusWeeks;
import static tech.tablesaw.columns.dates.PackedLocalDate.plusYears;
import static tech.tablesaw.columns.dates.PackedLocalDate.withDayOfMonth;
import static tech.tablesaw.columns.dates.PackedLocalDate.withMonth;
import static tech.tablesaw.columns.dates.PackedLocalDate.withYear;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;

public class PackedLocalDateTest {

  // Tests if getDayOfMonth correctly extracts the day of the month from a packed date
  @Test
  public void testGetDayOfMonth() {
    LocalDate day = LocalDate.of(2011, Month.MARCH, 9);
    assertEquals(9, getDayOfMonth(pack(day)));
  }

  // Tests if withDayOfMonth correctly updates only the day of the month of a packed date
  @Test
  public void testWithDayOfMonth() {
    LocalDate day = LocalDate.of(2011, Month.MARCH, 9);
    int packed = pack(day);
    int day2 = withDayOfMonth(4, packed);
    assertEquals(4, getDayOfMonth(day2));
    assertEquals(2011, getYear(day2));
  }

  // Tests if withMonth correctly updates only the month of a packed date
  @Test
  public void testWithMonth() {
    LocalDate day = LocalDate.of(2011, Month.MARCH, 9);
    int packed = pack(day);
    int day2 = withMonth(7, packed);
    assertEquals(7, getMonthValue(day2));
    assertEquals(2011, getYear(day2));
    assertEquals(9, getDayOfMonth(day2));
  }

  // Tests if withYear correctly updates only the year of a packed date
  @Test
  public void testWithYear() {
    LocalDate day = LocalDate.of(2011, Month.MARCH, 9);
    int packed = pack(day);
    int day2 = withYear(2020, packed);
    assertEquals(3, getMonthValue(day2));
    assertEquals(2020, getYear(day2));
    assertEquals(9, getDayOfMonth(day2));
  }

  // Tests adding years to a packed date
  @Test
  public void testPlusYears() {
    LocalDate day = LocalDate.of(2011, Month.MARCH, 9);
    int packed = pack(day);
    int day2 = plusYears(10, packed);
    assertEquals(3, getMonthValue(day2));
    assertEquals(2021, getYear(day2));
    assertEquals(9, getDayOfMonth(day2));
  }

  // Tests subtracting years from a packed date
  @Test
  public void testMinusYears() {
    LocalDate day = LocalDate.of(2011, Month.MARCH, 9);
    int packed = pack(day);
    int day2 = minusYears(10, packed);
    assertEquals(3, getMonthValue(day2));
    assertEquals(2001, getYear(day2));
    assertEquals(9, getDayOfMonth(day2));
  }

  // Tests adding months to a packed date, including year wrap-around
  @Test
  public void testPlusMonths() {
    LocalDate day = LocalDate.of(2011, Month.MARCH, 9);
    int packed = pack(day);
    int day2 = plusMonths(11, packed);
    assertEquals(2, getMonthValue(day2));
    assertEquals(2012, getYear(day2));
    assertEquals(9, getDayOfMonth(day2));
  }

  // Tests subtracting months from a packed date, including year wrap-around
  @Test
  public void testMinusMonths() {
    LocalDate day = LocalDate.of(2011, Month.MARCH, 9);
    int packed = pack(day);
    int day2 = minusMonths(4, packed);
    assertEquals(11, getMonthValue(day2));
    assertEquals(2010, getYear(day2));
    assertEquals(9, getDayOfMonth(day2));
  }

  // Tests adding days to a packed date, including month/year wrap-around
  @Test
  public void testPlusDays() {
    LocalDate day = LocalDate.of(2011, Month.DECEMBER, 30);
    int packed = pack(day);
    int day2 = plusDays(11, packed);
    assertEquals(1, getMonthValue(day2));
    assertEquals(2012, getYear(day2));
    assertEquals(10, getDayOfMonth(day2));
  }

  // Tests adding weeks to a packed date
  @Test
  public void testPlusWeeks() {
    LocalDate day = LocalDate.of(2000, Month.FEBRUARY, 26);
    int packed = pack(day);
    int day2 = plusWeeks(2, packed);
    assertEquals(asLocalDate(day2), day.plusWeeks(2));
  }

  // Tests subtracting weeks from a packed date
  @Test
  public void testMinusWeeks() {
    LocalDate day = LocalDate.of(2001, Month.JANUARY, 3);
    int packed = pack(day);
    int day2 = minusWeeks(5, packed);
    assertEquals(asLocalDate(day2), day.minusWeeks(5));
  }

  // Tests calculating the number of days between two packed dates
  @Test
  public void testDaysBetween() {
    int packed = pack(2001, 1, 3);
    int day2 = pack(2001, 1, 10);
    assertEquals(7, daysUntil(day2, packed));
  }

  // Tests subtracting days from a packed date, including month/year wrap-around
  @Test
  public void testMinusDays() {
    LocalDate day = LocalDate.of(2011, Month.JANUARY, 3);
    int packed = pack(day);
    int day2 = minusDays(4, packed);
    assertEquals(12, getMonthValue(day2));
    assertEquals(2010, getYear(day2));
    assertEquals(30, getDayOfMonth(day2));
  }

  // Tests getting the correct number of days in a year (handling leap years)
  @Test
  public void testLengthOfYear() {
    LocalDate day = LocalDate.of(2000, Month.JANUARY, 3);
    int packed = pack(day);
    assertEquals(366, lengthOfYear(packed));
    day = LocalDate.of(2001, Month.JANUARY, 3);
    packed = pack(day);
    assertEquals(365, lengthOfYear(packed));
  }

  // Tests getting the correct number of days in a month for leap and non-leap years
  @Test
  public void testLengthOfMonth() {
    LocalDate day = LocalDate.of(2011, Month.JANUARY, 3);
    int packed = pack(day);
    assertEquals(31, lengthOfMonth(packed));
    day = LocalDate.of(2011, Month.SEPTEMBER, 3);
    packed = pack(day);
    assertEquals(30, lengthOfMonth(packed));
  }

  // Tests if DayOfWeek getters and predicates correctly evaluate weekdays
  @Test
  public void testDayOfWeek() {
    LocalDate day = LocalDate.of(2018, Month.MARCH, 29);
    int packed = pack(day);
    assertEquals(DayOfWeek.THURSDAY, getDayOfWeek(packed));
    assertTrue(isThursday(packed));
    packed = plusDays(1, packed);
    assertEquals(DayOfWeek.FRIDAY, getDayOfWeek(packed));
    assertTrue(isFriday(packed));
    packed = plusDays(1, packed);
    assertEquals(DayOfWeek.SATURDAY, getDayOfWeek(packed));
    assertTrue(isSaturday(packed));
    packed = plusDays(1, packed);
    assertEquals(DayOfWeek.SUNDAY, getDayOfWeek(packed));
    assertTrue(isSunday(packed));
    packed = plusDays(1, packed);
    assertEquals(DayOfWeek.MONDAY, getDayOfWeek(packed));
    assertTrue(isMonday(packed));
    packed = plusDays(1, packed);
    assertEquals(DayOfWeek.TUESDAY, getDayOfWeek(packed));
    assertTrue(isTuesday(packed));
    packed = plusDays(1, packed);
    assertEquals(DayOfWeek.WEDNESDAY, getDayOfWeek(packed));
    assertTrue(isWednesday(packed));
  }

  // Tests getting the correct calendar quarter (Q1-Q4) of a packed date
  @Test
  public void testQuarters() {
    LocalDate day = LocalDate.of(2018, Month.MARCH, 29);
    int packed = pack(day);
    assertTrue(isInQ1(packed));
    packed = plusMonths(3, packed);
    assertTrue(isInQ2(packed));
    packed = plusMonths(3, packed);
    assertTrue(isInQ3(packed));
    packed = plusMonths(3, packed);
    assertTrue(isInQ4(packed));
  }

  // Tests extracting the year from a packed date
  @Test
  public void testGetYear() {
    LocalDate today = LocalDate.now();
    assertEquals(today.getYear(), getYear(pack(today)));
  }

  // Tests extracting the month value and verifying monthly predicates (e.g., isInJanuary)
  @Test
  public void testGetMonthValue() {
    int date = pack(LocalDate.of(2015, Month.JANUARY, 25));

    Month[] months = Month.values();
    for (int i = 0; i < months.length; i++) {
      assertEquals(months[i], getMonth(date));
      assertEquals(i + 1, getMonthValue(date));
      switch (i) {
        case 0:
          assertTrue(isInJanuary(date));
          break;
        case 1:
          assertTrue(isInFebruary(date));
          break;
        case 2:
          assertTrue(isInMarch(date));
          break;
        case 3:
          assertTrue(isInApril(date));
          break;
        case 4:
          assertTrue(isInMay(date));
          break;
        case 5:
          assertTrue(isInJune(date));
          break;
        case 6:
          assertTrue(isInJuly(date));
          break;
        case 7:
          assertTrue(isInAugust(date));
          break;
        case 8:
          assertTrue(isInSeptember(date));
          break;
        case 9:
          assertTrue(isInOctober(date));
          break;
        case 10:
          assertTrue(isInNovember(date));
          break;
        case 11:
          assertTrue(isInDecember(date));
          break;
        default:
          throw new IllegalArgumentException("Can't have a month outside this range");
      }
      date = plusMonths(1, date);
    }
  }

  // Tests equality comparison between two identical packed dates
  @Test
  public void testEquals() {
    int date = pack(LocalDate.of(2015, Month.JANUARY, 25));
    int date2 = pack(LocalDate.of(2015, Month.JANUARY, 25));
    assertTrue(isEqualTo(date, date2));
  }

  // Tests chronological "after", equality, and "before" logic
  @Test
  public void testAfter() {
    int date = pack(LocalDate.of(2015, Month.JANUARY, 25));
    int date2 = minusDays(1, date);
    assertTrue(isAfter(date, date2));
    assertFalse(isEqualTo(date, date2));
    assertFalse(isBefore(date, date2));
  }

  // Tests chronological "before", "after", and equality logic
  @Test
  public void testBefore() {
    int date = pack(LocalDate.of(2015, Month.JANUARY, 25));
    int date2 = plusDays(1, date);
    assertTrue(isBefore(date, date2));
    assertFalse(isAfter(date, date2));
    assertFalse(isEqualTo(date, date2));
  }

  // Tests if getDayOfWeek correctly extracts the day of the week matching java.time.LocalDate
  @Test
  public void testGetDayOfWeek() {
    LocalDate date = LocalDate.of(2015, Month.DECEMBER, 25);
    int dateTime = pack(date);
    assertEquals(date.getDayOfWeek(), getDayOfWeek(dateTime));
  }
}
