package tablesaw.ui.nattable;

import org.eclipse.swt.widgets.Composite;

import tablesaw.ui.AbstractTablesawView;
import tech.tablesaw.api.Table;

public class NatTablesawView extends AbstractTablesawView {

	public NatTablesawView() {
		super(false);
	}

	@Override
	protected void createConfigControls(final Composite configParent) {
		createWorkbenchDataProvideSelector("Source: ", configParent);
	}

	private NatTablesawViewer natTablesawViewer;

	@Override
	protected void createTableDataControls(final Composite parent) {
		natTablesawViewer = new NatTablesawViewer();
		natTablesawViewer.createPartControl(parent);
	}

	protected Table getTableViewerInput() {
		return getViewTable();
	}

	@Override
	protected void updateTableControls() {
		natTablesawViewer.setInput(getTableViewerInput());
	}
}
