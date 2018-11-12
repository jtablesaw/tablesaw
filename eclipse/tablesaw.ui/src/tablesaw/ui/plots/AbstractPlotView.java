package tablesaw.ui.plots;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import tablesaw.ui.AbstractTablesawView;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Margin;

public abstract class AbstractPlotView extends AbstractTablesawView {

	public AbstractPlotView() {
		super(false);
	}

	private Browser browser;


	@Override
	public void createTableDataControls(final Composite parent) {
		browser = new Browser(parent, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		browser.setJavascriptEnabled(true);
	}

	protected abstract String computeBrowserContents(Point size);

	protected Layout getPlotLayout(final Point size) {
		final Layout layout = Layout.builder()
				.height(size.y)
				.width(size.x)
				.margin(getPlotMargin())
				.build();
		return layout;
	}

	protected Margin getPlotMargin() {
		return Margin.builder().left(40).right(20).top(10).bottom(25).build();
	}

	@Override
	protected void updateTableControls() {
		if (browser != null) {
			String plotHtml = null;
			if (getViewTable() != null) {
				plotHtml = computeBrowserContents(browser.getSize());
			}
			if (plotHtml == null) {
				plotHtml = "<h2>no plot data</h2>";
			}
			browser.setText(plotHtml);
		}
	}

	@Override
	public void setFocus() {
	}
}
