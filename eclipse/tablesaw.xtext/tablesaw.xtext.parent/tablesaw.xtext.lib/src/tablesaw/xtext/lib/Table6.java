package tablesaw.xtext.lib;

import tech.tablesaw.columns.Column;

public class Table6<T1, T2, T3, T4, T5, T6> extends Table5<T1, T2, T3, T4, T5> {

	public Table6(final String name, final Column<T1> col1, final Column<T2> col2, final Column<T3> col3, final Column<T4> col4, final Column<T5> col5, final Column<T6> col6) {
		super(name, col1, col2, col3, col4, col5);
		addColumns(col6);
	}

	public Column<T6> getColumn6() {
		return (Column<T6>) column(5);
	}

	public static class Row6<T1, T2, T3, T4, T5, T6> extends Row5<T1, T2, T3, T4, T5> {

		public final T6 t6;

		public Row6(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6) {
			super(t1, t2, t3, t4, t5);
			this.t6 = t6;
		}
	}

	public void append(final Row6<T1, T2, T3, T4, T5, T6> row) {
		super.append(row);
		getColumn5().append(row.t5);
	}
}
