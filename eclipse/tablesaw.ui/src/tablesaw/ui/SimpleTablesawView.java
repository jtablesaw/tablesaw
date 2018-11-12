package tablesaw.ui;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class SimpleTablesawView extends AbstractTablesawView {

	private Composite tableViewerParent;
	private TableViewer tablesawViewer;

	public SimpleTablesawView() {
		super(false);
	}

	@Override
	protected void createTableDataControls(final Composite parent) {
		tableViewerParent = parent;
		updateTableControls();
	}

	@Override
	protected void createConfigControls(final Composite configParent) {
		createWorkbenchDataProvideSelector("Source: ", configParent);
	}

	protected void createTableViewer(final Composite parent) {
		final Table table = getTableViewerInput();
		if (table != null) {
			tablesawViewer = new TableViewer(parent, SWT.VIRTUAL | SWT.V_SCROLL);
			tablesawViewer.setContentProvider(new AbstractTablesawContentProvider() {
				@Override
				protected boolean isContentTable(final Table table) {
					return true;
				}
			});
			tablesawViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			tablesawViewer.getTable().setHeaderVisible(true);

			addTableColumn(null);
			for (int colNum = 0; colNum < table.columnCount(); colNum++) {
				final Column<?> col = table.column(colNum);
				addTableColumn(col);
			}
			tablesawViewer.setInput(table);

			parent.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					final org.eclipse.swt.widgets.Table swtTable = tablesawViewer.getTable();
					if (swtTable != null && (! swtTable.isDisposed())) {
						for (final TableColumn column : swtTable.getColumns()) {
							column.pack();
						}
					}
				}
			});
		}
	}

	protected Table getTableViewerInput() {
		return getViewTable();
	}

	@Override
	protected void updateTableControls() {
		if (tablesawViewer != null) {
			tablesawViewer.getTable().dispose();
			tablesawViewer = null;
		}
		createTableViewer(tableViewerParent);
		tableViewerParent.layout(true);
		//		tableViewerParent.redraw();
		//		final Control swtTable = tablesawViewer.getControl();
		//		if (swtTable != null && (! swtTable.isDisposed())) {
		//			swtTable.redraw();
		//		}
	}

	protected void addTableColumn(final Column<?> column) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tablesawViewer, SWT.NONE);
		final AbstractTablesawColumnLabelProvider labelProvider = new AbstractTablesawColumnLabelProvider() {
			@Override
			protected String getColumnName() {
				return (column != null ? column.name() : null);
			}
			@Override
			protected String getColumnTitle() {
				final String name = getColumnName();
				return (name != null ? name : "#");
			}
		};
		viewerColumn.setLabelProvider(labelProvider);
		viewerColumn.getColumn().setText(labelProvider.getColumnTitle());
		viewerColumn.getColumn().setWidth(labelProvider.getColumnTitle().length() * 10);
	}
}
