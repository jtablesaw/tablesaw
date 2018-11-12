package tablesaw.ui.nattable;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;

public class DefaultTablesawColumnLabelAccumulator extends ColumnOverrideLabelAccumulator {

	public DefaultTablesawColumnLabelAccumulator(final ILayer layer, final Table table) {
		super(layer);
		if (table != null) {
			for (int colNum = 0; colNum < table.columnCount(); colNum++) {
				final ColumnType colType = table.column(colNum).type();
				final String[] labels = getLabelsFor(colType);
				if (labels != null) {
					super.registerColumnOverrides(colNum, labels);
				}
				final String label = getLabelFor(table.column(colNum).type());
				if (label != null) {
					super.registerColumnOverrides(colNum, label);
				}
			}
		}
	}

	protected String[] getLabelsFor(final ColumnType type) {
		return null;
	}

	protected String getLabelFor(final ColumnType type) {
		return type.name();
	}
}
