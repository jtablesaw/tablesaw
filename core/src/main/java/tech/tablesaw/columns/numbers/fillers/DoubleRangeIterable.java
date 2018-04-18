package tech.tablesaw.columns.numbers.fillers;

import it.unimi.dsi.fastutil.doubles.DoubleIterable;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;

public class DoubleRangeIterable implements DoubleIterable {

	private final double from, to, by;
	private final boolean including; 
	private final int count;

	private DoubleRangeIterable(double from, double to, boolean including, double by, int count) {
		this.from = from;
		this.to = to;
		this.including = including;
		this.by = by;
		this.count = count;
	}

	private static DoubleRangeIterable range(double from, double to, double by, int count) {
		return new DoubleRangeIterable(from, to, false, by, count);		
	}

	public static DoubleRangeIterable range(double from, double to, double by) {
		return range(from, to, by, -1);
	}

	public static DoubleRangeIterable range(double from, double to) {
		return range(from, to, 1.0);
	}
	
	public static DoubleRangeIterable range(double from, double by, int count) {
		return range(from, Double.NaN, by, count);
	}
	
	public static DoubleRangeIterable range(double from, int count) {
		return range(from, 1.0, count);
	}

	@Override
	public DoubleIterator iterator() {

		return new DoubleIterator() {

			double next = from;
			int num = 0;
			
			@Override
			public boolean hasNext() {
				return (count < 0 || num < count) && (Double.isNaN(to) || Math.abs(next - from) < Math.abs(to - from) || (including && next == to));
			}
			
			@Override
			public double nextDouble() {
				final double current = next;
				next += by;
				num++;
				return current;
			}
		};
	}	
}
