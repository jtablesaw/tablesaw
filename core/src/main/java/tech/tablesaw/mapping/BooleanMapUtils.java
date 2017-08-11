package tech.tablesaw.mapping;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.columns.Column;

/**
 * An interface for mapping operations unique to Boolean columns
 */
public interface BooleanMapUtils extends Column {

    /*
     * TODO(lwhite): Replace this implementation with a roaring bitmap version
     */
    default BooleanColumn and(BooleanColumn... columns) {
        BooleanColumn newColumn = BooleanColumn.create("");
        BooleanColumn thisColumn = (BooleanColumn) this;
        for (int i = 0; i < this.size(); i++) {
            boolean booleanValue = thisColumn.get(i);
            if (!booleanValue) {
                newColumn.append(false);
            } else {
                boolean result = true;
                for (BooleanColumn booleanColumn : columns) {
                    result = booleanColumn.get(i);
                    if (!result) {
                        newColumn.append(false);
                        break;
                    }
                }
                newColumn.append(result);
            }
        }
        return newColumn;
    }

    default BooleanColumn or(BooleanColumn... columns) {
        BooleanColumn newColumn = BooleanColumn.create("");
        BooleanColumn thisColumn = (BooleanColumn) this;

        for (int i = 0; i < this.size(); i++) {
            boolean booleanValue = thisColumn.get(i);
            if (booleanValue) {
                newColumn.append(true);
            } else {
                boolean result = false;
                for (BooleanColumn booleanColumn : columns) {
                    result = booleanColumn.get(i);
                    if (result) {
                        newColumn.append(true);
                        break;
                    }
                }
                newColumn.append(result);
            }
        }
        return newColumn;
    }
}
