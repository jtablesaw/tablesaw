package tablesaw.ui;

import java.util.ArrayList;
import java.util.Collection;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class BooleanColumnFilterSupport {

	private Table modelTable;
	private Table filterTable;

	public BooleanColumnFilterSupport() {
	}

	public void setModelTable(final Table modelTable) {
		this.modelTable = modelTable;
		updateFilterTable();
	}

	protected void updateFilterTable() {
		final Column<?>[] filterColumns = new Column[modelTable.columnCount()];
		for (int i = 0; i < filterColumns.length; i++) {
			final Column<?> column = modelTable.column(i);
			final BooleanColumn filterColumn = BooleanColumn.create(column.name());
			filterColumn.append(Boolean.TRUE);
			filterColumns[i] = filterColumn;
		}
		filterTable = Table.create(modelTable.name() + ".filter", filterColumns);
	}

	public Table getFilterTable() {
		return filterTable;
	}

	protected boolean isContentRow(final TablesawContentRow row) {
		return row.getTable() == modelTable;
	}

	public Collection<String> getColumnNames() {
		final Collection<String> columnNames = new ArrayList<String>();
		for (final Column<?> column : filterTable.columns()) {
			if (isColumnSelected(column)) {
				columnNames.add(column.name());
			}
		}
		return columnNames;
	}

	public boolean isColumnSelected(final Column<?> column) {
		return Boolean.TRUE.equals(column.get(0));
	}

	public boolean isColumnSelected(final int colNum) {
		return isColumnSelected(filterTable.column(colNum));
	}

	public boolean isColumnSelected(final String name) {
		return isColumnSelected(filterTable.column(name));
	}
}
