package tablesaw.xtext.lib;

import tech.tablesaw.columns.Column;

public class Table4<T1, T2, T3, T4> extends Table3<T1, T2, T3> {

	public Table4(final String name, final Column<T1> col1, final Column<T2> col2, final Column<T3> col3, final Column<T4> col4) {
		super(name, col1, col2, col3);
		addColumns(col4);
	}

	public Column<T4> getColumn4() {
		return (Column<T4>) column(3);
	}

	public static class Row4<T1, T2, T3, T4> extends Row3<T1, T2, T3> {

		public final T4 t4;

		public Row4(final T1 t1, final T2 t2, final T3 t3, final T4 t4) {
			super(t1, t2, t3);
			this.t4 = t4;
		}
	}

	public void append(final Row4<T1, T2, T3, T4> row) {
		super.append(row);
		getColumn4().append(row.t4);
	}
}
