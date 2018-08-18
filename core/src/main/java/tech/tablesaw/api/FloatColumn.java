package tech.tablesaw.api;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.FloatDataWrapper;
import tech.tablesaw.columns.numbers.NumericDataWrapper;

import java.util.Iterator;

import static tech.tablesaw.api.ColumnType.FLOAT;

public class FloatColumn extends NumberColumn<Float> implements NumericColumn<Float> {

    protected FloatColumn(final String name, FloatArrayList data) {
        super(FLOAT, name);
        setDataWrapper(new FloatDataWrapper(data));
    }

    protected FloatColumn(final String name, final NumericDataWrapper data) {
        super(data.type(), name);
        setDataWrapper(data);
    }

    public static FloatColumn create(final String name, final NumericDataWrapper data) {
        return new FloatColumn(name, data);
    }

    public static FloatColumn create(final String name) {
        return new FloatColumn(name, new FloatArrayList());
    }

    public static FloatColumn create(final String name, final float[] arr) {
        return new FloatColumn(name, new FloatArrayList(arr));
    }

    public static FloatColumn create(final String name, final int initialSize) {
        return new FloatColumn(name, new FloatArrayList(initialSize));
    }

    @Override
    protected NumberColumn<Float> createCol(String name, NumericDataWrapper data) {
	return FloatColumn.create(name, data);
    }

    @Override
    public Float get(int index) {
	return data.getFloat(index);
    }

    @Override
    public FloatColumn unique() {  // todo, this is broken. these should be floats
        final IntSet values = new IntOpenHashSet();
        for (int i = 0; i < size(); i++) {
            if (!isMissing(i)) {
                values.add(getInt(i));
            }
        }
        final FloatColumn column = FloatColumn.create(name() + " Unique values", values.size());
        for (int value : values) {
            column.append(value);
        }
        return column;
    }

    public FloatColumn append(float i) {
        data.append(i);
        return this;
    }

    public FloatColumn append(Float val) {
        this.append(val.intValue());
        return this;
    }

    @Override
    public FloatColumn emptyCopy() {
        return (FloatColumn) super.emptyCopy();
    }

    @Override
    public FloatColumn emptyCopy(final int rowSize) {
	return (FloatColumn) super.emptyCopy(rowSize);
    }

    @Override
    public FloatColumn copy() {
        return (FloatColumn) super.copy();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Float> iterator() {
        return (Iterator<Float>) data.iterator();
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
    public int compare(Float o1, Float o2) {
        return Float.compare(o1, o2);
    }

    @Override
    public FloatColumn set(int i, Float val) {
        return (FloatColumn) set(i, (float) val);
    }

    @Override
    public FloatColumn append(final Column<Float> column) {
        Preconditions.checkArgument(column.type() == this.type());
        final FloatColumn numberColumn = (FloatColumn) column;
        for (int i = 0; i < numberColumn.size(); i++) {
            append(numberColumn.getFloat(i));
        }
        return this;
    }

}
