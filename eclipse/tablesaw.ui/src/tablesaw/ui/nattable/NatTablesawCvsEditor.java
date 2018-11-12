package tablesaw.ui.nattable;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import tablesaw.ui.Activator;
import tablesaw.ui.BooleanColumnFilterSupport;
import tablesaw.ui.ExprViewerRowFilterSupport;
import tablesaw.ui.TableProvider;
import tablesaw.ui.TableProviderHelper;
import tablesaw.ui.expr.ExprSupport;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.io.csv.CsvReader;
import tech.tablesaw.io.csv.CsvWriteOptions;
import tech.tablesaw.io.csv.CsvWriter;

public class NatTablesawCvsEditor extends EditorPart implements TableProvider, ISelectionProvider {

	private Table modelTable;

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

	//	private Combo exprLang;
	//	private Text exprText;
	//	private Label exprResult;

	private final ExprViewerRowFilterSupport exprViewerRowFilterSupport = new ExprViewerRowFilterSupport();
	private final BooleanColumnFilterSupport booleanViewerColumnFilterSupport = new BooleanColumnFilterSupport();

	private NatTablesawViewer natTablesawViewer;

	@Override
	public void createPartControl(final Composite parent) {
		natTablesawViewer = new NatTablesawViewer();
		natTablesawViewer.setEditable(true);
		natTablesawViewer.createPartControl(parent);
		natTablesawViewer.setInput(modelTable);
		natTablesawViewer.addTableChangeListener(new TablesawDataProvider.Listener() {
			@Override
			public void cellChanged(final int row, final int column, final Object oldValue, final Object newValue) {
				setDirty(true);
			}
		});
		exprViewerRowFilterSupport.setExprSupport(getExprSupport("js"));
		//		tablesawViewer.addSelectionChangedListener(selectionChangeListener);
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

	@Override
	public void setFocus() {
	}

	// TableProvider

	@Override
	public Table getTable() {
		final Table table = Table.create(modelTable.name());
		final Collection<Column<?>> columns = new ArrayList<Column<?>>();
		final Collection<Integer> rowNums = getRows();
		for (int colNum = 0; colNum < modelTable.columnCount(); colNum++) {
			final Column<?> modelColumn = modelTable.column(colNum);
			final Column<?> column = modelColumn.emptyCopy();
			for (final int rowNum : rowNums) {
				final Object element = modelColumn.get(rowNum);
				((Column<Object>) column).append(element);
			}
			columns.add(column);
		}
		table.addColumns(columns.toArray(new Column<?>[columns.size()]));
		return table;
	}

	protected final Collection<Integer> getRows() {
		final Collection<Integer> rows = new ArrayList<>();
		for (int i = 0; i < modelTable.rowCount(); i++) {
			rows.add(i);
		}
		return rows;
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
	}

	@Override
	public ISelection getSelection() {
		return StructuredSelection.EMPTY;
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

	// IAdaptable

	@Override
	public <T> T getAdapter(final Class<T> adapter) {
		return super.getAdapter(adapter);
	}
}
