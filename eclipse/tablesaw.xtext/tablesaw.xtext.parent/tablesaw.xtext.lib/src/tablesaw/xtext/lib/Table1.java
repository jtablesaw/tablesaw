package tablesaw.xtext.lib;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class Table1<T1> extends Table {

	public Table1(final String name, final Column<T1> col1) {
		super(name);
		addColumns(col1);
	}

	public Column<T1> getColumn1() {
		return (Column<T1>) column(0);
	}

	public static class Row1<T1> {

		public final T1 t1;

		public Row1(final T1 t1) {
			this.t1 = t1;
		}
	}

	public void append(final Row1<T1> row) {
		getColumn1().append(row.t1);
	}
}
