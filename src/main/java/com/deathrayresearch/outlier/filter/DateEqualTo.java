package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.ColumnReference;
import com.deathrayresearch.outlier.LocalDateColumn;
import com.deathrayresearch.outlier.Relation;
import org.roaringbitmap.RoaringBitmap;

import java.time.LocalDate;

/**
 */
public class DateEqualTo extends ColumnFilter {

    LocalDate value;

    public DateEqualTo(ColumnReference reference, LocalDate value) {
        super(reference);
        this.value = value;
    }

    public RoaringBitmap apply(Relation relation) {
        LocalDateColumn dateColumn = (LocalDateColumn) relation.column(columnReference.getColumnName());
        return dateColumn.isEqualTo(value);
    }
}
