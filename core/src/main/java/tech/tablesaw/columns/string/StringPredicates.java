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

package tech.tablesaw.columns.string;

import org.apache.commons.lang3.StringUtils;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.filtering.predicates.StringBiPredicate;
import tech.tablesaw.filtering.predicates.StringIntBiPredicate;
import tech.tablesaw.filtering.predicates.StringPredicate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringPredicates {

    public static final StringPredicate isMissing = i -> i.equals(StringColumn.MISSING_VALUE);

    public static final StringPredicate isNotMissing = i -> !i.equals(StringColumn.MISSING_VALUE);

    public static final StringPredicate isUpperCase = StringUtils::isAllUpperCase;

    public static final StringPredicate isLowerCase = StringUtils::isAllLowerCase;

    public static final StringBiPredicate startsWith = String::startsWith;

    public static final StringBiPredicate endsWith = String::endsWith;

    public static final StringBiPredicate stringContains = String::contains;

    public static final StringBiPredicate matchesRegex = (String valueToTest, String valueToCompareAgainst) -> {
        Pattern p = Pattern.compile(valueToCompareAgainst);
        Matcher m = p.matcher(valueToTest);
        return (m.matches());
    };

    public static final StringBiPredicate isEqualTo = String::equals;

    public static final StringBiPredicate isNotEqualTo = new StringBiPredicate() {
        @Override
        public boolean test(String valueToTest, String valueToCompareAgainst) {
            return !valueToTest.equals(valueToCompareAgainst);
        }
    };

    public static final StringBiPredicate isEqualToIgnoringCase = String::equalsIgnoreCase;

    public static final StringPredicate isAlpha = StringUtils::isAlpha;

    public static final StringPredicate isAlphaNumeric = StringUtils::isAlphanumeric;

    public static final StringPredicate isNumeric = StringUtils::isNumeric;

    public static final StringPredicate isEmpty = StringUtils::isEmpty;

    public static final StringIntBiPredicate isLongerThan = new StringIntBiPredicate() {
        @Override
        public boolean test(String valueToTest, int valueToCompareAgainst) {
            return valueToTest.length() > valueToCompareAgainst;
        }
    };

    public static final StringIntBiPredicate isShorterThan = new StringIntBiPredicate() {
        @Override
        public boolean test(String valueToTest, int valueToCompareAgainst) {
            return valueToTest.length() < valueToCompareAgainst;
        }
    };

    public static final StringIntBiPredicate hasEqualLengthTo = new StringIntBiPredicate() {
        @Override
        public boolean test(String valueToTest, int valueToCompareAgainst) {
            return valueToTest.length() == valueToCompareAgainst;
        }
    };
}
