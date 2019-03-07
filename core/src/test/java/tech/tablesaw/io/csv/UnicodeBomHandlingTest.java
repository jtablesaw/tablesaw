package tech.tablesaw.io.csv;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.tablesaw.io.csv.UnicodeBomHandlingTest.BOM.UTF_8;

public class UnicodeBomHandlingTest {

    private static final byte[] CONTENT = "1, 2, 3, 4, 5/t6, 7, 8, 9, 10".getBytes();
    private static final byte[] UTF8_BOM_CONTENT;

    static {
        UTF8_BOM_CONTENT = new byte[UTF_8.getBytes().length + CONTENT.length];

        System.arraycopy(UTF_8.getBytes(), 0, UTF8_BOM_CONTENT, 0, UTF_8.getBytes().length);
        System.arraycopy(CONTENT, 0, UTF8_BOM_CONTENT, UTF_8.getBytes().length, CONTENT.length);
    }


    @Test
    public void javaBehaviour() throws IOException {

        Table t = new CsvReader().read(CsvReadOptions.builder(
                new InputStreamReader(new ByteArrayInputStream(CONTENT)), "R").header(false).build());
        assertEquals(1, t.get(0, 0));
        t = new CsvReader().read(CsvReadOptions.builder(
                new InputStreamReader(new ByteArrayInputStream(UTF8_BOM_CONTENT)), "R").header(false).build());
        assertEquals(1, t.get(0, 0));
    }

    protected static final class BOM {
        /**
         * UTF-8 BOM (EF BB BF).
         */
        protected static final BOM UTF_8 = new BOM(new byte[]{(byte) 0xEF,
                (byte) 0xBB,
                (byte) 0xBF},
                "UTF-8");
        private final byte bytes[];
        private final String description;

        private BOM(final byte bom[], final String description) {
            assert (bom != null) : "invalid BOM: null is not allowed";
            assert (description != null) : "invalid description: null is not allowed";
            assert (description.length() != 0) : "invalid description: empty string is not allowed";

            this.bytes = bom;
            this.description = description;
        }

        /**
         * Returns a <code>String</code> representation of this <code>BOM</code>
         * value.
         */
        public final String toString() {
            return description;
        }

        /**
         * Returns the bytes corresponding to this <code>BOM</code> value.
         */
        private final byte[] getBytes() {
            final int length = bytes.length;
            final byte[] result = new byte[length];

            // make a defensive copy
            System.arraycopy(bytes, 0, result, 0, length);

            return result;
        }

    }

}
