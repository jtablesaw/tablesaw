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
import java.io.IOException;
import java.io.Writer;
import javax.annotation.concurrent.Immutable;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.DataWriter;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriterRegistry;

/** Class that writes tables and individual columns to FixedWidth files */
@Immutable
public final class FixedWidthWriter implements DataWriter<FixedWidthWriteOptions> {

  private static final FixedWidthWriter INSTANCE = new FixedWidthWriter();

  static {
    register(Table.defaultWriterRegistry);
  }

  public static void register(WriterRegistry registry) {
    registry.registerOptions(FixedWidthWriteOptions.class, INSTANCE);
  }

  public void write(Table table, FixedWidthWriteOptions options) {
    FixedWidthWriterSettings settings = fixedWidthWriterSettings(options);
    settings.setFormat(fixedWidthFormat(options));

    com.univocity.parsers.fixed.FixedWidthWriter fixedWidthWriter = null;
    // Creates a writer with the above settings;
    try {
      Writer writer = options.destination().createWriter();
      fixedWidthWriter = new com.univocity.parsers.fixed.FixedWidthWriter(writer, settings);
      if (options.header()) {
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
        if (options.autoClose()) fixedWidthWriter.close();
      }
    }
  }

  protected FixedWidthFormat fixedWidthFormat(FixedWidthWriteOptions options) {
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

  protected FixedWidthWriterSettings fixedWidthWriterSettings(FixedWidthWriteOptions options) {
    FixedWidthWriterSettings settings = new FixedWidthWriterSettings();
    if (options.columnSpecs() != null) {
      settings = new FixedWidthWriterSettings(options.columnSpecs());
    }

    if (options.autoConfigurationEnabled()) {
      settings.setAutoConfigurationEnabled(options.autoConfigurationEnabled());
    } else {
      columnRowSettings(settings, options);
      errorSettings(settings, options);
      skipIgnoreSettings(settings, options);
    }
    return settings;
  }

  protected void columnRowSettings(
      FixedWidthWriterSettings settings, FixedWidthWriteOptions options) {
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

  protected void errorSettings(FixedWidthWriterSettings settings, FixedWidthWriteOptions options) {
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

  protected void skipIgnoreSettings(
      FixedWidthWriterSettings settings, FixedWidthWriteOptions options) {
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

  @Override
  public void write(Table table, Destination dest) throws IOException {
    write(table, FixedWidthWriteOptions.builder(dest).build());
  }
}
