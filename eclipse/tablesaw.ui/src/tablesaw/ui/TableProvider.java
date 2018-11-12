package tablesaw.ui;

import tech.tablesaw.api.Table;

public interface TableProvider {

	public Table getTable();

	public static interface Listener {
		public void tableChanged(TableProvider tableProvider);
		public void tableDataChanged(TableProvider tableProvider);
	}

	public void addTableDataProviderListener(Listener listener);
	public void removeTableDataProviderListener(Listener listener);
}
