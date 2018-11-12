package tablesaw.ui.plots;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Page;
import tech.tablesaw.plotly.traces.PieTrace;

public class PiePlotView extends AbstractPlotView {

	StructuredViewer categorySelector, numericsSelector;

	@Override
	public void createConfigControls(final Composite parent) {
		super.createConfigControls(parent);
		categorySelector = createColumnSelector("Category: ", parent);
		numericsSelector = createColumnSelector("Numbers: ", parent);
	}

	@Override
	protected void updateConfigControls() {
		super.updateConfigControls();
		categorySelector.setInput(getViewTable());
		numericsSelector.setInput(getViewTable());
	}

	@Override
	protected String computeBrowserContents(final Point size) {
		final String category = getSelectedColumnName(categorySelector);
		final String numeric = getSelectedColumnName(numericsSelector);
		if (category != null && numeric != null) {
			final Layout layout = getPlotLayout(size);
			final Table table = getViewTable();
			final PieTrace trace = PieTrace.builder(table.categoricalColumn(category), table.numberColumn(numeric)).build();
			final Figure figure = new Figure(layout, trace);
			final Page page = Page.pageBuilder(figure, "plot").build();
			return page.asJavascript();
		}
		return null;
	}
}
