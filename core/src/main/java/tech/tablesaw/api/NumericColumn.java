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

package tech.tablesaw.api;

import tech.tablesaw.columns.Column;

/**
 * Functionality common to all numeric column types
 */
public interface NumericColumn extends Column {

    static NumericColumn subtractColumns(NumericColumn column1, NumericColumn column2) {
        int col1Size = column1.size();
        int col2Size = column2.size();
        if (col1Size != col2Size) throw new IllegalArgumentException("The columns must have the same number of elements");

        if (column1 instanceof DoubleColumn || column2 instanceof DoubleColumn) {
            DoubleColumn result = new DoubleColumn(column1.name() + " - " + column2.name(), col1Size);
            for (int r = 0; r < col1Size; r++) {
                result.append(DoubleColumn.subtract(column1.getDouble(r), column2.getDouble(r)));
            }
            return result;
        }

        else if (column1 instanceof FloatColumn || column2 instanceof FloatColumn) {
            FloatColumn result = new FloatColumn(column1.name() + " - " + column2.name(), col1Size);
            for (int r = 0; r < col1Size; r++) {
                result.append(FloatColumn.subtract(column1.getFloat(r), column2.getFloat(r)));
            }
            return result;
        }

        else if (column1 instanceof LongColumn || column2 instanceof LongColumn) {
            LongColumn result = new LongColumn(column1.name() + " - " + column2.name(), col1Size);
            for (int r = 0; r < col1Size; r++) {
                result.append(LongColumn.subtract(column1.getLong(r), column2.getLong(r)));
            }
            return result;
        }
        // otherwise we return an IntColumn

        IntColumn result = new IntColumn(column1.name() + " - " + column2.name(), col1Size);
        for (int r = 0; r < col1Size; r++) {
            result.append(IntColumn.subtract(column1.getInt(r), column2.getInt(r)));
        }
        return result;
    }

    static NumericColumn addColumns(NumericColumn column1, NumericColumn column2) {
        int col1Size = column1.size();
        int col2Size = column2.size();
        if (col1Size != col2Size) throw new IllegalArgumentException("The columns must have the same number of elements");

        if (column1 instanceof DoubleColumn || column2 instanceof DoubleColumn) {
            DoubleColumn result = new DoubleColumn(column1.name() + " + " + column2.name(), col1Size);
            for (int r = 0; r < col1Size; r++) {
                result.append(DoubleColumn.add(column1.getDouble(r), column2.getDouble(r)));
            }
            return result;
        }

        else if (column1 instanceof FloatColumn || column2 instanceof FloatColumn) {
            FloatColumn result = new FloatColumn(column1.name() + " + " + column2.name(), col1Size);
            for (int r = 0; r < col1Size; r++) {
                result.append(FloatColumn.add(column1.getFloat(r), column2.getFloat(r)));
            }
            return result;
        }

        else if (column1 instanceof LongColumn || column2 instanceof LongColumn) {
            LongColumn result = new LongColumn(column1.name() + " + " + column2.name(), col1Size);
            for (int r = 0; r < col1Size; r++) {
                result.append(LongColumn.add(column1.getLong(r), column2.getLong(r)));
            }
            return result;
        }
        // otherwise we return an IntColumn

        IntColumn result = new IntColumn(column1.name() + " + " + column2.name(), col1Size);
        for (int r = 0; r < col1Size; r++) {
            result.append(IntColumn.add(column1.getInt(r), column2.getInt(r)));
        }
        return result;
    }

    default NumericColumn subtract(NumericColumn column2) {
        return NumericColumn.subtractColumns(this, column2);
    }

    default NumericColumn add(NumericColumn column2) {
        return NumericColumn.addColumns(this, column2);
    }

    double[] toDoubleArray();

    /**
     * Returns int value at <code>index</code> position in the column. A conversion, if needed, could result
     * in data or accuracy loss.
     * @param index position in column
     * @return int value at position
     */
    default int getInt(int index) {
        throw new UnsupportedOperationException("getInt() method not supported for all data types");
    }

    /**
     * Returns long value at <code>index</code> position in the column. A conversion, if needed, could result
     * in data or accuracy loss.
     * @param index position in column
     * @return long value at position
     */
    default long getLong(int index) {
        throw new UnsupportedOperationException("getLong() method not supported for all data types");
    }

    /**
     * Returns float value at <code>index</code> position in the column. A conversion, if needed, could result
     * in data or accuracy loss.
     * @param index position in column
     * @return float value at position
     */
    default float getFloat(int index) {
        throw new UnsupportedOperationException("getFloat() method not supported for all data types");
    }

    /**
     * Returns double value at <code>index</code> position in the column. A conversion, if needed, could result
     * in data or accuracy loss.
     * @param index position in column
     * @return double value at position
     */
    default double getDouble(int index) {
        throw new UnsupportedOperationException("getDouble() method not supported for all data types");
    }

    double max();

    double min();

    double product();

    double mean();

    double median();

    double quartile1();

    double quartile3();

    double percentile(double percentile);

    double range();

    double variance();

    double populationVariance();

    double standardDeviation();

    double sumOfLogs();

    double sumOfSquares();

    double geometricMean();

    /**
     * Returns the quadraticMean, aka the root-mean-square, for all values in this column
     */
    double quadraticMean();

    double kurtosis();

    double skewness();
}
