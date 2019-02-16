/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.io.fixed;

import com.univocity.parsers.fixed.FixedWidthFormat;
import com.univocity.parsers.fixed.FixedWidthWriterSettings;
import tech.tablesaw.api.Table;

import javax.annotation.concurrent.Immutable;
import java.io.Writer;

/**
 * Class that writes tables and individual columns to FixedWidth files
 */
@Immutable
final public class FixedWidthWriter {

    private final Table table;
    private final boolean header;
    private final Writer writer;
    private final FixedWidthWriterSettings settings;
    private final FixedWidthFormat format;

    /**
     * Private constructor to prevent instantiation
     */
    public FixedWidthWriter(Table table, FixedWidthWriteOptions options) {
        this.table = table;
        this.header = options.header();
        this.writer = options.writer();
        this.settings = fixedWidthWriterSettings(options);
        this.format = fixedWidthFormat(options);
        settings.setFormat(format);
    }

    public void write() {

        com.univocity.parsers.fixed.FixedWidthWriter fixedWidthWriter = null;
        // Creates a writer with the above settings;
        try {

            fixedWidthWriter = new com.univocity.parsers.fixed.FixedWidthWriter(writer, settings);
            if (header) {
                String[] header = new String[table.columnCount()];
                for (int c = 0; c < table.columnCount(); c++) {
                    header[c] = table.column(c).name();
                }
                fixedWidthWriter.writeHeaders(header);
            }
            for (int r = 0; r < table.rowCount(); r++) {
                String[] entries = new String[table.columnCount()];
                for (int c = 0; c < table.columnCount(); c++) {
                    table.get(r, c);
                    entries[c] = table.getUnformatted(r, c);
                }
                fixedWidthWriter.writeRow(entries);
            }
        } finally {
            if (fixedWidthWriter != null) {
                fixedWidthWriter.flush();
                fixedWidthWriter.close();
            }
        }
    }

    public Table getTable() {
        return table;
    }

    public boolean getHeader() {
        return header;
    }

    public FixedWidthWriterSettings getSettings() {
        return settings;
    }

    public FixedWidthFormat getFormat() {
        return format;
    }


    private FixedWidthFormat fixedWidthFormat(FixedWidthWriteOptions options) {
        FixedWidthFormat format = new FixedWidthFormat();

        if (options.padding() != ' ') {
            format.setPadding(options.padding());
        }
        if (options.lookupWildcard() != '?') {
            format.setLookupWildcard(options.lookupWildcard());
        }
        if (options.comment() != '#') {
            format.setComment(options.comment());
        }
        if (options.lineSeparator() != null) {
            format.setLineSeparator(options.lineSeparator());
        }
        if (options.lineSeparatorString() != null) {
            format.setLineSeparator(options.lineSeparatorString());
        }
        if (options.normalizedNewline() != '\n') {
            format.setNormalizedNewline(options.normalizedNewline());
        }

        return format;
    }

    private FixedWidthWriterSettings fixedWidthWriterSettings(FixedWidthWriteOptions options) {
        FixedWidthWriterSettings settings = new FixedWidthWriterSettings();

        if (options.autoConfigurationEnabled()) {
            settings.setAutoConfigurationEnabled(options.autoConfigurationEnabled());
        } else {
            columnRowSettings(settings, options);
            errorSettings(settings, options);
            skipIgnoreSettings(settings, options);
        }
        return settings;
    }

    private void columnRowSettings(FixedWidthWriterSettings settings, FixedWidthWriteOptions options) {
        if (options.defaultAlignmentForHeaders() != null) {
            settings.setDefaultAlignmentForHeaders(options.defaultAlignmentForHeaders());
        }
        if (options.columnReorderingEnabled()) {
            settings.setColumnReorderingEnabled(options.columnReorderingEnabled());
        }
        if (options.expandIncompleteRows()) {
            settings.setExpandIncompleteRows(options.expandIncompleteRows());
        }
        if (!options.defaultPaddingForHeaders()) {
            settings.setUseDefaultPaddingForHeaders(options.defaultPaddingForHeaders());
        }
        if (!options.writeLineSeparatorAfterRecord()) {
            settings.setWriteLineSeparatorAfterRecord(options.writeLineSeparatorAfterRecord());
        }
    }

    private void errorSettings(FixedWidthWriterSettings settings, FixedWidthWriteOptions options) {
        if (options.errorContentLength() <= -1) {
            settings.setErrorContentLength(options.errorContentLength());
        }
        if (options.nullValue() != null) {
            settings.setNullValue(options.nullValue());
        }
        if (options.emptyValue() != null) {
            settings.setEmptyValue(options.emptyValue());
        }
    }

    private void skipIgnoreSettings(FixedWidthWriterSettings settings, FixedWidthWriteOptions options) {
        if (!options.ignoreTrailingWhitespaces()) {
            settings.setIgnoreTrailingWhitespaces(options.ignoreTrailingWhitespaces());
        }
        if (!options.ignoreLeadingWhitespaces()) {
            settings.setIgnoreLeadingWhitespaces(options.ignoreLeadingWhitespaces());
        }
        if (!options.skipBitsAsWhitespace()) {
            settings.setSkipBitsAsWhitespace(options.skipBitsAsWhitespace());
        }
        if (!options.skipEmptyLines()) {
            settings.setSkipEmptyLines(options.skipEmptyLines());
        }
    }
}

