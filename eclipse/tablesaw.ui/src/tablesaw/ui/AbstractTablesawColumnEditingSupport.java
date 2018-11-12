package tablesaw.ui;

import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import tech.tablesaw.columns.Column;

public abstract class AbstractTablesawColumnEditingSupport extends EditingSupport {

	protected final AbstractTablesawColumnLabelProvider labelProvider;

	public AbstractTablesawColumnEditingSupport(final TableViewer viewer, final AbstractTablesawColumnLabelProvider labelProvider) {
		super(viewer);
		this.labelProvider = labelProvider;
	}

	protected TableViewer getTableViewer() {
		return ((TableViewer) getViewer());
	}

	@Override
	protected boolean canEdit(final Object element) {
		return true;
	}

	@Override
	protected Object getValue(final Object element) {
		final Integer rowNum = labelProvider.getTableRowNum(element);
		final Column<?> column = labelProvider.getColumn(element);
		if (column.isMissing(rowNum)) {
			return null;
		}
		return labelProvider.getColumnValue(column, rowNum);
	}

	@Override
	protected void setValue(final Object element, final Object inputValue) {
		final Integer rowNum = labelProvider.getTableRowNum(element);
		final Column<?> column = labelProvider.getColumn(element);
		try {
			setColumnValue(column, rowNum, inputValue);
			updateViewerAfterEdit(element);
		} catch (final Exception e) {
		}
	}

	protected void updateViewerAfterEdit(final Object element) {
		getViewer().update(element, null);
	}

	protected abstract void setColumnValue(Column<?> column, int rowNum, Object inputValue);
}
