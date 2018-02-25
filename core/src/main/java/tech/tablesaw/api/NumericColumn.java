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

    static NumericColumn multiplyColumns(NumericColumn column1, NumericColumn column2) {
        int col1Size = column1.size();
        int col2Size = column2.size();
        if (col1Size != col2Size) throw new IllegalArgumentException("The columns must have the same number of elements");

        if (column1 instanceof DoubleColumn || column2 instanceof DoubleColumn) {
            DoubleColumn result = new DoubleColumn(column1.name() + " * " + column2.name(), col1Size);
            for (int r = 0; r < col1Size; r++) {
                result.append(DoubleColumn.multiply(column1.getDouble(r), column2.getDouble(r)));
            }
            return result;
        }

        else if (column1 instanceof FloatColumn || column2 instanceof FloatColumn) {
            FloatColumn result = new FloatColumn(column1.name() + " * " + column2.name(), col1Size);
            for (int r = 0; r < col1Size; r++) {
                result.append(FloatColumn.multiply(column1.getFloat(r), column2.getFloat(r)));
            }
            return result;
        }

        else if (column1 instanceof LongColumn || column2 instanceof LongColumn) {
            LongColumn result = new LongColumn(column1.name() + " * " + column2.name(), col1Size);
            for (int r = 0; r < col1Size; r++) {
                result.append(LongColumn.multiply(column1.getLong(r), column2.getLong(r)));
            }
            return result;
        }
        // otherwise we return an IntColumn

        IntColumn result = new IntColumn(column1.name() + " * " + column2.name(), col1Size);
        for (int r = 0; r < col1Size; r++) {
            result.append(IntColumn.multiply(column1.getInt(r), column2.getInt(r)));
        }
        return result;
    }

    static NumericColumn divideColumns(NumericColumn column1, NumericColumn column2) {
        int col1Size = column1.size();
        int col2Size = column2.size();
        if (col1Size != col2Size) throw new IllegalArgumentException("The columns must have the same number of elements");

        if (column1 instanceof DoubleColumn || column2 instanceof DoubleColumn) {
            DoubleColumn result = new DoubleColumn(column1.name() + " / " + column2.name(), col1Size);
            for (int r = 0; r < col1Size; r++) {
                result.append(DoubleColumn.divide(column1.getDouble(r), column2.getDouble(r)));
            }
            return result;
        }

        else if (column1 instanceof FloatColumn || column2 instanceof FloatColumn) {
            FloatColumn result = new FloatColumn(column1.name() + " / " + column2.name(), col1Size);
            for (int r = 0; r < col1Size; r++) {
                result.append(FloatColumn.divide(column1.getFloat(r), column2.getFloat(r)));
            }
            return result;
        }

        else if (column1 instanceof LongColumn || column2 instanceof LongColumn) {
            LongColumn result = new LongColumn(column1.name() + " / " + column2.name(), col1Size);
            for (int r = 0; r < col1Size; r++) {
                result.append(LongColumn.divide(column1.getLong(r), column2.getLong(r)));
            }
            return result;
        }
        // otherwise we return an IntColumn

        IntColumn result = new IntColumn(column1.name() + " / " + column2.name(), col1Size);
        for (int r = 0; r < col1Size; r++) {
            result.append(IntColumn.divide(column1.getInt(r), column2.getInt(r)));
        }
        return result;
    }

    default NumericColumn subtract(NumericColumn column2) {
        return NumericColumn.subtractColumns(this, column2);
    }

    default NumericColumn add(NumericColumn column2) {
        return NumericColumn.addColumns(this, column2);
    }

    default NumericColumn multiply(NumericColumn column2) {
        return NumericColumn.multiplyColumns(this, column2);
    }

    default NumericColumn divide(NumericColumn column2) {
        return NumericColumn.divideColumns(this, column2);
    }

    default NumericColumn add(Number value) {
        if (value instanceof Double || this instanceof DoubleColumn) {
            double val = (double) value;
            DoubleColumn result = new DoubleColumn(name() + " + " + val);
            for (int i = 0; i < size(); i++) {
                result.append(DoubleColumn.add(getDouble(i) , val));
            }
            return result;
        } else if (value instanceof Float || this instanceof FloatColumn) {
            float val = (float) value;
            FloatColumn result = new FloatColumn(name() + " + " + val);
            for (int i = 0; i < size(); i++) {
                result.append(FloatColumn.add(val, getFloat(i)));
            }
            return result;
        } else if (value instanceof Long || this instanceof LongColumn) {
            long val = (long) value;
            LongColumn result = new LongColumn(name() + " + " + val);
            for (int i = 0; i < size(); i++) {
                result.append(LongColumn.add(getLong(i), val));
            }
            return result;
        }
        int val = (int) value;
        IntColumn result = new IntColumn(name() + " + " + val);
        for (int i = 0; i < size(); i++) {
            result.append(IntColumn.add(getInt(i), val));
        }
        return result;
    }

    default NumericColumn subtract(Number value) {
        if (value instanceof Double || this instanceof DoubleColumn) {
            double val = (double) value;
            DoubleColumn result = new DoubleColumn(name() + " - " + val);
            for (int i = 0; i < size(); i++) {
                result.append(DoubleColumn.subtract(getDouble(i) , val));
            }
            return result;
        } else if (value instanceof Float || this instanceof FloatColumn) {
            float val = (float) value;
            FloatColumn result = new FloatColumn(name() + " - " + val);
            for (int i = 0; i < size(); i++) {
                result.append(FloatColumn.subtract(val, getFloat(i)));
            }
            return result;
        } else if (value instanceof Long || this instanceof LongColumn) {
            long val = (long) value;
            LongColumn result = new LongColumn(name() + " - " + val);
            for (int i = 0; i < size(); i++) {
                result.append(LongColumn.subtract(getLong(i), val));
            }
            return result;
        }
        int val = (int) value;
        IntColumn result = new IntColumn(name() + " - " + val);
        for (int i = 0; i < size(); i++) {
            result.append(IntColumn.subtract(getInt(i), val));
        }
        return result;
    }

    default NumericColumn divide(Number value) {
        if (value instanceof Double || this instanceof DoubleColumn) {
            double val = (double) value;
            DoubleColumn result = new DoubleColumn(name() + " / " + val);
            for (int i = 0; i < size(); i++) {
                result.append(DoubleColumn.divide(getDouble(i) , val));
            }
            return result;
        } else if (value instanceof Float || this instanceof FloatColumn) {
            float val = (float) value;
            FloatColumn result = new FloatColumn(name() + " / " + val);
            for (int i = 0; i < size(); i++) {
                result.append(FloatColumn.divide(val, getFloat(i)));
            }
            return result;
        } else if (value instanceof Long || this instanceof LongColumn) {
            long val = (long) value;
            LongColumn result = new LongColumn(name() + " / " + val);
            for (int i = 0; i < size(); i++) {
                result.append(LongColumn.divide(getLong(i), val));
            }
            return result;
        }
        int val = (int) value;
        IntColumn result = new IntColumn(name() + " / " + val);
        for (int i = 0; i < size(); i++) {
            result.append(IntColumn.divide(getInt(i), val));
        }
        return result;
    }

    default NumericColumn multiply(Number value) {
        if (value instanceof Double || this instanceof DoubleColumn) {
            double val = (double) value;
            DoubleColumn result = new DoubleColumn(name() + " * " + val);
            for (int i = 0; i < size(); i++) {
                result.append(DoubleColumn.multiply(getDouble(i) , val));
            }
            return result;
        } else if (value instanceof Float || this instanceof FloatColumn) {
            float val = (float) value;
            FloatColumn result = new FloatColumn(name() + " * " + val);
            for (int i = 0; i < size(); i++) {
                result.append(FloatColumn.multiply(val, getFloat(i)));
            }
            return result;
        } else if (value instanceof Long || this instanceof LongColumn) {
            long val = (long) value;
            LongColumn result = new LongColumn(name() + " * " + val);
            for (int i = 0; i < size(); i++) {
                result.append(LongColumn.multiply(getLong(i), val));
            }
            return result;
        }
        int val = (int) value;
        IntColumn result = new IntColumn(name() + " * " + val);
        for (int i = 0; i < size(); i++) {
            result.append(IntColumn.multiply(getInt(i), val));
        }
        return result;
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
