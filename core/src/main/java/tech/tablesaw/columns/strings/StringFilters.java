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

import static tech.tablesaw.columns.strings.StringPredicates.endsWith;
import static tech.tablesaw.columns.strings.StringPredicates.hasEqualLengthTo;
import static tech.tablesaw.columns.strings.StringPredicates.isAlpha;
import static tech.tablesaw.columns.strings.StringPredicates.isAlphaNumeric;
import static tech.tablesaw.columns.strings.StringPredicates.isEmpty;
import static tech.tablesaw.columns.strings.StringPredicates.isEqualTo;
import static tech.tablesaw.columns.strings.StringPredicates.isEqualToIgnoringCase;
import static tech.tablesaw.columns.strings.StringPredicates.isLongerThan;
import static tech.tablesaw.columns.strings.StringPredicates.isLowerCase;
import static tech.tablesaw.columns.strings.StringPredicates.isMissing;
import static tech.tablesaw.columns.strings.StringPredicates.isNotEqualTo;
import static tech.tablesaw.columns.strings.StringPredicates.isNotMissing;
import static tech.tablesaw.columns.strings.StringPredicates.isNumeric;
import static tech.tablesaw.columns.strings.StringPredicates.isShorterThan;
import static tech.tablesaw.columns.strings.StringPredicates.isUpperCase;
import static tech.tablesaw.columns.strings.StringPredicates.matchesRegex;
import static tech.tablesaw.columns.strings.StringPredicates.startsWith;
import static tech.tablesaw.columns.strings.StringPredicates.stringContains;

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

public interface StringFilters extends Column<String> {

    default Selection eval(BiPredicate<String, String> predicate, Column<String> otherColumn) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            if (predicate.test(get(idx), otherColumn.get(idx))) {
                selection.add(idx);
            }
        }
        return selection;
    }

    default Selection eval(BiPredicate<String, String> predicate, String value) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            if (predicate.test(get(idx), value)) {
                selection.add(idx);
            }
        }
        return selection;
    }

    default Selection eval(BiPredicate<String, Integer> predicate, Integer value) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            if (predicate.test(get(idx), value)) {
                selection.add(idx);
            }
        }
        return selection;
    }

    default Selection eval(Predicate<String> predicate) {
        Selection selection = new BitmapBackedSelection();
        for (int idx = 0; idx < size(); idx++) {
            if (predicate.test(get(idx))) {
                selection.add(idx);
            }
        }
        return selection;
    }

    default Selection equalsIgnoreCase(String string) {
        return eval(isEqualToIgnoringCase, string);
    }

    default Selection isEmptyString() {
        return eval(isEmpty);
    }

    default Selection startsWith(String string) {
        return eval(startsWith, string);
    }

    default Selection endsWith(String string) {
        return eval(endsWith, string);
    }

    default Selection containsString(String string) {
        return eval(stringContains, string);
    }

    default Selection matchesRegex(String string) {
        return eval(matchesRegex, string);
    }

    default Selection isAlpha() {
        return eval(isAlpha);
    }

    default Selection isNumeric() {
        return eval(isNumeric);
    }

    default Selection isAlphaNumeric() {
        return eval(isAlphaNumeric);
    }

    default Selection isUpperCase() {
        return eval(isUpperCase);
    }

    default Selection isLowerCase() {
        return eval(isLowerCase);
    }

    default Selection lengthEquals(int stringLength) {
        return eval(hasEqualLengthTo, stringLength);
    }

    default Selection isShorterThan(int stringLength) {
        return eval(isShorterThan, stringLength);
    }

    default Selection isLongerThan(int stringLength) {
        return eval(isLongerThan, stringLength);
    }

    Selection isIn(String... strings);

    Selection isIn(Collection<String> strings);

    Selection isNotIn(String... strings);

    Selection isNotIn(Collection<String> strings);

    // Column Methods
    default Selection isEqualTo(Column<String> other) {
        return eval(isEqualTo, other);
    }

    default Selection isNotEqualTo(Column<String> other) {
        return eval(isNotEqualTo, other);
    }

    default Selection equalsIgnoreCase(Column<String> other) {
        return eval(isEqualToIgnoringCase, other);
    }

    default Selection startsWith(Column<String> other) {
        return eval(startsWith, other);
    }

    @Override
    default Selection isMissing() {
        return eval(isMissing);
    }

    @Override
    default Selection isNotMissing() {
        return eval(isNotMissing);
    }

    default Selection isEqualTo(String string) {
        return eval(isEqualTo, string);
    }

    default Selection isNotEqualTo(String string) {
        Selection selection = new BitmapBackedSelection();
        selection.addRange(0, size());
        selection.andNot(isEqualTo(string));
        return selection;
    }

    String get(int index);
}