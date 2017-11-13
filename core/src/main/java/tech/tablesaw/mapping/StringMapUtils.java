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

package tech.tablesaw.mapping;

import com.google.common.base.Strings;

import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.columns.Column;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

/**
 * String utility functions. Each function takes one or more String columns as input and produces
 * another Column as output. The resulting column need not be a string column.
 */
public interface StringMapUtils extends Column {

    default CategoryColumn upperCase() {
        CategoryColumn newColumn = new CategoryColumn(this.name() + "[ucase]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            if (value == null) {
                newColumn.add(null);
            } else {
                newColumn.add(value.toUpperCase());
            }
        }
        return newColumn;
    }

    default CategoryColumn lowerCase() {

        CategoryColumn newColumn = new CategoryColumn(name() + "[lcase]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.add(value.toLowerCase());
        }
        return newColumn;
    }

    default CategoryColumn trim() {

        CategoryColumn newColumn = new CategoryColumn(name() + "[trim]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.add(value.trim());
        }
        return newColumn;
    }

    default CategoryColumn replaceAll(String regex, String replacement) {

        CategoryColumn newColumn = new CategoryColumn(name() + "[repl]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.add(value.replaceAll(regex, replacement));
        }
        return newColumn;
    }

    default CategoryColumn replaceFirst(String regex, String replacement) {

        CategoryColumn newColumn = new CategoryColumn(name() + "[repl]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.add(value.replaceFirst(regex, replacement));
        }
        return newColumn;
    }

    default CategoryColumn substring(int start, int end) {

        CategoryColumn newColumn = new CategoryColumn(name() + "[sub]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.add(value.substring(start, end));
        }
        return newColumn;
    }


    default CategoryColumn substring(int start) {

        CategoryColumn newColumn = new CategoryColumn(name() + "[sub]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.add(value.substring(start));
        }
        return newColumn;
    }

    default CategoryColumn abbreviate(int maxWidth) {

        CategoryColumn newColumn = new CategoryColumn(name() + "[abbr]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.add(StringUtils.abbreviate(value, maxWidth));
        }
        return newColumn;
    }

    default CategoryColumn padEnd(int minLength, char padChar) {

        CategoryColumn newColumn = new CategoryColumn(name() + "[pad]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.add(Strings.padEnd(value, minLength, padChar));
        }
        return newColumn;
    }

    default CategoryColumn padStart(int minLength, char padChar) {

        CategoryColumn newColumn = new CategoryColumn(name() + "[pad]");

        for (int r = 0; r < size(); r++) {
            String value = getString(r);
            newColumn.add(Strings.padStart(value, minLength, padChar));
        }
        return newColumn;
    }

    default CategoryColumn commonPrefix(Column column2) {

        CategoryColumn newColumn = new CategoryColumn(name() + column2.name() + "[prefix]");

        for (int r = 0; r < size(); r++) {
            String value1 = getString(r);
            String value2 = column2.getString(r);
            newColumn.add(Strings.commonPrefix(value1, value2));
        }
        return newColumn;
    }

    default CategoryColumn commonSuffix(Column column2) {

        CategoryColumn newColumn = new CategoryColumn(name() + column2.name() + "[suffix]");

        for (int r = 0; r < size(); r++) {
            String value1 = getString(r);
            String value2 = column2.getString(r);
            newColumn.add(Strings.commonSuffix(value1, value2));
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

    default CategoryColumn join(Column column2, String delimiter) {

        CategoryColumn newColumn = new CategoryColumn(name() + column2.name() + "[join]");

        for (int r = 0; r < size(); r++) {
            String[] values = new String[2];
            values[0] = getString(r);
            values[1] = column2.getString(r);
            newColumn.add(StringUtils.join(values, delimiter));
        }
        return newColumn;
    }
}