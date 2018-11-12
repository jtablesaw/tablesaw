package tablesaw.ui.nattable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowHeaderComposite;
import org.eclipse.nebula.widgets.nattable.filterrow.IFilterStrategy;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import tablesaw.ui.TableProvider;
import tablesaw.ui.TableProviderHelper;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class NatTablesawViewer implements TableProvider, ISelectionProvider {

	private Table input;

	public void setInput(final Table input) {
		this.input = input;
		if (natTable != null) {
			bodyDataProvider.setTable(input);
			columnHeaderDataProvider.setTable(input);
			rowHeaderDataProvider.setTable(input);
			final DefaultTablesawColumnLabelAccumulator columnLabelAccumulator = new DefaultTablesawColumnLabelAccumulator(bodyDataLayer, input);
			bodyDataLayer.setConfigLabelAccumulator(columnLabelAccumulator);
			displayConverter.setTable(input);
		}
		refresh();
	}

	private NatTable natTable;
	private TablesawDataProvider bodyDataProvider, columnHeaderDataProvider, rowHeaderDataProvider;
	private DataLayer bodyDataLayer, columnDataLayer;
	private SelectionLayer selectionLayer;
	private AbstractTablesawDisplayConverter displayConverter;

	private final int defaultColumnWidth = 60, defaultRowHeight = 20;

	private boolean editable = false;

	public void setEditable(final boolean editable) {
		this.editable = editable;
	}

	private boolean includeFilterRow = true;

	public void setIncludeFilterRow(final boolean includeFilterRow) {
		this.includeFilterRow = includeFilterRow;
	}

	public void createPartControl(final Composite parent) {
		bodyDataProvider = new TablesawDataProvider(input);
		bodyDataLayer = new DataLayer(bodyDataProvider, defaultColumnWidth, defaultRowHeight);
		rowHeaderDataProvider = new TablesawDataProvider(input, false);
		final ColumnReorderLayer columnReorderLayer = new ColumnReorderLayer(bodyDataLayer);
		final ColumnHideShowLayer columnHideShowLayer = new ColumnHideShowLayer(columnReorderLayer);
		selectionLayer = new SelectionLayer(columnHideShowLayer);
		final ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

		columnHeaderDataProvider = new TablesawDataProvider(input, true);
		columnDataLayer = new DataLayer(columnHeaderDataProvider, defaultColumnWidth, defaultRowHeight);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(columnDataLayer, viewportLayer, selectionLayer);

		if (includeFilterRow) {
			final FilterRowHeaderComposite<Object> filterRowHeaderLayer =
					new FilterRowHeaderComposite<Object>(
							new IFilterStrategy<Object>() {
								@Override
								public void applyFilter(final Map<Integer, Object> filterIndexToObjectMap) {
								}
							},
							columnHeaderLayer,
							columnHeaderDataProvider,
							null);
			columnHeaderLayer = filterRowHeaderLayer;
		}

		final ILayer rowHeaderLayer = new RowHeaderLayer(new DataLayer(rowHeaderDataProvider, defaultRowHeight, defaultRowHeight), viewportLayer, selectionLayer);

		final IDataProvider cornerDataProvider = new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvider);
		final DataLayer cornerDataLayer = new DataLayer(cornerDataProvider);
		final CornerLayer cornerLayer = new CornerLayer(cornerDataLayer, rowHeaderLayer, columnHeaderLayer);
		final ILayer gridLayer = new GridLayer(viewportLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);
		natTable = new NatTable(parent, gridLayer, true);
		configure(natTable);
		natTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	protected void configure(final NatTable natTable) {
		final IConfigRegistry configRegistry = natTable.getConfigRegistry();
		displayConverter = new DefaultTablesawDisplayConverter(input);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, displayConverter);
		displayConverter.setTable(input);
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, new IEditableRule() {
			@Override
			public boolean isEditable(final int columnIndex, final int rowIndex) {
				return editable;
			}
			@Override
			public boolean isEditable(final ILayerCell cell, final IConfigRegistry configRegistry) {
				return editable;
			}
		});
	}

	public void refresh() {
		natTable.refresh();
	}

	//

	public void addTableChangeListener(final TablesawDataProvider.Listener listener) {
		bodyDataProvider.addTableChangeListener(listener);
	}

	public void removeTableChangeListener(final TablesawDataProvider.Listener listener) {
		bodyDataProvider.removeTableChangeListener(listener);
	}

	// TableProvider

	@Override
	public Table getTable() {
		final Table table = Table.create(input.name());
		final Collection<Column<?>> columns = new ArrayList<Column<?>>();
		final Collection<Integer> rowNums = getRows();
		for (int colNum = 0; colNum < input.columnCount(); colNum++) {
			final Column<?> modelColumn = input.column(colNum);
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
		for (int i = 0; i < input.rowCount(); i++) {
			rows.add(i);
		}
		return rows;
	}


	private final TableProviderHelper tableProviderHelper = new TableProviderHelper();

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
		return new StructuredSelection(selectionLayer.getSelectedCells());
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
