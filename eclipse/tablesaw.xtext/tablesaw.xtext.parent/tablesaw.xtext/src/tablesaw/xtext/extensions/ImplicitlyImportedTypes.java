package tablesaw.xtext.extensions;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;

public class ImplicitlyImportedTypes extends org.eclipse.xtext.xbase.scoping.batch.ImplicitlyImportedFeatures {

	@Override
	protected List<Class<?>> getExtensionClasses() {
		final List<Class<?>> extensionClasses = super.getExtensionClasses();
		extensionClasses.addAll(Lists.<Class<?>> newArrayList(
				TablesawExtensions.class
				));
		Collections.sort(extensionClasses, new Comparator<Class<?>>() {
			@Override
			public int compare(final Class<?> c1, final Class<?> c2) {
				return c1.getSimpleName().compareTo(c2.getSimpleName());
			}
		});
		return extensionClasses;
	}
}
