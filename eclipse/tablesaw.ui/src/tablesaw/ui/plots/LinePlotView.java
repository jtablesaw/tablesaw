package tablesaw.ui.plots;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import tech.tablesaw.plotly.api.LinePlot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Page;

public class LinePlotView extends AbstractPlotView {

	StructuredViewer categorySelector, xSelector, ySelector;

	@Override
	public void createConfigControls(final Composite parent) {
		super.createConfigControls(parent);
		categorySelector = createColumnSelector("Category: ", parent, false);
		xSelector = createColumnSelector("x: ", parent);
		ySelector = createColumnSelector("y: ", parent);
	}

	@Override
	protected void updateConfigControls() {
		super.updateConfigControls();
		categorySelector.setInput(getViewTable());
		xSelector.setInput(getViewTable());
		ySelector.setInput(getViewTable());
	}

	@Override
	protected String computeBrowserContents(final Point size) {
		final String x = getSelectedColumnName(xSelector), y = getSelectedColumnName(ySelector);
		final String category = getSelectedColumnName(categorySelector);
		if (x != null && x != null && category != null) {
			final Figure figure = (category == noColumn
					? LinePlot.create(getViewTable().name(), getViewTable(), x, y)
							: LinePlot.create(getViewTable().name(), getViewTable(), x, y, category));
			final Page page = Page.pageBuilder(figure, "plot").build();
			return page.asJavascript();
		}
		return null;
	}
}
