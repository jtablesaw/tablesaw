package tablesaw.xtext.lib;

import tech.tablesaw.columns.Column;

public class Table3<T1, T2, T3> extends Table2<T1, T2> {

	public Table3(final String name, final Column<T1> col1, final Column<T2> col2, final Column<T3> col3) {
		super(name, col1, col2);
		addColumns(col3);
	}

	public Column<T3> getColumn3() {
		return (Column<T3>) column(2);
	}

	public static class Row3<T1, T2, T3> extends Row2<T1, T2> {

		public final T3 t3;

		public Row3(final T1 t1, final T2 t2, final T3 t3) {
			super(t1, t2);
			this.t3 = t3;
		}
	}

	public void append(final Row3<T1, T2, T3> row) {
		super.append(row);
		getColumn3().append(row.t3);
	}
}
