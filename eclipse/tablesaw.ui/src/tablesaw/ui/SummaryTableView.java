package tablesaw.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.aggregate.NumericAggregateFunction;
import tech.tablesaw.aggregate.Summarizer;
import tech.tablesaw.api.Table;

public class SummaryTableView extends SimpleTablesawView implements TableProvider {

	private Control numericsSelector;
	private StructuredViewer categorySelector;
	private StructuredViewer aggregateFunctionSelector;

	@Override
	protected void createConfigControls(final Composite configParent) {
		super.createConfigControls(configParent);
		numericsSelector = createColumnControl("Numbers: ", configParent, true);
		categorySelector = createColumnSelector("Category: ", configParent, false);
		aggregateFunctionSelector = createAggregateFunctionSelector("Aggregate with: ", configParent, true);
	}

	@Override
	protected void updateConfigControls() {
		super.updateConfigControls();
		setColumnNames(categorySelector, getViewTable());
		setColumnNames(numericsSelector, getViewTable());
	}

	private final Collection<AggregateFunction<?, ?>> aggregateFunctions = new ArrayList<AggregateFunction<?,?>>();
	{
		for (final Field field : AggregateFunctions.class.getFields()) {
			final int modifiers = field.getModifiers();
			if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && NumericAggregateFunction.class.isAssignableFrom(field.getType())) {
				try {
					aggregateFunctions.add((AggregateFunction<?, ?>) field.get(null));
				} catch (final IllegalArgumentException e) {
				} catch (final IllegalAccessException e) {
				}
			}
		}
	}

	protected StructuredViewer createAggregateFunctionSelector(final String label, final Composite parent, final boolean multi) {
		final Label swtLabel = new Label(parent, SWT.NONE);
		swtLabel.setText(label);
		final StructuredViewer selector = (multi ? new ListViewer(parent) : new ComboViewer(parent));
		selector.setContentProvider(new IStructuredContentProvider() {
			@Override
			public Object[] getElements(final Object inputElement) {
				if (inputElement instanceof Collection<?>) {
					return ((Collection<?>) inputElement).toArray();
				}
				return null;
			}
		});
		selector.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				updateTableControls();
			}
		});
		selector.setInput(aggregateFunctions);
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		if (multi) {
			gridData.heightHint = 60;
		}
		selector.getControl().setLayoutData(gridData);
		return selector;
	}

	private Table summary = null;

	@Override
	protected Table getTableViewerInput() {
		return summary;
	}

	@Override
	protected void updateTableControls() {
		if (getViewTable() != null) {
			final String[] numerics = getSelectedColumnNames(numericsSelector);
			final String category = getSelectedColumnName(categorySelector);
			final IStructuredSelection selection = aggregateFunctionSelector.getStructuredSelection();
			if (category != null && numerics != null && numerics.length > 0 && (! selection.isEmpty())) {
				final AggregateFunction<?, ?>[] funs = new AggregateFunction<?, ?>[selection.size()];
				final Iterator<Object> it = selection.iterator();
				int pos = 0;
				while (it.hasNext()) {
					funs[pos++] = (AggregateFunction<?, ?>) it.next();
				}
				final Summarizer summarizer = getViewTable().summarize(Arrays.asList(numerics), funs);
				summary = (category == noColumn ? summarizer.apply() : summarizer.by(category));
				aggregateFunctionSelector.getControl().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						tableProviderHelper.fireTableChanged(SummaryTableView.this);
					}
				});
			}
		}
		super.updateTableControls();
	}

	//

	@Override
	public Table getTable() {
		return summary;
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
}
