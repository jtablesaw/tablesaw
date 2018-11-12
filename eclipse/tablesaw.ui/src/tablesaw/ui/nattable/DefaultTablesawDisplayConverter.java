package tablesaw.ui.nattable;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.ShortColumnType;

public class DefaultTablesawDisplayConverter extends AbstractTablesawDisplayConverter {

	public DefaultTablesawDisplayConverter(final Table table) {
		super(table);
	}

	// to display value

	@Override
	protected Object booleanDisplayValue(final Boolean value, final boolean missing) {
		return (missing ? missingBooleanDisplayValue() : Boolean.toString(value));
	}
	@Override
	protected String missingBooleanDisplayValue() {
		return missingDisplayValue();
	}

	@Override
	protected Object doubleDisplayValue(final double doubleValue, final boolean missing) {
		return (missing ? missingDoubleDisplayValue() : Double.toString(doubleValue));
	}
	@Override
	protected String missingDoubleDisplayValue() {
		return missingDisplayValue();
	}

	@Override
	protected Object shortDisplayValue(final short shortValue, final boolean missing) {
		return (missing ? missingShortDisplayValue() : Short.toString(shortValue));
	}
	@Override
	protected String missingShortDisplayValue() {
		return missingDisplayValue();
	}


	// to canonical value

	@Override
	protected Object booleanCanonicalValue(final Object value) {
		return (value != null ? Boolean.TRUE.equals(Boolean.valueOf(String.valueOf(value))) : null);
	}
	@Override
	protected Object doubleCanonicalValue(final Object value) {
		return (value != null ? Double.valueOf(String.valueOf(value)) : Double.valueOf(DoubleColumnType.missingValueIndicator()));
	}
	@Override
	protected Object shortCanonicalValue(final Object value) {
		return (value != null ? Short.valueOf(String.valueOf(value)) : Short.valueOf(ShortColumnType.missingValueIndicator()));
	}
}
