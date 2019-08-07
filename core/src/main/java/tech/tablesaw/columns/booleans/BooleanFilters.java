package tech.tablesaw.columns.booleans;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.selection.Selection;

public interface BooleanFilters {

    public Selection isFalse();

    public Selection isTrue();

    Selection isEqualTo(BooleanColumn other);

    Selection isMissing();

    Selection isNotMissing();
}
