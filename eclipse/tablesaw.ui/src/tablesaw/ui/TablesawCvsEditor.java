package tablesaw.ui;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import tablesaw.ui.expr.ExprSupport;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvReader;
import tech.tablesaw.io.csv.CsvWriteOptions;
import tech.tablesaw.io.csv.CsvWriter;

public class TablesawCvsEditor extends EditorPart implements TableProvider, ISelectionProvider {

	private TableViewer tablesawViewer;
	private Map<TableViewerColumn, AbstractTablesawColumnLabelProvider> tablesawViewerColumns;

	private Table modelTable;
	private Table rowFilterTable;
	private Table columnFilterTable;

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		if (input instanceof IFileEditorInput) {
			final IFile file = ((IFileEditorInput) input).getFile();
			setPartName(file.getName());
			loadCsv(file, null);
		}
	}

	protected void loadCsv(final IFile file, final IProgressMonitor monitor) {
		try {
			final CsvReadOptions csvOptions = new CsvReadOptions.Builder(new BufferedReader(new InputStreamReader(file.getContents()))).build();
			modelTable = new CsvReader().read(csvOptions);
			exprViewerRowFilterSupport.setModelTable(modelTable);
			booleanViewerColumnFilterSupport.setModelTable(modelTable);
			columnFilterTable = booleanViewerColumnFilterSupport.getFilterTable();
			rowFilterTable = exprViewerRowFilterSupport.getFilterTable();
		} catch (final CoreException e) {
		} catch (final IOException e) {
		}
	}

	@Override
	public void doSave(final IProgressMonitor monitor) {
		if (modelTable != null) {
			saveCsv(((IFileEditorInput) getEditorInput()).getFile(), monitor);
		}
	}

	@Override
	public void doSaveAs() {
	}

	protected void saveCsv(final IFile file, final IProgressMonitor monitor) {
		try {
			final ByteArrayOutputStream output = new ByteArrayOutputStream();
			final CsvWriteOptions csvOptions = new CsvWriteOptions.Builder(output).build();
			new CsvWriter(modelTable, csvOptions).write();
			file.setContents(new ByteArrayInputStream(output.toByteArray()), 0, monitor);
			setDirty(false);
		} catch (final CoreException e) {
		}
	}

	private boolean dirty = false;

	protected void setDirty(final boolean dirty) {
		final boolean changed = this.dirty != dirty;
		this.dirty = dirty;
		if (changed) {
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	private Combo exprLang;
	//	private Text exprText;
	//	private Label exprResult;

	private final ExprViewerRowFilterSupport exprViewerRowFilterSupport = new ExprViewerRowFilterSupport();
	private final BooleanColumnFilterSupport booleanViewerColumnFilterSupport = new BooleanColumnFilterSupport();

	@Override
	public void createPartControl(final Composite parent) {
		final GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		parent.setLayout(layout);
		final Composite exprParent = new Composite(parent, SWT.NONE);
		exprParent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		exprParent.setLayout(new GridLayout(2, false));
		final Label label = new Label(exprParent, SWT.NONE);
		label.setText("Expression language: ");
		exprLang = new Combo(exprParent, SWT.DROP_DOWN);
		for (final ExprSupport es : Activator.getInstance().getExprSupports()) {
			exprLang.add(es.getLang());
			if (exprLang.getSelectionIndex() < 0) {
				exprLang.select(0);
			}
		}
		//		exprText = new Text(exprParent, SWT.NONE);
		//		exprText.setText("<expression goes here>");
		//		final Button exprButton = new Button(exprParent, SWT.NONE);
		//		exprButton.setText("Evaluate");
		//		exprButton.addSelectionListener(new SelectionAdapter() {
		//			@Override
		//			public void widgetSelected(final SelectionEvent e) {
		//				filterViewer();
		//				//				evalExpr(exprText.getText(), 0, getExprSupport(exprLang.getSelectionIndex()));
		//			}
		//		});
		//		exprResult = new Label(exprParent, SWT.NONE);
		//		exprResult.setText("<No result yet>");
		//		exprResult.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		tablesawViewer = new TableViewer(parent, SWT.VIRTUAL | SWT.V_SCROLL);
		if (modelTable != null) {
			tablesawViewer.setContentProvider(new AbstractTablesawContentProvider() {
				@Override
				protected boolean isContentTable(final Table table) {
					return TablesawCvsEditor.this.isContentTable(table);
				}
			});
		}
		tablesawViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tablesawViewer.getTable().setHeaderVisible(true);
		tablesawViewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(final Viewer viewer, final Object e1, final Object e2) {
				return compareElements(e1, e2);
			}
		});
		tablesawViewerColumns = new HashMap<TableViewerColumn, AbstractTablesawColumnLabelProvider>();
		addTableColumn(null);
		for (int colNum = 0; colNum < modelTable.columnCount(); colNum++) {
			final Column<?> col = modelTable.column(colNum);
			addTableColumn(col);
		}

		exprViewerRowFilterSupport.setExprSupport(getExprSupport(exprLang.getSelectionIndex()));

		tablesawViewer.setFilters(exprViewerRowFilterSupport.getViewerFilter());
		tablesawViewer.setInput(new Table[]{
				booleanViewerColumnFilterSupport.getFilterTable(),
				exprViewerRowFilterSupport.getFilterTable(),
				modelTable
		});

		parent.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				for (final TableViewerColumn column : tablesawViewerColumns.keySet()) {
					column.getColumn().pack();
				}
			}
		});
		tablesawViewer.addSelectionChangedListener(selectionChangeListener);
	}

	protected boolean isContentTable(final Table table) {
		return table == modelTable;
	}

	protected ExprSupport getExprSupport(final int num) {
		return Activator.getInstance().getExprSupports()[num];
	}

	protected ExprSupport getExprSupport(final String lang) {
		for (final ExprSupport exprSupport : Activator.getInstance().getExprSupports()) {
			if (lang.equals(exprSupport.getLang())) {
				return exprSupport;
			}
		}
		return null;
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
		if (column != null) {
			final TextualColumnEditingSupport editingSupport = new TextualColumnEditingSupport(tablesawViewer, labelProvider) {
				@Override
				protected void updateViewerAfterEdit(final Object element) {
					super.updateViewerAfterEdit(element);
					if (element instanceof TablesawContentRow) {
						final Table table = ((TablesawContentRow) element).getTable();
						if (table == columnFilterTable) {
							tableProviderHelper.fireTableChanged(TablesawCvsEditor.this);
						} else if (table == rowFilterTable) {
							exprViewerRowFilterSupport.updateViewerFilter();
							filterViewer();
							tableProviderHelper.fireTableDataChanged(TablesawCvsEditor.this);
						} else if (isContentTable(table)) {
							setDirty(true);
						}
					}
				}
			};
			editingSupport.setColumnTypeEditor(ColumnType.BOOLEAN, new BooleanTextCellEditor(tablesawViewer.getTable()));

			if (editingSupport != null) {
				viewerColumn.setEditingSupport(editingSupport);
			}
		}
		viewerColumn.getColumn().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				sortViewer(viewerColumn.getColumn());
			}
		});
		tablesawViewerColumns.put(viewerColumn, labelProvider);
	}

	protected void sortViewer(final TableColumn swtColumn) {
		final org.eclipse.swt.widgets.Table table = swtColumn.getParent();
		if (swtColumn.equals(table.getSortColumn())) {
			table.setSortDirection(table.getSortDirection() == SWT.UP ? SWT.DOWN : SWT.UP);
		} else {
			table.setSortColumn(swtColumn);
			table.setSortDirection(SWT.UP);
		}
		tablesawViewer.refresh();
	}

	protected void filterViewer() {
		tablesawViewer.refresh();
	}

	protected boolean isContentRow(final TablesawContentRow row) {
		return isContentTable(row.getTable());
	}

	protected int compareElements(final Object e1, final Object e2) {
		if (e1 instanceof TablesawContentRow && e2 instanceof TablesawContentRow) {
			final TablesawContentRow row1 = (TablesawContentRow) e1, row2 = (TablesawContentRow) e2;
			if (isContentRow(row1) && isContentRow(row2)) {
				final org.eclipse.swt.widgets.Table table = tablesawViewer.getTable();
				AbstractTablesawColumnLabelProvider labelProvider = null;
				for (final TableViewerColumn viewerColumn : tablesawViewerColumns.keySet()) {
					if (viewerColumn.getColumn() == table.getSortColumn()) {
						labelProvider = tablesawViewerColumns.get(viewerColumn);
						break;
					}
				}
				if (labelProvider != null) {
					final int result = labelProvider.compare(e1, e2);
					return table.getSortDirection() == SWT.UP ? result : -result;
				}
			}
			return row1.getRowNum() - row2.getRowNum();
		}
		return 0;
	}

	@Override
	public void setFocus() {
		tablesawViewer.getTable().setFocus();
	}

	// TableProvider

	@Override
	public Table getTable() {
		final Table table = Table.create(modelTable.name());
		final Collection<Column<?>> columns = new ArrayList<Column<?>>();
		final Collection<Integer> rowNums = new ArrayList<>();
		getRows(rowNums);
		for (int colNum = 0; colNum < columnFilterTable.columnCount(); colNum++) {
			if (Boolean.TRUE.equals(columnFilterTable.column(colNum).get(0))) {
				final Column<?> modelColumn = modelTable.column(colNum);
				final Column<?> column = modelColumn.emptyCopy();
				for (final int rowNum : rowNums) {
					final Object element = modelColumn.get(rowNum);
					((Column<Object>) column).append(element);
				}
				columns.add(column);
			}
		}
		table.addColumns(columns.toArray(new Column<?>[columns.size()]));
		return table;
	}

	protected int getRows(final Collection<Integer> rows) {
		int rowCount = 0;
		final Object input = tablesawViewer.getInput();
		element: for (final Object element : ((IStructuredContentProvider) tablesawViewer.getContentProvider()).getElements(input)) {
			if (element instanceof TablesawContentRow) {
				final TablesawContentRow row = (TablesawContentRow) element;
				if (row.getTable() == modelTable) {
					for (final ViewerFilter viewerFilter : tablesawViewer.getFilters()) {
						if (! viewerFilter.select(tablesawViewer, input, element)) {
							continue element;
						}
					}
					if (rows != null) {
						rows.add(row.getTableRowNum());
					}
					rowCount++;
				}
			}
		}
		return rowCount;
	}


	TableProviderHelper tableProviderHelper = new TableProviderHelper();

	@Override
	public void addTableDataProviderListener(final TableProvider.Listener listener) {
		tableProviderHelper.addTableDataProviderListener(listener);
	}

	@Override
	public void removeTableDataProviderListener(final TableProvider.Listener listener) {
		tableProviderHelper.removeTableDataProviderListener(listener);
	}

	// ISelectionProvider

	@Override
	public void setSelection(final ISelection selection) {
		// not sure this makes much sense
		tablesawViewer.setSelection(selection);
	}

	@Override
	public ISelection getSelection() {
		return tablesawViewer.getSelection();
	}

	// forward selection changes
	private final ISelectionChangedListener selectionChangeListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(final SelectionChangedEvent event) {
			if (selectionListeners != null) {
				for (final ISelectionChangedListener selectionChangedListener : selectionListeners) {
					selectionChangedListener.selectionChanged(event);
				}
			}
		}
	};

	private Collection<ISelectionChangedListener> selectionListeners;

	@Override
	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
		if (selectionListeners == null) {
			selectionListeners = new ArrayList<ISelectionChangedListener>();
		}
		selectionListeners.add(listener);
	}

	@Override
	public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
		if (selectionListeners != null) {
			selectionListeners.remove(listener);
		}
	}
}
