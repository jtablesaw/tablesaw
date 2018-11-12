package tablesaw.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;

import tech.tablesaw.api.Table;

public abstract class AbstractTablesawContentProvider implements IStructuredContentProvider {

	protected abstract boolean isContentTable(Table table);

	@Override
	public Object[] getElements(final Object inputElement) {
		int elementCount = 0;
		final Collection<Table> tables = new ArrayList<Table>();
		if (inputElement instanceof Table) {
			final Table table = (Table) inputElement;
			tables.add(table);
			elementCount += table.rowCount();
		} else if (inputElement instanceof Object[]) {
			for (final Object o : (Object[]) inputElement) {
				if (o instanceof Table) {
					final Table table = (Table) o;
					tables.add(table);
					elementCount += table.rowCount();
				}
			}
		} else if (inputElement instanceof Iterable<?>) {
			for (final Object o : (Iterable<?>) inputElement) {
				if (o instanceof Table) {
					final Table table = (Table) o;
					tables.add(table);
					elementCount += table.rowCount();
				}
			}
		}
		int elementNum = 0, rowNum = 0;
		final Object[] rows = new Object[elementCount];
		for (final Table table : tables) {
			final boolean isContent = isContentTable(table);
			for (int i = 0; i < table.rowCount(); i++) {
				rows[elementNum] = new TablesawContentRow(isContent ? rowNum : -1, table, i);
				elementNum++;
				if (isContent) {
					rowNum++;
				}
			}
		}
		return rows;
	}
}
