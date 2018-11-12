package tablesaw.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import tablesaw.ui.util.MultiCheckSelectionCombo;
import tech.tablesaw.api.Table;

public abstract class AbstractTablesawView extends ViewPart implements TableProvider.Listener {

	protected AbstractTablesawView(final boolean autoSelectTableDataProvider) {
		this.autoSelectTableDataProvider = autoSelectTableDataProvider;
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		setAutoSelectTableDataProvider(autoSelectTableDataProvider);
	}

	@Override
	public void dispose() {
		setAutoSelectTableDataProvider(false);
		super.dispose();
	}

	private boolean autoSelectTableDataProvider = true;

	public void setAutoSelectTableDataProvider(final boolean autoSelectTableDataProvider) {
		if (this.autoSelectTableDataProvider) {
			getSite().getPage().removePartListener(partListener);
		}
		this.autoSelectTableDataProvider = autoSelectTableDataProvider;
		if (this.autoSelectTableDataProvider) {
			getSite().getPage().addPartListener(partListener);
			setTableDataProvider(getSite().getWorkbenchWindow().getActivePage().getActiveEditor());
		}
	}

	private TableProvider tableProvider;
	private Table viewTable;

	private TableProvider getTableProvider() {
		return tableProvider;
	}
	public Table getViewTable() {
		if (viewTable == null && tableProvider != null) {
			viewTable = tableProvider.getTable();
		}
		return viewTable;
	}

	@Override
	public void tableDataChanged(final TableProvider tableProvider) {
		// clear cache
		this.viewTable = null;
		updateTableControls();
	}

	@Override
	public void tableChanged(final TableProvider tableProvider) {
		this.viewTable = null;
		updateConfigControls();
		updateTableControls();
	}

	protected void setTableDataProvider(final IWorkbenchPart part) {
		if (part instanceof TableProvider) {
			setTableProvider((TableProvider) part);
		}
	}

	protected void setTableProvider(final TableProvider tableProvider) {
		if (this.tableProvider == tableProvider) {
			return;
		}
		if (this.tableProvider != null) {
			this.tableProvider.removeTableDataProviderListener(this);
		}
		this.tableProvider = tableProvider;
		this.viewTable = null;
		updateView();
		if (this.tableProvider != null) {
			this.tableProvider.addTableDataProviderListener(this);
		}
	}

	@Override
	public void createPartControl(final Composite parent) {
		final GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		parent.setLayout(layout);

		final Composite configParent = new Composite(parent, SWT.NONE);
		//		selectorParent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout configLayout = new GridLayout(2, false);
		configParent.setLayout(configLayout);
		createConfigControls(configParent);

		createTableDataControls(parent);

		updateView();

		final IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
		menuManager.add(updateViewAction);
	}

	@Override
	public void setFocus() {
	}

	protected abstract void createTableDataControls(final Composite parent);

	protected void createWorkbenchDataProvideSelector(final String label, final Composite parent) {
		if (label != null) {
			final Label swtLabel = new Label(parent, SWT.NONE);
			swtLabel.setText(label);
		}
		final ComboViewer viewer = new ComboViewer(parent);
		final IStructuredContentProvider contentProvider = new IStructuredContentProvider() {
			@Override
			public Object[] getElements(final Object inputElement) {
				final Collection<TableProvider> tableDataProviders = new ArrayList<TableProvider>();
				if (inputElement instanceof IWorkbenchPage) {
					for (final IEditorReference editorReference : ((IWorkbenchPage) inputElement).getEditorReferences()) {
						final IEditorPart editorPart = editorReference.getEditor(false);
						if (editorPart instanceof TableProvider) {
							tableDataProviders.add((TableProvider) editorPart);
						}
					}
					for (final IViewReference viewReference : ((IWorkbenchPage) inputElement).getViewReferences()) {
						final IViewPart viewPart = viewReference.getView(false);
						if (viewPart instanceof TableProvider) {
							tableDataProviders.add((TableProvider) viewPart);
						}
					}
				}
				return tableDataProviders.toArray();
			}
		};
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				if (element instanceof IWorkbenchPart) {
					return ((IWorkbenchPart) element).getTitle();
				}
				return null;
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				tableProviderChanged(((TableProvider) viewer.getStructuredSelection().getFirstElement()));
			}
		});
		final IPartListener partListener = new IPartListener() {
			private void refreshViewer(final Viewer viewer) {
				if (! viewer.getControl().isDisposed()) {
					viewer.refresh();
				}
			}
			@Override
			public void partOpened(final IWorkbenchPart part) {
				refreshViewer(viewer);
			}
			@Override
			public void partDeactivated(final IWorkbenchPart part) {
			}
			@Override
			public void partClosed(final IWorkbenchPart part) {
				refreshViewer(viewer);
			}
			@Override
			public void partBroughtToTop(final IWorkbenchPart part) {
			}
			@Override
			public void partActivated(final IWorkbenchPart part) {
			}
		};
		final IWorkbenchPage page = getSite().getPage();
		page.addPartListener(partListener);
		viewer.getControl().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(final DisposeEvent e) {
				page.removePartListener(partListener);
			}
		});
		viewer.setInput(page);
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				final Object[] elements = contentProvider.getElements(page);
				if (elements.length > 0) {
					final TableProvider tableDataProvider = (TableProvider) elements[0];
					viewer.setSelection(new StructuredSelection(tableDataProvider));
				}
			}
		});
	}


	protected final Action updateViewAction = new Action("Refresh") {
		@Override
		public void run() {
			updateView();
		}
	};

	protected void tableProviderChanged(final TableProvider tableDataProvider) {
		setTableProvider(tableDataProvider);
	}

	private final IPartListener partListener = new IPartListener() {

		@Override
		public void partOpened(final IWorkbenchPart part) {
		}
		@Override
		public void partClosed(final IWorkbenchPart part) {
			if (part == tableProvider) {
				setTableProvider((TableProvider) null);
			}
		}

		@Override
		public void partBroughtToTop(final IWorkbenchPart part) {
			if (autoSelectTableDataProvider) {
				setTableDataProvider(part);
			}
		}
		@Override
		public void partActivated(final IWorkbenchPart part) {
		}
		@Override
		public void partDeactivated(final IWorkbenchPart part) {
			if (part == tableProvider) {
				setTableProvider((TableProvider) null);
			}
		}
	};

	//

	protected void updateView() {
		viewTable = (tableProvider != null ? tableProvider.getTable() : null);
		updateConfigControls();
		updateTableControls();
	}

	protected final String noColumn = "<none>";

	protected StructuredViewer createColumnSelector(final String label, final Composite parent) {
		return createColumnSelector(label, parent, null);
	}

	protected StructuredViewer createColumnSelector(final String label, final Composite parent, final Boolean mode) {
		final Label swtLabel = new Label(parent, SWT.NONE);
		swtLabel.setText(label);
		final boolean multi = Boolean.TRUE.equals(mode);
		final StructuredViewer selector = (multi ? new ListViewer(parent) : new ComboViewer(parent));
		selector.setContentProvider(new IStructuredContentProvider() {
			@Override
			public Object[] getElements(final Object inputElement) {
				if (inputElement instanceof Table) {
					final Table table = (Table) inputElement;
					return table.columnNames().toArray();
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
		selector.setInput(getViewTable());
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		if (multi) {
			gridData.heightHint = 60;
		}
		selector.getControl().setLayoutData(gridData);
		return selector;
	}

	protected void setColumnNames(final StructuredViewer columnSelector, final Table table) {
		columnSelector.setInput(table);
	}

	protected String getSelectedColumnName(final StructuredViewer columnSelector) {
		return (String) columnSelector.getStructuredSelection().getFirstElement();
	}

	final static String[] noStrings = new String[0];

	protected Control createColumnControl(final String label, final Composite parent, final Boolean mode) {
		final Label swtLabel = new Label(parent, SWT.NONE);
		swtLabel.setText(label);
		final boolean multi = Boolean.TRUE.equals(mode);
		final Table table = getViewTable();
		final String[] items = (table != null ? table.columnNames().toArray(noStrings) : noStrings);
		final SelectionListener selectionListener = new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateTableControls();
			}
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
		};
		Control control;
		if (multi) {
			final MultiCheckSelectionCombo combo = new MultiCheckSelectionCombo(parent, SWT.NONE);
			combo.setItems(items);
			combo.addSelectionListener(selectionListener);
			control = combo;
		} else {
			final Combo combo = new Combo(parent, SWT.NONE);
			combo.setItems(items);
			combo.addSelectionListener(selectionListener);
			control = combo;
		}
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		//		if (multi) {
		//			gridData.heightHint = 60;
		//		}
		control.setLayoutData(gridData);
		return control;
	}

	protected void setColumnNames(final Control columnCombo, final Table table) {
		final String[] columnNames = (table != null ? table.columnNames().toArray(noStrings) : noStrings);
		if (columnCombo instanceof MultiCheckSelectionCombo) {
			((MultiCheckSelectionCombo) columnCombo).setItems(columnNames);
		} else if (columnCombo instanceof Combo) {
			((Combo) columnCombo).setItems(columnNames);
		}
	}
	protected String[] getSelectedColumnNames(final Control columnCombo) {
		if (columnCombo instanceof MultiCheckSelectionCombo) {
			return ((MultiCheckSelectionCombo) columnCombo).getSelections();
		} else if (columnCombo instanceof Combo) {
			final Combo combo = (Combo) columnCombo;
			return new String[]{combo.getItem(combo.getSelectionIndex())};
		}
		return null;
	}

	protected void createConfigControls(final Composite configParent) {
		createWorkbenchDataProvideSelector("Source: ", configParent);
	}

	protected abstract void updateTableControls();

	protected void updateConfigControls() {
	}
}
