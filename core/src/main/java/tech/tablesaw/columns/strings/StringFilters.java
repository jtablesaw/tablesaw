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

import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.filtering.predicates.StringBiPredicate;
import tech.tablesaw.filtering.predicates.StringIntBiPredicate;
import tech.tablesaw.filtering.predicates.StringPredicate;
import tech.tablesaw.selection.Selection;

import java.util.Collection;

import static tech.tablesaw.columns.strings.StringPredicates.*;

public interface StringFilters extends Column {

    StringColumn select(Filter filter);

    Selection eval(StringPredicate predicate);

    Selection eval(StringBiPredicate predicate, String value);

    Selection eval(StringIntBiPredicate predicate, int value);

    Selection eval(StringBiPredicate predicate, StringColumn otherColumn);

    default Selection equalsIgnoreCase(String string) {
        return eval(isEqualToIgnoringCase, string);
    }

    default StringColumn textEqualsIgnoreCase(String string) {
        return select(equalsIgnoreCase(string));
    }

    default Selection startsWith(String string) {
        return eval(startsWith, string);
    }

    default StringColumn textStartsWith(String string) {
        return select(startsWith(string));
    }

    default Selection endsWith(String string) {
        return eval(endsWith, string);
    }

    default StringColumn textEndsWith(String string) {
        return select(endsWith(string));
    }

    default Selection containsString(String string) {
        return eval(stringContains, string);
    }

    default StringColumn textContainsString(String string) {
        return select(containsString(string));
    }

    default Selection matchesRegex(String string) {
        return eval(matchesRegex, string);
    }

    default StringColumn textMatchesRegex(String string) {
        return select(matchesRegex(string));
    }

    default Selection isAlpha() {
        return eval(isAlpha);
    }

    default StringColumn textIsAlpha() {
        return select(isAlpha());
    }

    default Selection isNumeric() {
        return eval(isNumeric);
    }

    default StringColumn textIsNumeric() {
        return select(isNumeric());
    }

    default Selection isAlphaNumeric() {
        return eval(isAlphaNumeric);
    }

    default StringColumn textIsAlphaNumeric() {
        return select(isAlphaNumeric());
    }

    default Selection isUpperCase() {
        return eval(isUpperCase);
    }

    default StringColumn textIsUpperCase() {
        return select(isUpperCase());
    }

    default Selection isLowerCase() {
        return eval(isLowerCase);
    }

    default StringColumn textIsLowerCase() {
        return select(isLowerCase());
    }

    default Selection lengthEquals(int stringLength) {
        return eval(hasEqualLengthTo, stringLength);
    }

    default StringColumn textLengthEquals(int stringLength) {
        return select(lengthEquals(stringLength));
    }

    default Selection isShorterThan(int stringLength) {
        return eval(isShorterThan, stringLength);
    }

    default StringColumn textIsShorterThan(int stringLength) {
        return select(isShorterThan(stringLength));
    }

    default Selection isLongerThan(int stringLength) {
        return eval(isLongerThan, stringLength);
    }

    default StringColumn textIsLongerThan(int stringLength) {
        return select(isLongerThan(stringLength));
    }

    Selection isIn(String... strings);

    default StringColumn textIsIn(String... strings) {
        return select(isIn(strings));
    }

    default Selection isIn(Collection<String> strings) {
        return isIn(strings.toArray(new String[strings.size()]));
    }

    Selection isNotIn(String... strings);

    default Selection isNotIn(Collection<String> strings) {
        return isNotIn(strings.toArray(new String[strings.size()]));
    }

    default StringColumn textIsNotIn(String... strings) {
        return select(isNotIn(strings));
    }

    // Column Methods
    default Selection isEqualTo(StringColumn other) {
        return eval(isEqualTo, other);
    }

    default StringColumn textIsEqualTo(StringColumn other) {
        return select(isEqualTo(other));
    }

    default Selection isNotEqualTo(StringColumn other) {
        return eval(isNotEqualTo, other);
    }

    default StringColumn texIstNotEqualTo(StringColumn other) {
        return select(isNotEqualTo(other));
    }

    default Selection equalsIgnoreCase(StringColumn other) {
        return eval(String::equalsIgnoreCase, other);
    }

    default StringColumn textEqualsIgnoreCase(StringColumn other) {
        return select(equalsIgnoreCase(other));
    }

    @Override
    default Selection isMissing() {
        return eval(isMissing);
    }

    default StringColumn textIsMissing() {
        return select(isMissing());
    }

    @Override
    default Selection isNotMissing() {
        return eval(isNotMissing);
    }

    default StringColumn textIsNotMissing() {
        return select(isNotMissing());
    }

    Selection isEqualTo(String string);

    default StringColumn textIsEqualTo(String string) {
        return select(isEqualTo(string));
    }
}
