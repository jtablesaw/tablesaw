package tablesaw.xtext.jvmmodel;

import java.util.Iterator;

import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.compiler.XbaseCompiler;
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable;
import org.eclipse.xtext.xbase.jvmmodel.JvmTypesBuilder;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

import com.google.inject.Inject;

import tablesaw.xtext.xaw.InlineTableRow;
import tablesaw.xtext.xaw.TableColumn;
import tablesaw.xtext.xaw.TableLiteral;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.StringColumn;

@SuppressWarnings("restriction")
public class XawCompiler extends XbaseCompiler {

	@Override
	protected void internalToConvertedExpression(final XExpression obj, final ITreeAppendable appendable) {
		if (obj instanceof TableLiteral) {
			_toJavaExpression((TableLiteral) obj, appendable);
		} else {
			super.internalToConvertedExpression(obj, appendable);
		}
	}

	private Iterable<TableColumn> getTableColumns(final TableLiteral literal) {
		return IterableExtensions.filter(literal.getExpressions(), TableColumn.class);
	}
	private Iterable<InlineTableRow> getTableRows(final TableLiteral literal) {
		return IterableExtensions.filter(literal.getExpressions(), InlineTableRow.class);
	}

	private final String defaultTableName = "A table";

	protected void _toJavaExpression(final TableLiteral literal, final ITreeAppendable b) {
		final String name = literal.getName();
		b.append("Table.create(" + "\"" + (name != null ? name : defaultTableName) + "\"");
		for (final TableColumn column : getTableColumns(literal)) {
			b.append(", ");
			if (b.hasName(column)) {
				b.append(getVarName(column, b));
			} else {
				internalToJavaExpression(column.getExpression(), b);
			}
		}
		b.append(")");
	}

	protected void _toJavaExpression(final TableColumn column, final ITreeAppendable b) {
		System.out.println("_toJavaExpression(TableColumn), didn't expect that");
	}
	protected void _toJavaExpression(final InlineTableRow row, final ITreeAppendable b) {
		System.out.println("_toJavaExpression(InlineTableRow), didn't expect that");
	}

	@Override
	protected void doInternalToJavaStatement(final XExpression expr, final ITreeAppendable appendable, final boolean isReferenced) {
		if (expr instanceof TableLiteral) {
			_toJavaStatement((TableLiteral) expr, appendable, isReferenced);
		} else if (expr instanceof TableColumn) {
			_toJavaStatement((TableColumn) expr, appendable, isReferenced);
		} else if (expr instanceof InlineTableRow) {
			_toJavaStatement((TableColumn) expr, appendable, isReferenced);
		} else {
			super.doInternalToJavaStatement(expr, appendable, isReferenced);
		}
	}

	protected void _toJavaStatement(final TableLiteral literal, final ITreeAppendable b, final boolean isReferenced) {
		int colNum = 0;
		final Iterable<TableColumn> tableColumns = getTableColumns(literal);
		for (final TableColumn column : tableColumns) {
			final String name = column.getName();
			final JvmTypeReference columnType = getTypeReferenceForColumn(column.getType());
			final XExpression colExpr = column.getExpression();
			if (colExpr != null) {
				if (columnType == null) {
					throw new RuntimeException("Unsupported type: " + column.getType().getQualifiedName());
				}
				internalToJavaStatement(colExpr, b, true);
			} else {
				final String varName = b.declareSyntheticVariable(column, makeJavaIdentifier(name != null ? name + "Column" : "column" + colNum));
				b.newLine();
				serialize(columnType, literal, b);
				b.append(" ").append(varName).append(" = ");
				serialize(columnType, literal, b);
				b.append(".create(\"" + name + "\")").append(";");
			}
			colNum++;
		}
		for (final InlineTableRow row : getTableRows(literal)) {
			final Iterator<TableColumn> columnIt = tableColumns.iterator();
			for (final XExpression column : row.getExpressions()) {
				internalToJavaStatement(column, b, true);
				final TableColumn tableColumn = columnIt.next();
				if (isReferenced) {
					final XExpression colExpr = tableColumn.getExpression();
					final String colVar = getVarName(colExpr != null ? colExpr : tableColumn, b);
					b.newLine().append(colVar).append(".append(");
					internalToJavaExpression(column, b);
					b.append(");");
				}
			}
		}
	}

	protected void _toJavaStatement(final TableColumn column, final ITreeAppendable b, final boolean isReferenced) {
		System.out.println("_toJavaStatement: " + column);
	}
	protected void _toJavaStatement(final InlineTableRow row, final ITreeAppendable b, final boolean isReferenced) {
		System.out.println("_toJavaStatement: " + row);
	}

	@Inject
	private JvmTypesBuilder typeRefBuilder;

	protected JvmTypeReference getTypeReferenceForColumn(final JvmTypeReference elementType) {
		if (isSame(String.class, elementType)) {
			return typeRefBuilder.newTypeRef(elementType, StringColumn.class);
		} else if (isSame(Boolean.TYPE, elementType) || isSame(Boolean.class, elementType)) {
			return typeRefBuilder.newTypeRef(elementType, BooleanColumn.class);
		} else if (isSame(Double.TYPE, elementType) || isSame(Double.class, elementType)) {
			return typeRefBuilder.newTypeRef(elementType, DoubleColumn.class);
		} else if (isSame(Short.TYPE, elementType) || isSame(Short.class, elementType)) {
			return typeRefBuilder.newTypeRef(elementType, ShortColumn.class);
		}
		return null;
	}

	protected boolean isSame(final Class<?> clazz, final JvmTypeReference type) {
		return clazz.getName().equals(type.getQualifiedName());
	}
}
