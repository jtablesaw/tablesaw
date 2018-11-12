package tablesaw.ui;

import java.util.Comparator;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public abstract class AbstractTablesawColumnLabelProvider extends ColumnLabelProvider implements Comparator<Object> {

	protected abstract String getColumnName();

	protected String getColumnTitle() {
		return getColumnName();
	}

	protected Table getTable(final Object element) {
		return (element instanceof TablesawContentRow ? ((TablesawContentRow) element).getTable() : null);
	}

	protected Integer getTableRowNum(final Object element) {
		return (element instanceof TablesawContentRow ? ((TablesawContentRow) element).getTableRowNum() : null);
	}

	protected Object getColumnValue(final Column<?> column, final int rowNum) {
		return column.isMissing(rowNum) ? null : column.get(rowNum);
	}

	@Override
	public String getText(final Object element) {
		String s = "???";
		if (element instanceof TablesawContentRow) {
			final TablesawContentRow row = (TablesawContentRow) element;
			final Column<?> column = getColumn(row);
			if (column != null) {
				s = getColumnText(getColumnValue(column, row.getTableRowNum()));
			} else {
				final int rowNum = row.getRowNum();
				s = (rowNum >= 0 ? String.valueOf(rowNum + 1) : "");
			}
		}
		return s;
	}

	protected String getMissingValueLabel() {
		return "<missing>";
	}

	protected String getColumnText(final Object value) {
		return value != null ? value.toString() : getMissingValueLabel();
	}

	protected Column<?> getColumn(final Object element) {
		return (element instanceof TablesawContentRow ?getColumn((TablesawContentRow) element) : null);
	}

	protected Column<?> getColumn(final TablesawContentRow row) {
		final Table table = row.getTable();
		Column<?> column = null;
		final String columnName = getColumnName();
		if (columnName != null) {
			try {
				if (columnName != null) {
					column = table.column(table.columnIndex(columnName));
				}
			} catch (final Exception e) {
			}
		}
		return column;
	}

	@Override
	public int compare(final Object o1, final Object o2) {
		if (o1 instanceof TablesawContentRow && o2 instanceof TablesawContentRow) {
			final TablesawContentRow row1 = (TablesawContentRow) o1, row2 = (TablesawContentRow) o2;
			final Column<?> col1 = getColumn(row1), col2 = getColumn(row2);
			if (col1 != null && col2 != null) {
				final Object v1 = getColumnValue(col1, row1.getTableRowNum());
				final Object v2 = getColumnValue(col2, row2.getTableRowNum());
				if (v1 != null && v2 != null) {
					return ((Column<Object>) col1).compare(v1, v2);
				}
			}
			return row1.getRowNum() - row2.getRowNum();
		}
		return 0;
	}
}
