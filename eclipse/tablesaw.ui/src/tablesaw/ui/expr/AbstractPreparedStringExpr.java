package tablesaw.ui.expr;

import java.util.ArrayList;
import java.util.Collection;

import tech.tablesaw.api.ColumnType;

public abstract class AbstractPreparedStringExpr implements PreparedExpr {

	private String expr;
	private Collection<String> diagnostics = new ArrayList<String>();

	public AbstractPreparedStringExpr(String expr) {
		this.expr = expr;
	}

	@Override
	public String getExpr() {
		return expr;
	}

	@Override
	public ColumnType getColumnType() {
		return null;
	}

	@Override
	public Collection<String> getDiagnostics() {
		return new ArrayList<String>(diagnostics);
	}
	
	public void clearDiagnostics() {
		diagnostics.clear();
	}

	public void addDiagnostics(String diagnostic) {
		if (diagnostic != null) {
			diagnostics.add(diagnostic);
		}
	}
}
