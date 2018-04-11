package tech.tablesaw.api;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleIterable;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleSet;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntSet;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.columns.numbers.NumberFilters;
import tech.tablesaw.columns.numbers.NumberMapUtils;
import tech.tablesaw.columns.numbers.NumberReduceUtils;
import tech.tablesaw.columns.numbers.Stats;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.filtering.predicates.DoubleBiPredicate;
import tech.tablesaw.filtering.predicates.DoubleRangePredicate;
import tech.tablesaw.selection.Selection;

import java.text.NumberFormat;
import java.util.function.DoublePredicate;

import static tech.tablesaw.api.ColumnType.NUMBER;

public interface NumberColumn extends Column, DoubleIterable, IntConvertibleColumn, NumberMapUtils, NumberReduceUtils, NumberFilters, CategoricalColumn {
    double MISSING_VALUE = (Double) NUMBER.getMissingValue();

    static boolean valueIsMissing(double value) {
        return Double.isNaN(value);
    }

    @Override
    boolean isMissing(int rowNumber);

    void setPrintFormatter(NumberFormat format, String missingValueString);

    void setPrintFormatter(NumberColumnFormatter formatter);

    int size();

    @Override
    Table summary();

    Stats stats();

    DoubleArrayList top(int n);

    DoubleArrayList bottom(int n);

    @Override
    Column unique();

    double firstElement();

    void append(float f);

    void append(double d);

    @Override
    String getString(int row);

    @Override
    double getDouble(int row);

    @Override
    String getUnformattedString(int row);

    @Override
    NumberColumn emptyCopy();

    @Override
    NumberColumn emptyCopy(int rowSize);

    NumberColumn lead(int n);

    NumberColumn lag(int n);

    @Override
    NumberColumn copy();

    @Override
    void clear();

    @Override
    void sortAscending();

    @Override
    void sortDescending();

    @Override
    boolean isEmpty();

    @Override
    void appendCell(String object);

    Integer roundInt(int i);

    long getLong(int i);

    @Override
    IntComparator rowComparator();

    double get(int index);

    void set(int r, double value);

    void set(Selection rowSelection, double newValue);

    double[] asDoubleArray();

    @Override
    void append(Column column);

    @Override
    DoubleIterator iterator();

    @Override
    NumberColumn where(Filter filter);

    NumberColumn where(Selection selection);

    Selection eval(DoublePredicate predicate);

    Selection eval(DoubleBiPredicate predicate, NumberColumn otherColumn);

    Selection eval(DoubleBiPredicate predicate, Number value);

    Selection eval(DoubleRangePredicate predicate, Number rangeStart, Number rangeEnd);

    @Override
    Selection isIn(Number... numbers);

    @Override
    Selection isNotIn(Number... numbers);

    DoubleSet asSet();

    boolean contains(double value);

    @Override
    int byteSize();

    @Override
    byte[] asBytes(int rowNumber);

    @Override
    int[] asIntArray();

    @Override
    IntSet asIntegerSet();

    @Override
    DoubleList dataInternal();
}
