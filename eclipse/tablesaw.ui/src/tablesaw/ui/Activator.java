package tablesaw.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import tablesaw.ui.expr.ExprSupport;

public class Activator implements BundleActivator {

	private static Activator instance = null;

	public static Activator getInstance() {
		return instance;
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		instance = this;
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		instance = null;
	}

	private Collection<ExprSupport> exprSupports = null;

	public ExprSupport[] getExprSupports() {
		if (exprSupports == null) {
			exprSupports = new ArrayList<ExprSupport>();
			processExprSupportExtensions();
		}
		return exprSupports.toArray(new ExprSupport[exprSupports.size()]);
	}

	private void processExprSupportExtensions() {
		final IExtensionPoint ep = Platform.getExtensionRegistry().getExtensionPoint("tablesaw.ui.exprSupport");
		for (final IExtension extension : ep.getExtensions()) {
			for (final IConfigurationElement ces : extension.getConfigurationElements()) {
				if ("exprSupport".equals(ces.getName())) {
					try {
						final ExprSupport es = (ExprSupport) ces.createExecutableExtension("supportClass");
						es.setLang(ces.getAttribute("langName"));
						exprSupports.add(es);
					} catch (final CoreException e) {
					}
				}
			}
		}
	}
}
