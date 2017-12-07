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
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.LongColumnUtils;

public interface LongMapUtils extends LongColumnUtils {

    default LongColumn plus(LongColumn... columns) {

        // TODO(lwhite): Assert all columns are the same size.
        String nString = names(columns);
        String name = String.format("sum(%s)", nString);
        LongColumn newColumn = new LongColumn(name);

        for (int r = 0; r < columns[0].size(); r++) {
            long result = 0;
            for (LongColumn column : columns) {
                result = result + column.get(r);
            }
            newColumn.append(result);
        }
        return newColumn;
    }

    // TODO(lwhite): make this a shared utility
    default String names(LongColumn[] columns) {
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
        for (long next : this) {
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
        for (long next : this) {
            if (total != 0) {
                pctColumn.append(((float) next / total) * 100);
            } else {
                pctColumn.append(FloatColumn.MISSING_VALUE);
            }
        }
        return pctColumn;
    }

    long sum();

    long get(int index);

    default LongColumn difference(LongColumn column2) {
        LongColumn result = new LongColumn(name() + " - " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            result.append(get(r) - column2.get(r));
        }
        return result;
    }
}
