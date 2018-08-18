package tech.tablesaw.columns.numbers;

import java.util.Iterator;
import java.util.ListIterator;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;

public class NumberIterator {

    private DoubleArrayList dList;
    private IntArrayList iList;
    private FloatArrayList fList;

    private final ListIterator<?> iterator;

    public NumberIterator(DoubleArrayList list) {
        this.dList = list;
        this.iterator = list.iterator();
    }

    public NumberIterator(FloatArrayList list) {
        this.fList = list;
        this.iterator = list.iterator();
    }

    public NumberIterator(IntArrayList list) {
        this.iList = list;
        this.iterator = list.iterator();
    }

    public int nextIndex() {
        return iterator.nextIndex();
    }

    public int previousIndex() {
        return iterator.previousIndex();
    }

    public double next() {
        if (dList != null) {
            return ((DoubleIterator) iterator).nextDouble();
        } else if (iList != null){
            int nextInt = ((IntIterator) iterator).nextInt();
            return IntColumnType.isMissingValue(nextInt) ? DoubleColumnType.missingValueIndicator() : nextInt;
        } else {
            float nextFloat = ((FloatIterator) iterator).nextFloat();
            return FloatColumnType.isMissingValue(nextFloat) ? DoubleColumnType.missingValueIndicator() : nextFloat;
        }
    }

    public int nextInt() {
        if (dList != null) {
            return (int) ((DoubleIterator) iterator).nextDouble();
        } else if (iList != null){
            return ((IntIterator) iterator).nextInt();
        } else {
            return (int) ((FloatIterator) iterator).nextFloat();
        }
    }

    public float nextFloat() {
        if (dList != null) {
            return (float) ((DoubleIterator) iterator).nextDouble();
        } else if (iList != null){
            return ((IntIterator) iterator).nextInt();
        } else {
            return ((FloatIterator) iterator).nextFloat();
        }
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Iterator<Double> iterator() {
        if (dList != null) {
            return dList.iterator();
        } else if (iList != null) {
            return new Iterator<Double>() {
                IntIterator intIterator = iList.iterator();
                @Override
                public boolean hasNext() {
                    return intIterator.hasNext();
                }

                @Override
                public Double next() {
                    return (double) intIterator.nextInt();
                }
            };
        } else {
            return new Iterator<Double>() {
                FloatIterator floatIterator = fList.iterator();
                @Override
                public boolean hasNext() {
                    return floatIterator.hasNext();
                }

                @Override
                public Double next() {
                    return (double) floatIterator.nextFloat();
                }
            };
        }
    }
}
