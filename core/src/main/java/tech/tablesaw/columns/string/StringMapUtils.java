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

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.columns.Column;

/**
 * String utility functions. Each function takes one or more String columns as input and produces
 * another Column as output. The resulting column need not be a string column.
 */
public interface StringMapUtils extends Column {

    default StringColumn upperCase() {
        StringColumn newColumn = new StringColumn(this.name() + "[ucase]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            if (value == null) {
                newColumn.append(StringColumn.MISSING_VALUE);
            } else {
                newColumn.add(value.toUpperCase());
            }
        }
        return newColumn;
    }

    default StringColumn lowerCase() {

        StringColumn newColumn = new StringColumn(name() + "[lcase]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(value.toLowerCase());
        }
        return newColumn;
    }

    default StringColumn trim() {

        StringColumn newColumn = new StringColumn(name() + "[trim]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(value.trim());
        }
        return newColumn;
    }

    default StringColumn replaceAll(String regex, String replacement) {

        StringColumn newColumn = new StringColumn(name() + "[repl]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(value.replaceAll(regex, replacement));
        }
        return newColumn;
    }

    default StringColumn replaceFirst(String regex, String replacement) {

        StringColumn newColumn = new StringColumn(name() + "[repl]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(value.replaceFirst(regex, replacement));
        }
        return newColumn;
    }

    default StringColumn substring(int start, int end) {

        StringColumn newColumn = new StringColumn(name() + "[sub]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(value.substring(start, end));
        }
        return newColumn;
    }


    default StringColumn substring(int start) {

        StringColumn newColumn = new StringColumn(name() + "[sub]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(value.substring(start));
        }
        return newColumn;
    }

    default StringColumn abbreviate(int maxWidth) {

        StringColumn newColumn = new StringColumn(name() + "[abbr]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(StringUtils.abbreviate(value, maxWidth));
        }
        return newColumn;
    }

    default StringColumn padEnd(int minLength, char padChar) {

        StringColumn newColumn = new StringColumn(name() + "[pad]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(Strings.padEnd(value, minLength, padChar));
        }
        return newColumn;
    }

    default StringColumn padStart(int minLength, char padChar) {

        StringColumn newColumn = new StringColumn(name() + "[pad]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.append(Strings.padStart(value, minLength, padChar));
        }
        return newColumn;
    }

    default StringColumn commonPrefix(Column column2) {

        StringColumn newColumn = new StringColumn(name() + column2.name() + "[prefix]");

        for (int r = 0; r < size(); r++) {
            String value1 = getString(r);
            String value2 = column2.getString(r);
            newColumn.append(Strings.commonPrefix(value1, value2));
        }
        return newColumn;
    }

    default StringColumn commonSuffix(Column column2) {

        StringColumn newColumn = new StringColumn(name() + column2.name() + "[suffix]");

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

        FloatColumn newColumn = new FloatColumn(name() + column2.name() + "[distance]");

        for (int r = 0; r < size(); r++) {
            String value1 = getString(r);
            String value2 = column2.getString(r);
            newColumn.append(LevenshteinDistance.getDefaultInstance().apply(value1, value2));
        }
        return newColumn;
    }

    default StringColumn join(Column column2, String delimiter) {

        StringColumn newColumn = new StringColumn(name() + column2.name() + "[join]");

        for (int r = 0; r < size(); r++) {
            String[] values = new String[2];
            values[0] = getString(r);
            values[1] = column2.getString(r);
            newColumn.append(StringUtils.join(values, delimiter));
        }
        return newColumn;
    }
}