package tech.tablesaw.columns.numbers;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;

import java.util.Iterator;

public class NumberIterator implements Iterable<Double> {

    private DoubleArrayList dList;
    private IntArrayList iList;

    private final Object iterator;

    public NumberIterator(DoubleArrayList list) {
        this.dList = list;
        this.iterator = list.iterator();
    }

    public NumberIterator(IntArrayList list) {
        this.iList = list;
        this.iterator = list.iterator();
    }

    public double next() {
        if (dList != null) {
            return ((DoubleIterator) iterator).nextDouble();
        } else {
            return ((IntIterator) iterator).nextInt();
        }
    }

    public int nextInt() {
        if (dList != null) {
            return (int) ((DoubleIterator) iterator).nextDouble();
        } else {
            return ((IntIterator) iterator).nextInt();
        }
    }

    public int nextFloat() {
        if (dList != null) {
            return (int) ((FloatIterator) iterator).nextFloat();
        } else {
            return ((IntIterator) iterator).nextInt();
        }
    }

    public boolean hasNext() {
        if (dList != null) {
            return ((DoubleIterator) iterator).hasNext();
        } else {
            return ((IntIterator) iterator).hasNext();
        }
    }

    public Iterator<Double> iterator() {
        if (dList != null) {
            return dList.iterator();
        } else {
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
        }
    }
}
