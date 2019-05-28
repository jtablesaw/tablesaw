package tech.tablesaw.columns.temporal.fillers;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Iterator;

public class TemporalRangeIterable<T extends Temporal> implements Iterable<T> {

    private final T from, to;
    private final long by;
    private final TemporalUnit byUnit;
    private final boolean including;
    private final int count;

    private TemporalRangeIterable(final T from, final T to, final boolean including, final long by,
            final TemporalUnit byUnit, final int count) {
        this.from = from;
        this.to = to;
        this.including = including;
        this.by = by;
        this.byUnit = byUnit;
        this.count = count;
    }

    private static <T extends Temporal> TemporalRangeIterable<T> range(final T from, final T to, final long by,
            final TemporalUnit byUnit, final int count) {
        return new TemporalRangeIterable<T>(from, to, false, by, byUnit, count);
    }

    public static <T extends Temporal> TemporalRangeIterable<T> range(final T from, final T to, final long by,
            final TemporalUnit byUnit) {
        return range(from, to, by, byUnit, -1);
    }

    public static <T extends Temporal> TemporalRangeIterable<T> range(final T from, final long by,
            final TemporalUnit byUnit, final int count) {
        return range(from, null, by, byUnit, count);
    }

    @Override
    public Iterator<T> iterator() {

        return new Iterator<T>() {

            T next = from;
            int num = 0;

            @Override
            public boolean hasNext() {
                return (count < 0 || num < count)
                        && (to == null || next.until(to, byUnit) > 0 || (including && next.equals(to)));
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                final T current = next;
                next = (T) next.plus(by, byUnit);
                num++;
                return current;
            }
        };
    }
}
