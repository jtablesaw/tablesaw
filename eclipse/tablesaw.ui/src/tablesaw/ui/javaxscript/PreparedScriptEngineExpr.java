package tablesaw.ui.javaxscript;

import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import tablesaw.ui.expr.AbstractPreparedStringExpr;
import tech.tablesaw.api.ColumnType;

public class PreparedScriptEngineExpr extends AbstractPreparedStringExpr {

	private final ScriptEngine engine;
	private CompiledScript compiledScript = null;
	private final ScriptContext scriptContext;
	private final Bindings bindings;

	public PreparedScriptEngineExpr(String expr, final Map<String, ColumnType> varTypes, final String thisVar, final ScriptEngine engine) {
		super(expr);
		this.engine = engine;
		if (thisVar != null && expr.contains("$")) {
			expr = expr.replace("$", thisVar);
		}
		if (engine instanceof Compilable) {
			try {
				compiledScript = ((Compilable) engine).compile(expr);
			} catch (final ScriptException e) {
				addDiagnostics(e.getMessage());
			}
		}
		scriptContext = new SimpleScriptContext();
		scriptContext.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
		bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
		for (final String variable : varTypes.keySet()) {
			bindings.put(variable, null);
		}
	}

	public Object evalExpr(final Map<String, Object> varValues) {
		for (final Map.Entry<String, Object> variable : varValues.entrySet()) {
			bindings.put(variable.getKey(), variable.getValue());
		}
		try {
			final Object result = (compiledScript != null ? compiledScript.eval(scriptContext) : engine.eval(getExpr(), scriptContext));
			return result;
		} catch (final ScriptException e) {
			addDiagnostics(e.getMessage());
		}
		return null;
	}
}
