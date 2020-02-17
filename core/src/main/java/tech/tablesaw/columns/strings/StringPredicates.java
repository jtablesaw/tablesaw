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

package tech.tablesaw.columns.strings;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import tech.tablesaw.util.StringUtils;

public class StringPredicates {

  private StringPredicates() {}

  public static final Predicate<String> isMissing =
      i -> i.equals(StringColumnType.missingValueIndicator());

  public static final Predicate<String> isNotMissing =
      i -> !i.equals(StringColumnType.missingValueIndicator());

  public static final Predicate<String> isUpperCase = StringUtils::isAllUpperCase;

  public static final Predicate<String> isLowerCase = StringUtils::isAllLowerCase;

  public static final BiPredicate<String, String> startsWith = String::startsWith;

  public static final BiPredicate<String, String> endsWith = String::endsWith;

  public static final BiPredicate<String, String> stringContains = String::contains;

  public static final BiPredicate<String, String> matchesRegex =
      (String valueToTest, String valueToCompareAgainst) -> {
        Pattern p = Pattern.compile(valueToCompareAgainst);
        Matcher m = p.matcher(valueToTest);
        return (m.matches());
      };

  public static final BiPredicate<String, String> isEqualTo = String::equals;

  public static final BiPredicate<String, String> isNotEqualTo =
      (valueToTest, valueToCompareAgainst) -> !valueToTest.equals(valueToCompareAgainst);

  public static final BiPredicate<String, String> isEqualToIgnoringCase = String::equalsIgnoreCase;

  public static final Predicate<String> isAlpha = StringUtils::isAlpha;

  public static final Predicate<String> isAlphaNumeric = StringUtils::isAlphanumeric;

  public static final Predicate<String> isNumeric = StringUtils::isNumeric;

  public static final Predicate<String> isEmpty = e -> e.length() == 0;

  public static final BiPredicate<String, Integer> isLongerThan =
      (valueToTest, valueToCompareAgainst) -> valueToTest.length() > valueToCompareAgainst;

  public static final BiPredicate<String, Integer> isShorterThan =
      (valueToTest, valueToCompareAgainst) -> valueToTest.length() < valueToCompareAgainst;

  public static final BiPredicate<String, Integer> hasEqualLengthTo =
      (valueToTest, valueToCompareAgainst) -> valueToTest.length() == valueToCompareAgainst;
}
