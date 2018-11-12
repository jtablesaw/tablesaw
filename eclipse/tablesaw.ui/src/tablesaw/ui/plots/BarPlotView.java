package tablesaw.ui.plots;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Page;
import tech.tablesaw.plotly.traces.BarTrace;
import tech.tablesaw.plotly.traces.Trace;

public class BarPlotView extends AbstractPlotView {

	private StructuredViewer categorySelector;
	private Control numericsSelector;

	@Override
	public void createConfigControls(final Composite parent) {
		super.createConfigControls(parent);
		categorySelector = createColumnSelector("Category: ", parent);
		numericsSelector = createColumnControl("Numbers: ", parent, true);
	}

	@Override
	protected void updateConfigControls() {
		super.updateConfigControls();
		setColumnNames(categorySelector, getViewTable());
		setColumnNames(numericsSelector, getViewTable());
	}

	@Override
	protected String computeBrowserContents(final Point size) {
		final String category = getSelectedColumnName(categorySelector);
		final String[] numerics = getSelectedColumnNames(numericsSelector);
		if (category != null && numerics != null && numerics.length > 0) {
			final Table table = getViewTable();
			final Layout layout = getPlotLayout(size);
			final Trace[] traces = new Trace[numerics.length];
			for (int i = 0; i < numerics.length; i++) {
				final String name = String.valueOf(numerics[i]);
				final BarTrace trace = BarTrace.builder(
						table.categoricalColumn(category),
						table.numberColumn(name))
						.orientation(BarTrace.Orientation.VERTICAL)
						.showLegend(numerics.length > 1)
						.name(name)
						.build();
				traces[i] = trace;
			}
			final Figure figure = new Figure(layout, traces);
			final Page page = Page.pageBuilder(figure, "plot").build();
			final String html = page.asJavascript();
			return html;
		}
		return null;
	}
}
