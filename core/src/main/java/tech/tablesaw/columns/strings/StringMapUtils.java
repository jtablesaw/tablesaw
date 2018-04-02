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

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.Column;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * String utility functions. Each function takes one or more String columns as input and produces
 * another Column as output. The resulting column need not be a string column.
 */
public interface StringMapUtils extends Column {

    default StringColumn upperCase() {
        StringColumn newColumn = StringColumn.create(this.name() + "[ucase]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            if (value == null) {
                newColumn.append(StringColumn.MISSING_VALUE);
            } else {
                newColumn.append(value.toUpperCase());
            }
        }
        return newColumn;
    }

    default StringColumn lowerCase() {

        StringColumn newColumn = StringColumn.create(name() + "[lcase]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(value.toLowerCase());
        }
        return newColumn;
    }

    default StringColumn trim() {

        StringColumn newColumn = StringColumn.create(name() + "[trim]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(value.trim());
        }
        return newColumn;
    }

    default StringColumn replaceAll(String regex, String replacement) {

        StringColumn newColumn = StringColumn.create(name() + "[repl]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(value.replaceAll(regex, replacement));
        }
        return newColumn;
    }

    default StringColumn replaceFirst(String regex, String replacement) {

        StringColumn newColumn = StringColumn.create(name() + "[repl]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(value.replaceFirst(regex, replacement));
        }
        return newColumn;
    }

    default StringColumn substring(int start, int end) {

        StringColumn newColumn = StringColumn.create(name() + "[sub]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(value.substring(start, end));
        }
        return newColumn;
    }


    default StringColumn substring(int start) {

        StringColumn newColumn = StringColumn.create(name() + "[sub]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(value.substring(start));
        }
        return newColumn;
    }

    default StringColumn abbreviate(int maxWidth) {

        StringColumn newColumn = StringColumn.create(name() + "[abbr]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(StringUtils.abbreviate(value, maxWidth));
        }
        return newColumn;
    }

    default StringColumn padEnd(int minLength, char padChar) {

        StringColumn newColumn = StringColumn.create(name() + "[pad]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(Strings.padEnd(value, minLength, padChar));
        }
        return newColumn;
    }

    default StringColumn padStart(int minLength, char padChar) {

        StringColumn newColumn = StringColumn.create(name() + "[pad]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(Strings.padStart(value, minLength, padChar));
        }
        return newColumn;
    }

    default StringColumn commonPrefix(Column column2) {

        StringColumn newColumn = StringColumn.create(name() + column2.name() + "[prefix]");

        for (int r = 0; r < size(); r++) {
            String value1 = getString(r);
            String value2 = column2.getString(r);
            newColumn.append(Strings.commonPrefix(value1, value2));
        }
        return newColumn;
    }

    default StringColumn commonSuffix(Column column2) {

        StringColumn newColumn = StringColumn.create(name() + column2.name() + "[suffix]");

        for (int r = 0; r < size(); r++) {
            String value1 = getString(r);
            String value2 = column2.getString(r);
            newColumn.append(Strings.commonSuffix(value1, value2));
        }
        return newColumn;
    }

    /**
     * Returns a column containing the levenshtein distance between the two given string columns
     */
    default Column distance(Column column2) {

        NumberColumn newColumn = NumberColumn.create(name() + column2.name() + "[distance]");

        for (int r = 0; r < size(); r++) {
            String value1 = getString(r);
            String value2 = column2.getString(r);
            newColumn.append(LevenshteinDistance.getDefaultInstance().apply(value1, value2));
        }
        return newColumn;
    }

    default StringColumn join(Column column2, String delimiter) {

        StringColumn newColumn = StringColumn.create(name() + column2.name() + "[joining]");

        for (int r = 0; r < size(); r++) {
            String[] values = new String[2];
            values[0] = getString(r);
            values[1] = column2.getString(r);
            newColumn.append(StringUtils.join(values, delimiter));
        }
        return newColumn;
    }

    /**
     * Return a copy of this column with the given string appended
     *
     * @param append the column to append
     * @return the new column
     */
    default StringColumn concatenate(StringColumn append) {
        StringColumn newColumn = StringColumn.create(name() + "[column appended]", this.size());
        for (int r = 0; r < size(); r++) {
            newColumn.append(getString(r) + append.get(r));
        }
        return newColumn;
    }

    /**
     * Return a copy of this column with the given string appended to each element
     *
     * @param append the string to append
     * @return the new column
     */
    default StringColumn concatenate(String append) {
        StringColumn newColumn = StringColumn.create(name() + "[append]", this.size());
        for (int r = 0; r < size(); r++) {
            newColumn.append(getString(r) + append);
        }
        return newColumn;
    }

    /**
     * Creates a new column, replacing each string in this column with a new string formed by
     * replacing any substring that matches the regex
     *
     * @param regexArray  the regex array to replace
     * @param replacement the replacement array
     * @return the new column
     */
    default StringColumn replaceAll(String[] regexArray, String replacement) {

        StringColumn newColumn = StringColumn.create(name() + "[repl]", this.size());

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            for (String regex : regexArray) {
                value = value.replaceAll(regex, replacement);
            }
            newColumn.append(value);
        }
        return newColumn;
    }

    default StringColumn tokenizeAndSort(String separator) {
        StringColumn newColumn = StringColumn.create(name() + "[sorted]", this.size());

        for (int r = 0; r < size(); r++) {
            String value = getString(r);

            Splitter splitter = Splitter.on(separator);
            splitter = splitter.trimResults();
            splitter = splitter.omitEmptyStrings();
            List<String> tokens =
                    new ArrayList<>(splitter.splitToList(value));
            Collections.sort(tokens);
            value = String.join(" ", tokens);
            newColumn.append(value);
        }
        return newColumn;
    }

    /**
     * Splits on Whitespace and returns the lexicographically sorted result.
     *
     * @return a {@link StringColumn}
     */
    default StringColumn tokenizeAndSort() {
        StringColumn newColumn = StringColumn.create(name() + "[sorted]", this.size());

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            Splitter splitter = Splitter.on(CharMatcher.whitespace());
            splitter = splitter.trimResults();
            splitter = splitter.omitEmptyStrings();
            List<String> tokens = new ArrayList<>(splitter.splitToList(value));
            Collections.sort(tokens);
            value = String.join(" ", tokens);
            newColumn.append(value);
        }
        return newColumn;
    }

    default StringColumn tokenizeAndRemoveDuplicates() {
        StringColumn newColumn = StringColumn.create(name() + "[without duplicates]", this.size());

        for (int r = 0; r < size(); r++) {
            String value = getString(r);

            Splitter splitter = Splitter.on(CharMatcher.whitespace());
            splitter = splitter.trimResults();
            splitter = splitter.omitEmptyStrings();
            List<String> tokens = new ArrayList<>(splitter.splitToList(value));

            value = String.join(" ", new HashSet<>(tokens));
            newColumn.append(value);
        }
        return newColumn;
    }
}