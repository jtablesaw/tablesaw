package tablesaw.ui.javaxscript;

import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import tablesaw.ui.expr.ExprSupport;
import tablesaw.ui.expr.PreparedExpr;
import tech.tablesaw.api.ColumnType;

public class ScriptEngineExprSupport extends ExprSupport {

	private final ScriptEngineManager manager = new ScriptEngineManager();
	private final ScriptEngine engine;

	public ScriptEngineExprSupport(final String engineName) {
		engine = manager.getEngineByName(engineName);
	}
	public ScriptEngineExprSupport() {
		this("nashorn");
	}

	@Override
	public PreparedExpr prepareExpr(final String expr, final Map<String, ColumnType> varTypes, final String thisVar) {
		return new PreparedScriptEngineExpr(expr, varTypes, thisVar, engine);
	}

	@Override
	public Object evalExpr(final PreparedExpr expr, final Map<String, Object> varValues) {
		final PreparedScriptEngineExpr javaxScriptExpr = (PreparedScriptEngineExpr) expr;
		return javaxScriptExpr.evalExpr(varValues);
	}
}
