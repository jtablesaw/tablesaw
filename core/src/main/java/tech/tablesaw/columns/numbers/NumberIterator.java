package tech.tablesaw.columns.numbers;

import java.util.Iterator;
import java.util.ListIterator;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;

public class NumberIterator {

    private IntArrayList iList;
    private LongArrayList lList;
    private DoubleArrayList dList;
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

    public NumberIterator(LongArrayList list) {
        this.lList = list;
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
        } else if (fList != null) {
            float nextFloat = ((FloatIterator) iterator).nextFloat();
            return FloatColumnType.isMissingValue(nextFloat) ? DoubleColumnType.missingValueIndicator() : nextFloat;
        } else {
            long nextLong = ((LongIterator) iterator).nextLong();
            return LongColumnType.isMissingValue(nextLong) ? DoubleColumnType.missingValueIndicator() : nextLong;
        }
    }

    public int nextInt() {
        if (dList != null) {
            return (int) ((DoubleIterator) iterator).nextDouble();
        } else if (iList != null){
            return ((IntIterator) iterator).nextInt();
        } else if (fList != null) {
            return (int) ((FloatIterator) iterator).nextFloat();
        } else {
            return (int) ((LongIterator) iterator).nextLong();
        }
    }

    public float nextFloat() {
        if (dList != null) {
            return (float) ((DoubleIterator) iterator).nextDouble();
        } else if (iList != null){
            return ((IntIterator) iterator).nextInt();
        } else if (fList != null) {
            return ((FloatIterator) iterator).nextFloat();
        } else {
            return ((LongIterator) iterator).nextLong();
        }
    }

    public long nextLong() {
        if (dList != null) {
            return (long) ((DoubleIterator) iterator).nextDouble();
        } else if (iList != null){
            return ((IntIterator) iterator).nextInt();
        } else if (fList != null) {
            return (long) ((FloatIterator) iterator).nextFloat();
        } else {
            return ((LongIterator) iterator).nextLong();
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
        } else if (lList != null) {
            return new Iterator<Double>() {
                LongIterator longIterator = lList.iterator();
                @Override
                public boolean hasNext() {
                    return longIterator.hasNext();
                }

                @Override
                public Double next() {
                    return (double) longIterator.nextLong();
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
