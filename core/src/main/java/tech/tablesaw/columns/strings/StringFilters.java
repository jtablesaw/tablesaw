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
import tech.tablesaw.columns.strings.filters.ColumnEqualTo;
import tech.tablesaw.columns.strings.filters.ColumnEqualToIgnoringCase;
import tech.tablesaw.columns.strings.filters.ColumnNotEqualTo;
import tech.tablesaw.columns.strings.filters.ContainsString;
import tech.tablesaw.columns.strings.filters.EndsWith;
import tech.tablesaw.columns.strings.filters.EqualToIgnoringCase;
import tech.tablesaw.columns.strings.filters.HasLengthEqualTo;
import tech.tablesaw.columns.strings.filters.IsAlpha;
import tech.tablesaw.columns.strings.filters.IsAlphaNumeric;
import tech.tablesaw.columns.strings.filters.IsLongerThan;
import tech.tablesaw.columns.strings.filters.IsLowerCase;
import tech.tablesaw.columns.strings.filters.IsNumeric;
import tech.tablesaw.columns.strings.filters.IsShorterThan;
import tech.tablesaw.columns.strings.filters.IsUpperCase;
import tech.tablesaw.columns.strings.filters.MatchesRegex;
import tech.tablesaw.columns.strings.filters.StartsWith;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static tech.tablesaw.columns.strings.StringPredicates.*;

public interface StringFilters extends Column {

    StringColumn selectWhere(Filter filter);

    default Selection eval(BiPredicate<String, String> predicate, StringColumn otherColumn) {
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
        return new EqualToIgnoringCase(new StringColumnReference(this.name()), string).apply(this);
    }

    default Selection isEmptyString() {
        return eval(String::isEmpty);
    }

    default Selection startsWith(String string) {
        return new StartsWith(new StringColumnReference(this.name()), string).apply(this);
    }

    default Selection endsWith(String string) {
        return new EndsWith(new StringColumnReference(this.name()), string).apply(this);
    }

    default Selection containsString(String string) {
        return new ContainsString(new StringColumnReference(this.name()), string).apply(this);
    }

    default Selection matchesRegex(String string) {
        return new MatchesRegex(new StringColumnReference(this.name()), string).apply(this);
    }

    default Selection isAlpha() {
        return new IsAlpha(new StringColumnReference(this.name())).apply(this);
    }

    default Selection isNumeric() {
        return new IsNumeric(new StringColumnReference(this.name())).apply(this);
    }

    default Selection isAlphaNumeric() {
        return new IsAlphaNumeric(new StringColumnReference(this.name())).apply(this);
    }

    default Selection isUpperCase() {
        return new IsUpperCase(new StringColumnReference(this.name())).apply(this);
    }

    default Selection isLowerCase() {
        return new IsLowerCase(new StringColumnReference(this.name())).apply(this);
    }

    default Selection lengthEquals(int stringLength) {
        return new HasLengthEqualTo(new StringColumnReference(this.name()), stringLength).apply(this);
    }

    default Selection isShorterThan(int stringLength) {
        return new IsShorterThan(new StringColumnReference(this.name()), stringLength).apply(this);
    }

    default Selection isLongerThan(int stringLength) {
        return new IsLongerThan(new StringColumnReference(this.name()), stringLength).apply(this);
    }

    Selection isIn(String... strings);

    default Selection isIn(Collection<String> strings) {
        return isIn(strings.toArray(new String[strings.size()]));
    }

    Selection isNotIn(String... strings);

    default Selection isNotIn(Collection<String> strings) {
        return isNotIn(strings.toArray(new String[strings.size()]));
    }

    // Column Methods
    default Selection isEqualTo(StringColumn other) {
        return new ColumnEqualTo(other).apply(this);
    }

    default Selection isNotEqualTo(StringColumn other) {
        return new ColumnNotEqualTo(other).apply(this);
    }

    default Selection equalsIgnoreCase(StringColumn other) {
        return new ColumnEqualToIgnoringCase(other).apply(this);
    }

    @Override
    default Selection isMissing() {
        return eval(isMissing);
    }

    @Override
    default Selection isNotMissing() {
        return eval(isNotMissing);
    }

    Selection isEqualTo(String string);

    String get(int index);
}