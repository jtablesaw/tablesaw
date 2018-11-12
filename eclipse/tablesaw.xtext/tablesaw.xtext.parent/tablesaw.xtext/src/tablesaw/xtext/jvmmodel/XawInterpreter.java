package tablesaw.xtext.jvmmodel;

import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.interpreter.IEvaluationContext;
import org.eclipse.xtext.xbase.interpreter.IEvaluationResult;
import org.eclipse.xtext.xbase.interpreter.impl.DefaultEvaluationResult;
import org.eclipse.xtext.xbase.interpreter.impl.XbaseInterpreter;

import tablesaw.xtext.xaw.TableLiteral;
import tech.tablesaw.api.Table;

public class XawInterpreter extends XbaseInterpreter {

	@Override
	public IEvaluationResult evaluate(final XExpression expr, final IEvaluationContext context, final CancelIndicator indicator) {
		if (expr instanceof TableLiteral) {
			final TableLiteral tableLiteral = (TableLiteral) expr;
			return new DefaultEvaluationResult(Table.create(tableLiteral.getName()), null);
		} else {
			return super.evaluate(expr, context, indicator);
		}
	}
}
