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

import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.string.filters.ColumnEqualTo;
import tech.tablesaw.columns.string.filters.ColumnEqualToIgnoringCase;
import tech.tablesaw.columns.string.filters.ColumnNotEqualTo;
import tech.tablesaw.columns.string.filters.ContainsString;
import tech.tablesaw.columns.string.filters.EndsWith;
import tech.tablesaw.columns.string.filters.EqualToIgnoringCase;
import tech.tablesaw.columns.string.filters.HasLengthEqualTo;
import tech.tablesaw.columns.string.filters.IsAlpha;
import tech.tablesaw.columns.string.filters.IsAlphaNumeric;
import tech.tablesaw.columns.string.filters.IsLongerThan;
import tech.tablesaw.columns.string.filters.IsLowerCase;
import tech.tablesaw.columns.string.filters.IsNumeric;
import tech.tablesaw.columns.string.filters.IsShorterThan;
import tech.tablesaw.columns.string.filters.IsUpperCase;
import tech.tablesaw.columns.string.filters.MatchesRegex;
import tech.tablesaw.columns.string.filters.StartsWith;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.filtering.predicates.StringBiPredicate;
import tech.tablesaw.filtering.predicates.StringIntBiPredicate;
import tech.tablesaw.filtering.predicates.StringPredicate;
import tech.tablesaw.util.selection.Selection;

import java.util.Collection;

import static tech.tablesaw.columns.string.StringPredicates.*;

public interface StringFilters extends Column {

    StringColumn select(Filter filter);

    Selection eval(StringPredicate predicate);

    Selection eval(StringBiPredicate predicate, String value);

    Selection eval(StringIntBiPredicate predicate, int value);

    Selection eval(StringBiPredicate predicate, StringColumn otherColumn);

    default Selection equalsIgnoreCase(String string) {
        return new EqualToIgnoringCase(new StringColumnReference(this.name()), string).apply(this);
    }

    default StringColumn textEqualsIgnoreCase(String string) {
        return select(equalsIgnoreCase(string));
    }

    default Selection startsWith(String string) {
        return new StartsWith(new StringColumnReference(this.name()), string).apply(this);
    }

    default StringColumn textStartsWith(String string) {
        return select(startsWith(string));
    }

    default Selection endsWith(String string) {
        return new EndsWith(new StringColumnReference(this.name()), string).apply(this);
    }

    default StringColumn textEndsWith(String string) {
        return select(endsWith(string));
    }

    default Selection containsString(String string) {
        return new ContainsString(new StringColumnReference(this.name()), string).apply(this);
    }

    default StringColumn textContainsString(String string) {
        return select(containsString(string));
    }

    default Selection matchesRegex(String string) {
        return new MatchesRegex(new StringColumnReference(this.name()), string).apply(this);
    }

    default StringColumn textMatchesRegex(String string) {
        return select(matchesRegex(string));
    }

    default Selection isAlpha() {
        return new IsAlpha(new StringColumnReference(this.name())).apply(this);
    }

    default StringColumn textIsAlpha() {
        return select(isAlpha());
    }

    default Selection isNumeric() {
        return new IsNumeric(new StringColumnReference(this.name())).apply(this);
    }

    default StringColumn textIsNumeric() {
        return select(isNumeric());
    }

    default Selection isAlphaNumeric() {
        return new IsAlphaNumeric(new StringColumnReference(this.name())).apply(this);
    }

    default StringColumn textIsAlphaNumeric() {
        return select(isAlphaNumeric());
    }

    default Selection isUpperCase() {
        return new IsUpperCase(new StringColumnReference(this.name())).apply(this);
    }

    default StringColumn textIsUpperCase() {
        return select(isUpperCase());
    }

    default Selection isLowerCase() {
        return new IsLowerCase(new StringColumnReference(this.name())).apply(this);
    }

    default StringColumn textIsLowerCase() {
        return select(isLowerCase());
    }

    default Selection lengthEquals(int stringLength) {
        return new HasLengthEqualTo(new StringColumnReference(this.name()), stringLength).apply(this);
    }

    default StringColumn textLengthEquals(int stringLength) {
        return select(lengthEquals(stringLength));
    }

    default Selection isShorterThan(int stringLength) {
        return new IsShorterThan(new StringColumnReference(this.name()), stringLength).apply(this);
    }

    default StringColumn textIsShorterThan(int stringLength) {
        return select(isShorterThan(stringLength));
    }

    default Selection isLongerThan(int stringLength) {
        return new IsLongerThan(new StringColumnReference(this.name()), stringLength).apply(this);
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
        return new ColumnEqualTo(other).apply(this);
    }

    default StringColumn textIsEqualTo(StringColumn other) {
        return select(isEqualTo(other));
    }

    default Selection isNotEqualTo(StringColumn other) {
        return new ColumnNotEqualTo(other).apply(this);
    }

    default StringColumn texIstNotEqualTo(StringColumn other) {
        return select(isNotEqualTo(other));
    }

    default Selection equalsIgnoreCase(StringColumn other) {
        return new ColumnEqualToIgnoringCase(other).apply(this);
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
