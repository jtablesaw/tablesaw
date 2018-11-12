package tablesaw.xtext.extensions;

import static org.eclipse.xtext.naming.QualifiedName.create;

import org.eclipse.xtext.naming.QualifiedName;

public class OperatorMapping extends org.eclipse.xtext.xbase.scoping.featurecalls.OperatorMapping {

	public static final QualifiedName ASSERT_EQUALS = create("?=");

	@Override
	protected void initializeMapping() {
		map.put(ASSERT_EQUALS, create(OP_PREFIX + "assertEquals"));
		super.initializeMapping();
	}
}
