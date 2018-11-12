package tablesaw.ui.aql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.acceleo.query.parser.AstValidator;
import org.eclipse.acceleo.query.runtime.EvaluationResult;
import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult;
import org.eclipse.acceleo.query.runtime.IQueryEnvironment;
import org.eclipse.acceleo.query.runtime.IValidationMessage;
import org.eclipse.acceleo.query.runtime.IValidationResult;
import org.eclipse.acceleo.query.runtime.Query;
import org.eclipse.acceleo.query.runtime.impl.QueryBuilderEngine;
import org.eclipse.acceleo.query.runtime.impl.QueryEvaluationEngine;
import org.eclipse.acceleo.query.validation.type.EClassifierType;
import org.eclipse.acceleo.query.validation.type.IType;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

import tablesaw.ui.expr.PreparedExpr;
import tech.tablesaw.api.ColumnType;

public class PreparedAqlExpr implements PreparedExpr {

	private final EClass eClass;
	private final Collection<String> diagnostics = new ArrayList<String>();
	private final IQueryEnvironment queryEnvironment;
	private final AstResult astResult;

	private final String rowVar = "self";

	public PreparedAqlExpr(final String expr, final Map<String, ColumnType> varTypes, final String thisVar) {
		eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName("Expr");
		for (final Map.Entry<String, ColumnType> variables : varTypes.entrySet()) {
			final EAttribute attr = createAttribute(variables.getKey(), variables.getValue());
			if (attr == null) {
				diagnostics.add("Couldn't create attribute for " + variables.getKey());
			} else if (attr.getEType() == null) {
				diagnostics.add("Unsupported type: " + variables.getValue());
			} else {
				eClass.getEStructuralFeatures().add(attr);
			}
		}
		final EPackage pack = EcoreFactory.eINSTANCE.createEPackage();
		pack.setName("aql");
		pack.getEClassifiers().add(eClass);

		queryEnvironment = Query.newEnvironmentWithDefaultServices(null);
		queryEnvironment.registerEPackage(eClass.getEPackage());
		final QueryBuilderEngine builder = new QueryBuilderEngine(queryEnvironment);
		astResult = builder.build(expr);

		final Map<String, Set<IType>> variableTypes = new LinkedHashMap<String, Set<IType>>();
		for (final Map.Entry<String, ColumnType> variable : varTypes.entrySet()) {
			final String varName = variable.getKey();
			setType(variableTypes, varName, eClass.getEStructuralFeature(varName).getEType());
		}
		setType(variableTypes, rowVar, eClass);

		final AstValidator validator = new AstValidator(queryEnvironment);
		final IValidationResult validationResult = validator.validate(variableTypes, astResult);
		for (final IValidationMessage message : validationResult.getMessages()) {
			diagnostics.add(message.getMessage());
		}
	}

	protected void setType(final Map<String, Set<IType>> varTypes, final String varName, final EClassifier eClassifier) {
		final Set<IType> types = new LinkedHashSet<IType>();
		types.add(new EClassifierType(queryEnvironment, eClassifier));
		varTypes.put(varName, types);
	}

	protected EAttribute createAttribute(final String name, final ColumnType columnType) {
		final EAttribute eAttr = EcoreFactory.eINSTANCE.createEAttribute();
		eAttr.setName(name);
		final EDataType dataType = getDataType(columnType);
		if (dataType != null) {
			eAttr.setEType(dataType);
		}
		return eAttr;
	}

	protected EDataType getDataType(final ColumnType columnType) {
		EDataType dataType = null;
		if (columnType == ColumnType.DOUBLE) {
			dataType = EcorePackage.eINSTANCE.getEDouble();
		} else if (columnType == ColumnType.STRING) {
			dataType = EcorePackage.eINSTANCE.getEString();
		}
		return dataType;
	}

	@Override
	public String getExpr() {
		return astResult.toString();
	}

	@Override
	public ColumnType getColumnType() {
		return null;
	}

	@Override
	public Collection<String> getDiagnostics() {
		return diagnostics;
	}

	public Object eval(final Map<String, Object> varValues) {
		final QueryEvaluationEngine engine = new QueryEvaluationEngine(queryEnvironment);
		final Map<String, Object> variables = new HashMap<String, Object>();
		final EObject eObject = EcoreUtil.create(eClass);
		for (final Map.Entry<String, Object> variable : varValues.entrySet()) {
			final String varName = variable.getKey();
			final EStructuralFeature feature = eClass.getEStructuralFeature(varName);
			final Object value = variable.getValue();
			variables.put(varName, value);
			eObject.eSet(feature, value);
		}
		variables.put(rowVar, eObject);

		final EvaluationResult evaluationResult = engine.eval(astResult, variables);
		addDiagnostics(evaluationResult.getDiagnostic());
		return evaluationResult.getResult();
	}

	private void addDiagnostics(final Diagnostic diagnostic) {
		if (diagnostic != null && diagnostic.getMessage() != null) {
			diagnostics.add(diagnostic.getMessage());
			for (final Diagnostic child : diagnostic.getChildren()) {
				addDiagnostics(child);
			}
		}
	}
}
