package tablesaw.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import tablesaw.ui.expr.ExprSupport;
import tablesaw.ui.expr.PreparedExpr;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class ExprViewerRowFilterSupport {

	private Table modelTable;
	private Table filterTable;

	public ExprViewerRowFilterSupport() {
	}

	public void setModelTable(final Table modelTable) {
		this.modelTable = modelTable;
		updateFilterTable();
	}

	protected void updateFilterTable() {
		final Column<?>[] filterColumns = new Column[modelTable.columnCount()];
		for (int i = 0; i < filterColumns.length; i++) {
			final Column<?> column = modelTable.column(i);
			final StringColumn filterColumn = StringColumn.create(column.name());
			filterColumn.append("");
			filterColumns[i] = filterColumn;
		}
		filterTable = Table.create(modelTable.name() + ".filter", filterColumns);
	}

	public Table getFilterTable() {
		return filterTable;
	}

	private ExprSupport exprSupport;

	public void setExprSupport(final ExprSupport exprSupport) {
		this.exprSupport = exprSupport;
	}

	public void setExprSupport(final String lang) {
		for (final ExprSupport exprSupport : Activator.getInstance().getExprSupports()) {
			if (lang.equals(exprSupport.getLang())) {
				setExprSupport(exprSupport);
				break;
			}
		}
	}

	private final Map<String, Collection<PreparedExpr>> preparedFilterExprs = new HashMap<String, Collection<PreparedExpr>>();

	public void updateViewerFilter() {
		preparedFilterExprs.clear();
		final Map<String, ColumnType> varTypes = new HashMap<String, ColumnType>();
		for (final Column<?> col : modelTable.columns()) {
			varTypes.put(col.name(), col.type());
		}
		for (final Column<?> col : filterTable.columns()) {
			final Collection<PreparedExpr> filterExprs = new ArrayList<PreparedExpr>();
			for (int rowNum = 0; rowNum < filterTable.rowCount(); rowNum++) {
				final String filterExpr = col.getString(rowNum);
				if (filterExpr != null && filterExpr.trim().length() > 0) {
					final PreparedExpr preparedExpr = exprSupport.prepareExpr(filterExpr, varTypes, col.name());
					if (preparedExpr.getDiagnostics().isEmpty()) {
						filterExprs.add(preparedExpr);
					}
				}
			}
			preparedFilterExprs.put(col.name(), filterExprs);
		}
	}

	protected boolean isContentRow(final TablesawContentRow row) {
		return row.getTable() == modelTable;
	}

	public ViewerFilter getViewerFilter() {
		return new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				if (element instanceof TablesawContentRow) {
					final TablesawContentRow row = (TablesawContentRow) element;
					if (isContentRow(row)) {
						return ExprViewerRowFilterSupport.this.select(row.getTable(), row.getTableRowNum());
					}
				}
				return true;
			}
		};
	}

	protected boolean select(final Table table, final int rowNum) {
		final Map<String, Object> varValues = new HashMap<String, Object>();
		for (final Column<?> col : table.columns()) {
			Object value = null;
			if (col instanceof NumericColumn<?>) {
				value = ((NumericColumn<?>) col).getDouble(rowNum);
			} else if (col.type() == ColumnType.STRING) {
				value = col.getString(rowNum);
			}
			varValues.put(col.name(), value);
		}
		for (final Column<?> col : table.columns()) {
			final Collection<PreparedExpr> filterExprs = preparedFilterExprs.get(col.name());
			if (filterExprs != null) {
				for (final PreparedExpr filterExpr : filterExprs) {
					final Object result = exprSupport.evalExpr(filterExpr, varValues);
					if (filterExpr.getDiagnostics().isEmpty() && Boolean.TRUE.equals(result)) {
						continue;
					}
					return false;
				}
			}
		}
		return true;
	}
}
