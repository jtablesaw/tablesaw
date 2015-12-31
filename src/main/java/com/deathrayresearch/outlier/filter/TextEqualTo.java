package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.ColumnReference;
import com.deathrayresearch.outlier.Relation;
import com.deathrayresearch.outlier.TextColumn;
import org.roaringbitmap.RoaringBitmap;

/**
 */
public class TextEqualTo extends ColumnFilter {

    String value;

    public TextEqualTo(ColumnReference reference, String value) {
        super(reference);
        this.value = value;
    }

    public RoaringBitmap apply(Relation relation) {
        TextColumn column = (TextColumn) relation.column(columnReference.getColumnName());
        return column.isEqualTo(value);
    }
}
