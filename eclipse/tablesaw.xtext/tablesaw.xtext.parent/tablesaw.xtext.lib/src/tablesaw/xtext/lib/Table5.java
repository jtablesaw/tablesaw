package tablesaw.xtext.lib;

import tech.tablesaw.columns.Column;

public class Table5<T1, T2, T3, T4, T5> extends Table4<T1, T2, T3, T4> {

	public Table5(final String name, final Column<T1> col1, final Column<T2> col2, final Column<T3> col3, final Column<T4> col4, final Column<T5> col5) {
		super(name, col1, col2, col3, col4);
		addColumns(col5);
	}

	public Column<T5> getColumn5() {
		return (Column<T5>) column(4);
	}

	public static class Row5<T1, T2, T3, T4, T5> extends Row4<T1, T2, T3, T4> {

		public final T5 t5;

		public Row5(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5) {
			super(t1, t2, t3, t4);
			this.t5 = t5;
		}
	}

	public void append(final Row5<T1, T2, T3, T4, T5> row) {
		super.append(row);
		getColumn5().append(row.t5);
	}
}
