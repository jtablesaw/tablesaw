package tablesaw.xtext.jvmmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.typesystem.computation.ITypeComputationState;
import org.eclipse.xtext.xbase.typesystem.computation.XbaseTypeComputer;
import org.eclipse.xtext.xbase.typesystem.references.LightweightTypeReference;

import tablesaw.xtext.xaw.InlineTableRow;
import tablesaw.xtext.xaw.TableColumn;
import tablesaw.xtext.xaw.TableLiteral;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class XawTypeComputer extends XbaseTypeComputer {

	@Override
	public void computeTypes(final XExpression expression, final ITypeComputationState state) {
		if (expression instanceof TableLiteral) {
			final TableLiteral literal = (TableLiteral) expression;
			final Collection<JvmTypeReference> colTypes = new ArrayList<JvmTypeReference>();
			for (final XExpression child : literal.getExpressions()) {
				if (child instanceof TableColumn) {
					final TableColumn column = (TableColumn) child;
					colTypes.add(column.getType());
					final XExpression colExp = column.getExpression();
					if (colExp != null) {
						// TODO should expect a parameterized Column type
						final ITypeComputationState expressionState = state.withExpectation(getRawTypeForName(Column.class, state));
						expressionState.computeTypes(colExp);
					}
				} else if (child instanceof InlineTableRow) {
					final Iterator<JvmTypeReference> colTypesIt = colTypes.iterator();
					for (final XExpression column : ((InlineTableRow) child).getExpressions()) {
						final JvmTypeReference typeRef = colTypesIt.next();
						final ITypeComputationState expressionState = state.withExpectation(getTypeForName(typeRef.getQualifiedName(), state));
						expressionState.computeTypes(column);
					}
				}
			}
			final LightweightTypeReference result = getTypeForName(Table.class, state);
			state.acceptActualType(result);
		} else if (expression instanceof TableColumn) {
			System.out.println("Type for " + expression);
			super.computeTypes(expression, state);
		} else if (expression instanceof InlineTableRow) {
			System.out.println("Type for " + expression);
			super.computeTypes(expression, state);
		} else {
			super.computeTypes(expression, state);
		}
	}
}
