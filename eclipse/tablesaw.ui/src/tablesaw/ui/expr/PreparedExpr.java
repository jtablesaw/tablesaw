package tablesaw.ui.expr;

import java.util.Collection;

import tech.tablesaw.api.ColumnType;

public interface PreparedExpr {
	public String getExpr();
	public ColumnType getColumnType();
	public Collection<String> getDiagnostics();
}
