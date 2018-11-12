package tablesaw.ui.expr;

import java.util.Map;

import tech.tablesaw.api.ColumnType;

public abstract class ExprSupport {

	private String lang;

	public String getLang() {
		return lang;
	}

	public void setLang(final String lang) {
		this.lang = lang;
	}

	public abstract PreparedExpr prepareExpr(String expr, Map<String, ColumnType> varTypes, String thisToken);
	public abstract Object evalExpr(PreparedExpr expr, Map<String, Object> varValues);
}
