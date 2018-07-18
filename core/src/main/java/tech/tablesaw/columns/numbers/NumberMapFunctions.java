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

package tech.tablesaw.columns.numbers;

import static tech.tablesaw.api.NumberColumn.MISSING_VALUE;

import org.apache.commons.math3.stat.StatUtils;

import it.unimi.dsi.fastutil.doubles.DoubleIterable;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.columns.Column;

public interface NumberMapFunctions extends Column<Double>, DoubleIterable {

    /**
     * Returns a transformation of the data in this column such that the result has a mean of 0, and a
     * standard deviation of 1
     */
    default NumberColumn normalize() {
        final double[] result = StatUtils.normalize(asDoubleArray());
        return DoubleColumn.create(name() + " normalized", result);
    }

    /**
     * Return the elements of this column as the ratios of their value and the sum of all
     * elements
     */
    default NumberColumn asRatio() {
        final NumberColumn pctColumn = DoubleColumn.create(name() + " percents");
        final double total = sum();
        for (final double next : this) {
            if (total != 0) {
                pctColumn.append((float) next / total);
            } else {
                pctColumn.append(MISSING_VALUE);
            }
        }
        return pctColumn;
    }

    double sum();

    /**
     * Return the elements of this column as the percentages of their value relative to the sum of all
     * elements
     */
    default NumberColumn asPercent() {
        final NumberColumn pctColumn = DoubleColumn.create(name() + " percents");
        final double total = sum();
        for (final double next : this) {
            if (total != 0) {
                pctColumn.append(((float) next / total) * 100);
            } else {
                pctColumn.append(MISSING_VALUE);
            }
        }
        return pctColumn;
    }

    default NumberColumn subtract(final NumberColumn column2) {
        final int col1Size = size();
        final int col2Size = column2.size();
        if (col1Size != col2Size)
            throw new IllegalArgumentException("The columns must have the same number of elements");

        final NumberColumn result = DoubleColumn.create(name() + " - " + column2.name(), col1Size);
        for (int r = 0; r < col1Size; r++) {
            result.append(subtract(get(r), column2.get(r)));
        }
        return result;
    }

    default NumberColumn add(final NumberColumn column2) {
        final int col1Size = size();
        final int col2Size = column2.size();
        if (col1Size != col2Size)
            throw new IllegalArgumentException("The columns must have the same number of elements");

        final NumberColumn result = DoubleColumn.create(name() + " + " + column2.name(), col1Size);
        for (int r = 0; r < col1Size; r++) {
            result.append(add(get(r), column2.get(r)));
        }
        return result;
    }

    default NumberColumn multiply(final NumberColumn column2) {
        final int col1Size = size();
        final int col2Size = column2.size();
        if (col1Size != col2Size)
            throw new IllegalArgumentException("The columns must have the same number of elements");

        final NumberColumn result = DoubleColumn.create(name() + " * " + column2.name(), col1Size);
        for (int r = 0; r < col1Size; r++) {
            result.append(multiply(get(r), column2.get(r)));
        }
        return result;
    }

    default NumberColumn divide(final NumberColumn column2) {
        final int col1Size = size();
        final int col2Size = column2.size();
        if (col1Size != col2Size)
            throw new IllegalArgumentException("The columns must have the same number of elements");

        final NumberColumn result = DoubleColumn.create(name() + " / " + column2.name(), col1Size);
        for (int r = 0; r < col1Size; r++) {
            result.append(divide(get(r), column2.get(r)));
        }
        return result;
    }

    default NumberColumn add(final Number value) {
        final double val = value.doubleValue();
        final NumberColumn result = DoubleColumn.create(name() + " + " + val);
        for (int i = 0; i < size(); i++) {
            result.append(add(get(i), val));
        }
        return result;
    }


    default NumberColumn subtract(final Number value) {
        final double val = value.doubleValue();
        final NumberColumn result = DoubleColumn.create(name() + " - " + val);
        for (int i = 0; i < size(); i++) {
            result.append(subtract(get(i), val));
        }
        return result;
    }

    default NumberColumn divide(final Number value) {
        final double val = value.doubleValue();
        final NumberColumn result = DoubleColumn.create(name() + " / " + val);
        for (int i = 0; i < size(); i++) {
            result.append(divide(get(i), val));
        }
        return result;
    }

    default NumberColumn multiply(final Number value) {
        final double val = value.doubleValue();
        final NumberColumn result = DoubleColumn.create(name() + " * " + val);
        for (int i = 0; i < size(); i++) {
            result.append(multiply(get(i), val));
        }
        return result;
    }

    default double add(final double val1, final double val2) {
        if (NumberColumn.valueIsMissing(val1) || NumberColumn.valueIsMissing(val2)) {
            return MISSING_VALUE;
        }
        return val1 + val2;
    }

    default double multiply(final double val1, final double val2) {
        if (NumberColumn.valueIsMissing(val1) || NumberColumn.valueIsMissing(val2)) {
            return MISSING_VALUE;
        }
        return val1 * val2;
    }

    default double divide(final double val1, final double val2) {
        if (NumberColumn.valueIsMissing(val1) || NumberColumn.valueIsMissing(val2)) {
            return MISSING_VALUE;
        }
        return val1 / val2;
    }

    /**
     * Returns the result of subtracting val2 from val1, after handling missing values
     */
    default double subtract(final double val1, final double val2) {
        if (NumberColumn.valueIsMissing(val1) || NumberColumn.valueIsMissing(val2)) {
            return MISSING_VALUE;
        }
        return val1 - val2;
    }

    /**
     * Returns a doubleColumn with the square of each value in this column
     */
    default NumberColumn square() {
        final NumberColumn newColumn = DoubleColumn.create(name() + "[sq]", size());
        for (final double value : this) {
            newColumn.append(value * value);
        }
        return newColumn;
    }

    default NumberColumn sqrt() {
        final NumberColumn newColumn = DoubleColumn.create(name() + "[sqrt]", size());
        for (final double value : this) {
            newColumn.append(Math.sqrt(value));
        }
        return newColumn;
    }

    default NumberColumn cubeRoot() {
        final NumberColumn newColumn = DoubleColumn.create(name() + "[cbrt]", size());
        for (final double value : this) {
            newColumn.append(Math.cbrt(value));
        }
        return newColumn;
    }

    default NumberColumn cube() {
        final NumberColumn newColumn = DoubleColumn.create(name() + "[cb]", size());
        for (final double value : this) {
            newColumn.append(value * value * value);
        }
        return newColumn;
    }


    default NumberColumn remainder(final NumberColumn column2) {
        final NumberColumn result = DoubleColumn.create(name() + " % " + column2.name(), size());
        for (int r = 0; r < size(); r++) {
            final double val1 = get(r);
            final double val2 = column2.get(r);
            if (NumberColumn.valueIsMissing(val1) || NumberColumn.valueIsMissing(val2)) {
                result.append(MISSING_VALUE);
            } else {
                result.append(get(r) % column2.get(r));
            }
        }
        return result;
    }

    /**
     * Returns the natural log of the values in this column as a NumberColumn.
     */
    default NumberColumn logN() {
        final NumberColumn newColumn = DoubleColumn.create(name() + "[logN]", size());

        for (final double value : this) {
            newColumn.append(Math.log(value));
        }
        return newColumn;
    }

    /**
     * Returns the base 10 log of the values in this column as a NumberColumn.
     */
    default NumberColumn log10() {
        final NumberColumn newColumn = DoubleColumn.create(name() + "[log10]", size());

        for (final double value : this) {
            newColumn.append(Math.log10(value));
        }
        return newColumn;
    }

    /**
     * Returns the natural log of the values in this column, after adding 1 to each so that zero
     * values don't return -Infinity
     */
    default NumberColumn log1p() {
        final NumberColumn newColumn = DoubleColumn.create(name() + "[1og1p]", size());
        for (final double value : this) {
            newColumn.append(Math.log1p(value));
        }
        return newColumn;
    }

    default NumberColumn round() {
        final NumberColumn newColumn = DoubleColumn.create(name() + "[rounded]", size());
        for (final double value : this) {
            newColumn.append(Math.round(value));
        }
        return newColumn;
    }

    /**
     * Returns the rounded values as a NumberColumn. Use roundLong() if larger
     *
     * @throws ClassCastException if the returned value will not fit in an int
     */
    default NumberColumn roundInt() {
        final NumberColumn newColumn = DoubleColumn.create(name() + "[rounded]", size());
        for (final double value : this) {
            newColumn.append((int) Math.round(value));
        }
        return newColumn;
    }


    /**
     * Returns a doubleColumn with the absolute value of each value in this column
     */
    default NumberColumn abs() {
        final NumberColumn newColumn = DoubleColumn.create(name() + "[abs]", size());
        for (final double value : this) {
            newColumn.append(Math.abs(value));
        }
        return newColumn;
    }

    /**
     * For each item in the column, returns the same number with the sign changed.
     * For example:
     * -1.3   returns  1.3,
     * 2.135 returns -2.135
     * 0     returns  0
     */
    default NumberColumn neg() {
        final NumberColumn newColumn = DoubleColumn.create(name() + "[neg]", size());
        for (final double value : this) {
            newColumn.append(value * -1);
        }
        return newColumn;
    }

    default NumberColumn difference() {
        final NumberColumn returnValue = DoubleColumn.create(this.name(), this.size());
        if (isEmpty()) {
            return returnValue;
        }
        returnValue.append(MISSING_VALUE);
        for (int current = 1; current < size(); current++) {
            returnValue.append(subtract(get(current), get(current - 1)));
        }
        return returnValue;
    }

    /**
     * Returns a new column with a cumulative sum calculated
     */
    default NumberColumn cumSum() {
        double total = 0.0;
        final NumberColumn newColumn = DoubleColumn.create(name() + "[cumSum]", size());
        for (final double value : this) {
            if (NumberColumn.valueIsMissing(value)) {
                newColumn.append(MISSING_VALUE);
            } else {
                total += value;
                newColumn.append(total);
            }
        }
        return newColumn;
    }

    /**
     * Returns a new column with a cumulative product calculated
     */
    default NumberColumn cumProd() {
        double total = 1.0;
        final NumberColumn newColumn = DoubleColumn.create(name() + "[cumProd]", size());
        for (final double value : this) {
            if (NumberColumn.valueIsMissing(value)) {
                newColumn.append(MISSING_VALUE);
            } else {
                total *= value;
                newColumn.append(total);
            }
        }
        return newColumn;
    }

    /**
     * Returns a new column with a percent change calculated
     */
    default NumberColumn pctChange() {
        final NumberColumn newColumn = DoubleColumn.create(name() + "[pctChange]", size());
        newColumn.append(MISSING_VALUE);
        for (int i = 1; i < size(); i++) {
            newColumn.append(get(i) / get(i - 1) - 1);
        }
        return newColumn;
    }

    @Override
    Double get(int i);
}
