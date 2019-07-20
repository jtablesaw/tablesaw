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
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.util.LevenshteinDistance;
import tech.tablesaw.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * String utility functions. Each function takes one or more String columns as input and produces
 * another Column as output. The resulting column need not be a string column.
 *
 * This code was developed as part of Apache Commons Text.
 */
public interface StringMapFunctions extends Column<String> {

    default StringColumn upperCase() {
        StringColumn newColumn = StringColumn.create(this.name() + "[ucase]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            if (value == null) {
                newColumn.append(StringColumnType.missingValueIndicator());
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

    /**
     * Returns a column containing the substrings from start to the end of the input
     * @throws java.lang.StringIndexOutOfBoundsException if any string in the column is shorter than start
     */
    default StringColumn substring(int start) {

        StringColumn newColumn = StringColumn.create(name() + "[sub]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(value.substring(start));
        }
        return newColumn;
    }

    /**
     * Abbreviates a String using ellipses. This will turn
     * "Now is the time for all good men" into "Now is the time for..."
     * @param maxWidth  the maximum width of the resulting strings, including the elipses.
     */
    default StringColumn abbreviate(int maxWidth) {
        final String defaultAbbrevMarker = "...";

        StringColumn newColumn = StringColumn.create(name() + "[abbr]");
        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(StringUtils.abbreviate(value, defaultAbbrevMarker, maxWidth));
        }
        return newColumn;
    }

    default StringColumn format(String formatString) {

        StringColumn newColumn = StringColumn.create(name() + "[formatted]");
        for (int r = 0; r < size(); r++) {
            newColumn.append(String.format(formatString, getString(r)));
        }
        return newColumn;
    }

    /**
     * Returns an IntColumn containing all the values of this string column as integers,
     * assuming all the values are stringified ints in the first place. Otherwise an exception is thrown
     *
     * @return  An IntColumn containing ints parsed from the strings in this column
     */
    default IntColumn parseInt() {
        IntColumn newColumn = IntColumn.create(name() + "[parsed]");
        for (String s : this) {
            if (StringColumn.valueIsMissing(s)) {
                newColumn.appendMissing();
            } else {
                newColumn.append(Integer.parseInt(s));
            }
        }
        return newColumn;
    }

    /**
     * Returns an Double containing all the values of this string column as doubles,
     * assuming all the values are stringified doubles in the first place. Otherwise an exception is thrown
     *
     * @return  A DoubleColumn containing doubles parsed from the strings in this column
     */
    default DoubleColumn parseDouble() {
        DoubleColumn newColumn = DoubleColumn.create(name() + "[parsed]");
        for (String s : this) {
            if (StringColumn.valueIsMissing(s)) {
                newColumn.appendMissing();
            } else {
                newColumn.append(Double.parseDouble(s));
            }
        }
        return newColumn;
    }

    /**
     * Returns an Float containing all the values of this string column as floats,
     * assuming all the values are stringified floats in the first place. Otherwise an exception is thrown
     *
     * @return  A FloatColumn containing floats parsed from the strings in this column
     */
    default FloatColumn parseFloat() {
        FloatColumn newColumn = FloatColumn.create(name() + "[parsed]");
        for (String s : this) {
            if (StringColumn.valueIsMissing(s)) {
                newColumn.appendMissing();
            } else {
                newColumn.append(Float.parseFloat(s));
            }
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

    default StringColumn commonPrefix(Column<?> column2) {

        StringColumn newColumn = StringColumn.create(name() + column2.name() + "[prefix]");

        for (int r = 0; r < size(); r++) {
            String value1 = getString(r);
            String value2 = column2.getString(r);
            newColumn.append(Strings.commonPrefix(value1, value2));
        }
        return newColumn;
    }

    default StringColumn commonSuffix(Column<?> column2) {

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

    default DoubleColumn distance(Column<?> column2) {

        DoubleColumn newColumn = DoubleColumn.create(name() + column2.name() + "[distance]");

        for (int r = 0; r < size(); r++) {
            String value1 = getString(r);
            String value2 = column2.getString(r);
            newColumn.append(LevenshteinDistance.getDefaultInstance().apply(value1, value2));
        }
        return newColumn;
    }

    /**
     * Return a copy of this column with the given string appended
     *
     * @param columns the column to append
     * @return the new column
     */
    default StringColumn join(String separator, Column<?> ... columns) {
        StringColumn newColumn = StringColumn.create(name() + "[column appended]", this.size());
        for (int r = 0; r < size(); r++) {
            String result = getString(r);
            for (Column<?> stringColumn : columns) {
                result = result + separator + stringColumn.get(r);
            }
            newColumn.set(r, result);
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
            newColumn.set(r, getString(r) + append);
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
            newColumn.set(r, value);
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
            value = String.join(separator, tokens);
            newColumn.set(r, value);
        }
        return newColumn;
    }

    default DoubleColumn countTokens(String separator) {
        DoubleColumn newColumn = DoubleColumn.create(name() + "[token count]", this.size());

        for (int r = 0; r < size(); r++) {
            String value = getString(r);

            Splitter splitter = Splitter.on(separator);
            splitter = splitter.trimResults();
            splitter = splitter.omitEmptyStrings();
            List<String> tokens = new ArrayList<>(splitter.splitToList(value));
            newColumn.set(r, tokens.size());
        }
        return newColumn;
    }

    /**
     * Returns a column of arbitrary size containing each unique token in this column, where a token is defined using the
     * given separator, and uniqueness is calculated across the entire column
     *
     * NOTE: Unlike other map functions, this method produces a column whose size may be different from the source,
     * so they cannot safely be combined in a table.
     *
     * @param separator the delimiter used in the tokenizing operation
     * @return          a new column
     */
    default StringColumn uniqueTokens(String separator) {
        return tokens(separator).unique();
    }

    /**
     * Returns a column of arbitrary size containing each token in this column, where a token is defined using the
     * given separator.
     *
     * NOTE: Unlike other map functions, this method produces a column whose size may be different from the source,
     * so they cannot safely be combined in a table.
     *
     * @param separator the delimiter used in the tokenizing operation
     * @return          a new column
     */
    default StringColumn tokens(String separator) {
        StringColumn newColumn = StringColumn.create(name() + "[token count]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            Splitter splitter = Splitter.on(separator);
            splitter = splitter.trimResults();
            splitter = splitter.omitEmptyStrings();
            List<String> tokens = new ArrayList<>(splitter.splitToList(value));
            for (String token : tokens) {
                newColumn.append(token);
            }
        }
        return newColumn;
    }

    /**
     * Returns a column containing the character length of each string in this column
     * The returned column is the same size as the original
     */
    default DoubleColumn length() {
        DoubleColumn newColumn = DoubleColumn.create(name() + "[length]", this.size());

        for (int r = 0; r < size(); r++) {
            newColumn.set(r, getString(r).length());
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
            newColumn.set(r, value);
        }
        return newColumn;
    }

    default StringColumn tokenizeAndRemoveDuplicates(String separator) {
        StringColumn newColumn = StringColumn.create(name() + "[without duplicates]", this.size());

        for (int r = 0; r < size(); r++) {
            String value = getString(r);

            Splitter splitter = Splitter.on(separator);
            splitter = splitter.trimResults();
            splitter = splitter.omitEmptyStrings();
            List<String> tokens = new ArrayList<>(splitter.splitToList(value));

            String result = tokens.stream().distinct().collect(Collectors.joining(separator));
            newColumn.set(r, result);
        }
        return newColumn;
    }
}