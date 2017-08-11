package tech.tablesaw.columns;

import org.junit.Test;

import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.store.ColumnMetadata;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ColumnMetadataTest {

    private final Column d = new FloatColumn("Float col1");

    @Test
    public void testToFromJson() {
        String meta = d.metadata();
        ColumnMetadata d2 = ColumnMetadata.fromJson(meta);
        assertEquals(d2, ColumnMetadata.fromJson(meta));
    }
}