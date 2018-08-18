package tech.tablesaw.api;

import static tech.tablesaw.api.ColumnType.INTEGER;

import java.util.Iterator;

import com.google.common.base.Preconditions;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.IntDataWrapper;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.columns.numbers.NumericDataWrapper;

public class IntColumn extends NumberColumn<Integer> implements NumericColumn<Integer>, CategoricalColumn<Integer> {

    protected IntColumn(final String name, IntArrayList data) {
        super(INTEGER, name);
        this.printFormatter = NumberColumnFormatter.ints();
        setDataWrapper(new IntDataWrapper(data));
    }

    protected IntColumn(final String name, final NumericDataWrapper data) {
        super(data.type(), name);
        setDataWrapper(data);
    }

    public static IntColumn create(final String name, final NumericDataWrapper data) {
        return new IntColumn(name, data);
    }

    public static IntColumn create(final String name) {
        return new IntColumn(name, new IntArrayList());
    }

    public static IntColumn create(final String name, final int[] arr) {
        return new IntColumn(name, new IntArrayList(arr));
    }

    public static IntColumn create(final String name, final int initialSize) {
        return new IntColumn(name, new IntArrayList(initialSize));
    }

    public static IntColumn createWithIntegers(String name) {
        return new IntColumn(name, new IntArrayList(DEFAULT_ARRAY_SIZE));
    }

    public static IntColumn createWithIntegers(String name, int size) {
        return new IntColumn(name, new IntArrayList(size));
    }

    /**
     * Returns a new numeric column initialized with the given name and size. The values in the column are
     * integers beginning at startsWith and continuing through size (exclusive), monotonically increasing by 1
     * TODO consider a generic fill function including steps or random samples from various distributions
     */
    public static IntColumn indexColumn(final String columnName, final int size, final int startsWith) {
        final IntColumn indexColumn = IntColumn.createWithIntegers(columnName, size);
        for (int i = 0; i < size; i++) {
            indexColumn.append(i + startsWith);
        }
        return indexColumn;
    }

    @Override
    protected NumberColumn<Integer> createCol(String name, NumericDataWrapper data) {
	return IntColumn.create(name, data);
    }

    @Override
    public Integer get(int index) {
	return data.getInt(index);
    }

    @Override
    public IntColumn unique() {
        final IntSet values = new IntOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                values.add(getInt(i));
            }
        }
        final IntColumn column = IntColumn.create(name() + " Unique values", values.size());
        for (int value : values) {
            column.append(value);
        }
        return column;
    }

    public IntColumn append(int i) {
        data.append(i);
        return this;
    }

    public IntColumn append(Integer val) {
        this.append(val.intValue());
        return this;
    }

    @Override
    public IntColumn emptyCopy() {
        return (IntColumn) super.emptyCopy();
    }

    @Override
    public IntColumn emptyCopy(final int rowSize) {
	return (IntColumn) super.emptyCopy(rowSize);
    }

    @Override
    public IntColumn copy() {
        return (IntColumn) super.copy();
    }

    @Override
    public int[] asIntArray() {  // TODO: Need to figure out how to handle NaN -> Maybe just use a list with nulls?
        final int[] result = new int[size()];
        for (int i = 0; i < size(); i++) {
            result[i] = roundInt(i);
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Integer> iterator() {
        return (Iterator<Integer>) data.iterator();
    }

    @Override
    public Object[] asObjectArray() {
        final Integer[] output = new Integer[size()];
        for (int i = 0; i < size(); i++) {
            output[i] = getInt(i);
        }
        return output;
    }

    @Override
    public int compare(Integer o1, Integer o2) {
        return Integer.compare(o1, o2);
    }

    @Override
    public IntColumn set(int i, Integer val) {
        return (IntColumn) set(i, (int) val);
    }

    @Override
    public IntColumn append(final Column<Integer> column) {
        Preconditions.checkArgument(column.type() == this.type());
        final IntColumn numberColumn = (IntColumn) column;
        for (int i = 0; i < numberColumn.size(); i++) {
            append(numberColumn.getInt(i));
        }
        return this;
    }

}
