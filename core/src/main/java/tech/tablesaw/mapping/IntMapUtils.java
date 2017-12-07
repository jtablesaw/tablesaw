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

import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.IntColumnUtils;

public interface IntMapUtils extends IntColumnUtils {

    default IntColumn plus(IntColumn... columns) {

        // TODO(lwhite): Assert all columns are the same size.
        String nString = names(columns);
        String name = String.format("sum(%s)", nString);
        IntColumn newColumn = new IntColumn(name);

        for (int r = 0; r < columns[0].size(); r++) {
            int result = 0;
            for (IntColumn column : columns) {
                result = result + column.get(r);
            }
            newColumn.append(result);
        }
        return newColumn;
    }

    default IntColumn plus(int value) {

        // TODO(lwhite): Assert all columns are the same size.
        String name = name() + " + " + value;
        IntColumn newColumn = new IntColumn(name);

        for (int r = 0; r < size(); r++) {
            newColumn.append(get(r) + value);
        }
        return newColumn;
    }

    default IntColumn multiply(int value) {

        // TODO(lwhite): Assert all columns are the same size.
        String name = name() + " * " + value;
        IntColumn newColumn = new IntColumn(name);

        for (int r = 0; r < size(); r++) {
            int result = (get(r) == IntColumn.MISSING_VALUE) ? IntColumn.MISSING_VALUE : get(r) * value;
            newColumn.append(result);
        }
        return newColumn;
    }

    default FloatColumn multiply(double value) {

        // TODO(lwhite): Assert all columns are the same size.
        String name = name() + " * " + value;
        FloatColumn newColumn = new FloatColumn(name);

        for (int r = 0; r < size(); r++) {
            newColumn.append(get(r) * (float) value);
        }
        return newColumn;
    }

    default FloatColumn divide(int value) {

        // TODO(lwhite): Assert all columns are the same size.
        String name = name() + " / " + value;
        FloatColumn newColumn = new FloatColumn(name);

        for (int r = 0; r < size(); r++) {
            newColumn.append(get(r) / (value * 1.0f));
        }
        return newColumn;
    }

    default FloatColumn divide(double value) {

        // TODO(lwhite): Assert all columns are the same size.
        String name = name() + " / " + value;
        FloatColumn newColumn = new FloatColumn(name);

        for (int r = 0; r < size(); r++) {
            newColumn.append(get(r) / value);
        }
        return newColumn;
    }

    default FloatColumn divide(IntColumn divisor) {

        // TODO(lwhite): Assert all columns are the same size.
        String name = name() + " / " + divisor.name();
        FloatColumn newColumn = new FloatColumn(name);

        for (int r = 0; r < size(); r++) {
            newColumn.append(get(r) / (divisor.get(r) * 1.0f));
        }
        return newColumn;
    }

    // TODO(lwhite): make this a shared utility
    default String names(IntColumn[] columns) {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (Column column : columns) {
            builder.append(column.name());
            if (count < columns.length - 1) {
                builder.append(", ");
            }
            count++;
        }
        return builder.toString();
    }

    /**
     * Return the elements of this column as the ratios of their value and the sum of all
     * elements
     */
    default FloatColumn asRatio() {
        FloatColumn pctColumn = new FloatColumn(name() + " percents");
        float total = sum();
        for (int next : this) {
            if (total != 0) {
                pctColumn.append((float) next / total);
            } else {
                pctColumn.append(FloatColumn.MISSING_VALUE);
            }
        }
        return pctColumn;
    }

    /**
     * Return the elements of this column as the percentages of their value relative to the sum of all
     * elements
     */
    default FloatColumn asPercent() {
        FloatColumn pctColumn = new FloatColumn(name() + " percents");
        float total = sum();
        for (int next : this) {
            if (total != 0) {
                pctColumn.append(((float) next / total) * 100);
            } else {
                pctColumn.append(FloatColumn.MISSING_VALUE);
            }
        }
        return pctColumn;
    }

    long sum();

    int get(int index);

    default IntColumn subtract(int value) {
        IntColumn result = new IntColumn(name() + " - " + value);
        for (int r = 0; r < size(); r++) {
            result.append(get(r) - value);
        }
        return result;
    }
}