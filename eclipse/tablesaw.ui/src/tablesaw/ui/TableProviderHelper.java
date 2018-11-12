package tablesaw.ui;

import java.util.ArrayList;
import java.util.Collection;

import tech.tablesaw.api.Table;

public class TableProviderHelper implements TableProvider {

	private Table table = null;

	public void setTable(final Table table) {
		this.table = table;
	}

	@Override
	public Table getTable() {
		return table;
	}

	private Collection<TableProvider.Listener> tableProviderListeners;

	@Override
	public void addTableDataProviderListener(final TableProvider.Listener listener) {
		if (tableProviderListeners == null) {
			tableProviderListeners = new ArrayList<TableProvider.Listener>();
		}
		tableProviderListeners.add(listener);
	}

	@Override
	public void removeTableDataProviderListener(final TableProvider.Listener listener) {
		if (tableProviderListeners != null) {
			tableProviderListeners.remove(listener);
		}
	}

	public void fireTableDataChanged() {
		fireTableDataChanged(this);
	}
	public void fireTableDataChanged(final TableProvider tableProvider) {
		if (tableProviderListeners != null) {
			for (final TableProvider.Listener tableProviderListener : tableProviderListeners) {
				tableProviderListener.tableDataChanged(tableProvider);
			}
		}
	}

	public void fireTableChanged() {
		fireTableChanged(this);
	}
	public void fireTableChanged(final TableProvider tableProvider) {
		if (tableProviderListeners != null) {
			for (final TableProvider.Listener tableProviderListener : tableProviderListeners) {
				tableProviderListener.tableChanged(tableProvider);
			}
		}
	}
}
