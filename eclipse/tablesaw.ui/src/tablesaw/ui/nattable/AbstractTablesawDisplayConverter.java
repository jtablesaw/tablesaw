package tablesaw.ui.nattable;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.booleans.BooleanColumnType;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.ShortColumnType;

public abstract class AbstractTablesawDisplayConverter extends DefaultDisplayConverter {

	private Table table;

	public AbstractTablesawDisplayConverter(final Table table) {
		this.table = table;
	}

	public void setTable(final Table table) {
		this.table = table;
	}

	protected ColumnType getColumnType(final ILayerCell cell) {
		final Column<?> column = getColumn(cell);
		return (column != null ? column.type() : null);
	}

	protected Column<?> getColumn(final ILayerCell cell) {
		return (table != null ? table.column(cell.getColumnIndex()) : null);
	}

	@Override
	public Object canonicalToDisplayValue(final ILayerCell cell, final IConfigRegistry configRegistry, final Object canonicalValue) {
		final Object displayValue = canonicalToDisplayValue(canonicalValue, getColumnType(cell));
		return (displayValue != null ? displayValue : super.canonicalToDisplayValue(cell, configRegistry, canonicalValue));
	}

	@Override
	public Object displayToCanonicalValue(final ILayerCell cell, final IConfigRegistry configRegistry, final Object displayValue) {
		final Object canonicalValue = displayToCanonicalValue(displayValue, getColumnType(cell));
		return (canonicalValue != null ? canonicalValue : super.displayToCanonicalValue(cell, configRegistry, displayValue));
	}

	// to display value

	protected String missingDisplayValue() {
		return "";
	}

	protected Object canonicalToDisplayValue(final Object value, final ColumnType type) {
		if (type instanceof BooleanColumnType && value instanceof Boolean) {
			return booleanDisplayValue((Boolean) value, value == null);
		} else if (type instanceof DoubleColumnType && value instanceof Number) {
			final double doubleValue = ((Number) value).doubleValue();
			return doubleDisplayValue(doubleValue, DoubleColumnType.isMissingValue(doubleValue));
		} else if (type instanceof ShortColumnType && value instanceof Number) {
			final short shortValue = ((Number) value).shortValue();
			return shortDisplayValue(shortValue, ShortColumnType.isMissingValue(shortValue));
		} else if (value == null) {
			return missingDisplayValue();
		}
		return null;
	}
	protected abstract Object booleanDisplayValue(final Boolean value, final boolean missing);
	protected abstract String missingBooleanDisplayValue();

	protected abstract Object doubleDisplayValue(final double doubleValue, final boolean missing);
	protected abstract String missingDoubleDisplayValue();

	protected abstract Object shortDisplayValue(final short shortValue, final boolean missing);
	protected abstract String missingShortDisplayValue();

	// to canonical value

	protected Object displayToCanonicalValue(final Object value, final ColumnType type) {
		if (type instanceof BooleanColumnType) {
			return booleanCanonicalValue(value);
		} else if (type instanceof DoubleColumnType) {
			return doubleCanonicalValue(value);
		} else if (type instanceof ShortColumnType) {
			return shortCanonicalValue(value);
		}
		return null;
	}

	protected abstract Object booleanCanonicalValue(final Object value);
	protected abstract Object doubleCanonicalValue(final Object value);
	protected abstract Object shortCanonicalValue(final Object value);
}
