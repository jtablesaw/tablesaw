package tech.tablesaw.io.fixed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;

import com.univocity.parsers.fixed.FixedWidthFormat;

public class FixedWidthWriteOptionsTest {

    @Test
    public void testSettingsPropagation() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        FixedWidthWriteOptions options = new FixedWidthWriteOptions.Builder(stream)
                .header(true)
                .lineSeparatorString("\r\n")
                .padding('~')
                .build();

        assertTrue(options.header());

        FixedWidthWriter writer = new FixedWidthWriter();
        FixedWidthFormat format = writer.fixedWidthFormat(options);
        assertEquals("\r\n", format.getLineSeparatorString());
        assertEquals('~', format.getPadding());
    }

}
