package tablesaw.ui.nattable;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class TablesawDataProvider implements IDataProvider {

	private Table table;
	// null = normal, TRUE = column header, FALSE = row header
	private final Boolean mode;

	public TablesawDataProvider(final Table table) {
		this(table, null);
	}

	public TablesawDataProvider(final Table table, final Boolean mode) {
		super();
		setTable(table);
		this.mode = mode;
	}

	public void setTable(final Table table) {
		this.table = table;
	}

	@Override
	public Object getDataValue(final int columnIndex, final int rowIndex) {
		if (table != null) {
			final Column<?> column = table.column(columnIndex);
			return  (mode == null ? column.get(rowIndex) : (mode ? column.name() : rowIndex));
		}
		return null;
	}

	@Override
	public void setDataValue(final int columnIndex, final int rowIndex, final Object newValue) {
		if (table != null) {
			final Column<Object> column = (Column<Object>) table.column(columnIndex);
			if (mode == null) {
				final Object oldValue = column.get(rowIndex);
				column.set(rowIndex, newValue);
				if (oldValue != newValue && (oldValue == null || (! oldValue.equals(newValue)))) {
					fireCellChanged(rowIndex, columnIndex, oldValue, newValue);
				}
			} else if (mode) {
				column.setName(String.valueOf(newValue));
			}
		}
	}

	@Override
	public int getColumnCount() {
		return (table != null ? (Boolean.FALSE.equals(mode) ? 1 : table.columnCount()) : 0);
	}

	@Override
	public int getRowCount() {
		return (table != null ? (Boolean.TRUE.equals(mode) ? 1 : table.rowCount()) : 0);
	}

	//

	public static interface Listener {
		public void cellChanged(int row, int column, Object oldValue, Object newValue);
	}

	private Collection<Listener> listeners = null;

	public void addTableChangeListener(final Listener listener) {
		if (listeners == null) {
			listeners = new ArrayList<TablesawDataProvider.Listener>();
		}
		listeners.add(listener);
	}

	public void removeTableChangeListener(final Listener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	protected void fireCellChanged(final int row, final int column, final Object oldValue, final Object newValue) {
		for (final Listener listener : listeners) {
			listener.cellChanged(row, column, oldValue, newValue);
		}
	}
}
