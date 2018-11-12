package tablesaw.xtext.lib;

import tech.tablesaw.columns.Column;

public class Table2<T1, T2> extends Table1<T1> {

	public Table2(final String name, final Column<T1> col1, final Column<T2> col2) {
		super(name, col1);
		addColumns(col2);
	}

	public Column<T2> getColumn2() {
		return (Column<T2>) column(1);
	}

	public static class Row2<T1, T2> extends Row1<T1> {

		public final T2 t2;

		public Row2(final T1 t1, final T2 t2) {
			super(t1);
			this.t2 = t2;
		}
	}

	public void append(final Row2<T1, T2> row) {
		super.append(row);
		getColumn2().append(row.t2);
	}
}
