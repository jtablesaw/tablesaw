package tech.tablesaw.io.fixed;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Paths;

import com.univocity.parsers.fixed.FieldAlignment;
import com.univocity.parsers.fixed.FixedWidthFields;

public class FixedWidthWriteOptions {

    private final Writer writer;
    private final boolean header;
    private final char[] lineSeparator;
    private final String lineSeparatorString;
    private final FixedWidthFields columnSpecs;
    private final boolean defaultPaddingForHeaders;
    private final boolean skipBitsAsWhitespace;
    private final FieldAlignment defaultAlignmentForHeaders;
    private final boolean skipEmptyLines;
    private final boolean expandIncompleteRows;
    private final boolean autoConfigurationEnabled;
    private final int errorContentLength;
    private final boolean writeLineSeparatorAfterRecord;
    private final boolean ignoreTrailingWhitespaces;
    private final boolean ignoreLeadingWhitespaces;
    private final boolean columnReorderingEnabled;
    private final String emptyValue;
    private final String nullValue;
    private final char comment;
    private final char padding;
    private final char lookupWildcard;
    private final char normalizedNewline;

    private FixedWidthWriteOptions(Builder builder) {
        this.writer = builder.writer;
        this.header = builder.header;
        this.lineSeparator = builder.lineSeparator;
        this.lineSeparatorString = builder.lineSeparatorString;
        this.columnSpecs = builder.columnSpecs;
        this.defaultAlignmentForHeaders = builder.defaultAlignmentForHeaders;
        this.defaultPaddingForHeaders = builder.defaultPaddingForHeaders;
        this.skipBitsAsWhitespace = builder.skipBitsAsWhitespace;
        this.skipEmptyLines = builder.skipEmptyLines;
        this.expandIncompleteRows = builder.expandIncompleteRows;
        this.autoConfigurationEnabled = builder.autoConfigurationEnabled;
        this.writeLineSeparatorAfterRecord = builder.writeLineSeparatorAfterRecord;
        this.ignoreTrailingWhitespaces = builder.ignoreTrailingWhitespaces;
        this.ignoreLeadingWhitespaces = builder.ignoreLeadingWhitespaces;
        this.columnReorderingEnabled = builder.columnReorderingEnabled;
        this.errorContentLength = builder.errorContentLength;
        this.emptyValue = builder.emptyValue;
        this.nullValue = builder.nullValue;
        this.comment = builder.comment;
        this.padding = builder.padding;
        this.lookupWildcard = builder.lookupWildcard;
        this.normalizedNewline = builder.normalizedNewline;
    }

    public Writer writer() {
        return writer;
    }

    public boolean header() {
        return header;
    }

    public char[] lineSeparator() {
        return lineSeparator;
    }
    public String lineSeparatorString() {
        return lineSeparatorString;
    }

    public FixedWidthFields columnSpecs() {
        return columnSpecs;
    }

    public boolean defaultPaddingForHeaders() {
        return defaultPaddingForHeaders;
    }

    public boolean skipBitsAsWhitespace() {
        return skipBitsAsWhitespace;
    }

    public FieldAlignment defaultAlignmentForHeaders() {
        return defaultAlignmentForHeaders;
    }

    public boolean skipEmptyLines() {
        return skipEmptyLines;
    }

    public boolean expandIncompleteRows() {
        return expandIncompleteRows;
    }

    public boolean autoConfigurationEnabled() {
        return autoConfigurationEnabled;
    }

    public int errorContentLength() {
        return errorContentLength;
    }

    public boolean writeLineSeparatorAfterRecord() {
        return writeLineSeparatorAfterRecord;
    }

    public boolean ignoreTrailingWhitespaces() {
        return ignoreTrailingWhitespaces;
    }

    public boolean ignoreLeadingWhitespaces() {
        return ignoreLeadingWhitespaces;
    }

    public boolean columnReorderingEnabled() {
        return columnReorderingEnabled;
    }

    public String emptyValue() {
        return emptyValue;
    }

    public String nullValue() {
        return nullValue;
    }

    public char comment() {
        return comment;
    }

    public char padding() {
        return padding;
    }

    public char lookupWildcard() {
        return lookupWildcard;
    }

    public char normalizedNewline() {
        return normalizedNewline;
    }

    public static Builder builder(File file) throws IOException {
        return new Builder(file);
    }

    public static Builder builder(String fileName) throws IOException {
        return builder(new File(fileName));
    }

    public static class Builder {

        private Writer writer;
        private boolean header = true;
        private char[] lineSeparator;
        private String lineSeparatorString;
        private FixedWidthFields columnSpecs;
        private boolean defaultPaddingForHeaders = true;
        private boolean skipBitsAsWhitespace = true;
        private FieldAlignment defaultAlignmentForHeaders;
        private boolean skipEmptyLines = true;
        private boolean expandIncompleteRows = false;
        private boolean autoConfigurationEnabled = true;
        private int errorContentLength = -1;
        private boolean writeLineSeparatorAfterRecord = true;
        private boolean ignoreTrailingWhitespaces = true;
        private boolean ignoreLeadingWhitespaces = true;
        private boolean columnReorderingEnabled = true;
        private String emptyValue;
        private String nullValue;
        private char comment = '#';
        private char padding = '_';
        private char lookupWildcard = '?';
        private char normalizedNewline = '\n';


        public Builder(String fileName) throws IOException {
            File file = Paths.get(fileName).toFile();
            this.writer = new FileWriter(file);
        }

        public Builder(File file) throws IOException {
            this.writer = new FileWriter(file);
        }

        public Builder(Writer writer) {
            this.writer = writer;
        }

        public Builder(OutputStream stream) {
            this.writer = new OutputStreamWriter(stream);
        }

        public FixedWidthWriteOptions.Builder lineSeparator(char[] lineSeparator) {
            this.lineSeparator = lineSeparator;
            return this;
        }

        public FixedWidthWriteOptions.Builder lineSeparatorString(String lineSeparatorString) {
            this.lineSeparatorString = lineSeparatorString;
            return this;
        }

        public FixedWidthWriteOptions.Builder header(boolean header) {
            this.header = header;
            return this;
        }

        public FixedWidthWriteOptions.Builder header(FixedWidthFields columnSpecs) {
            this.columnSpecs = columnSpecs;
            return this;
        }

        public FixedWidthWriteOptions.Builder defaultPaddingForHeaders(boolean defaultPaddingForHeaders) {
            this.defaultPaddingForHeaders = defaultPaddingForHeaders;
            return this;
        }

        public FixedWidthWriteOptions.Builder skipBitsAsWhitespace(boolean skipBitsAsWhitespace) {
            this.skipBitsAsWhitespace = skipBitsAsWhitespace;
            return this;
        }

        public FixedWidthWriteOptions.Builder defaultAlignmentForHeaders(FieldAlignment defaultAlignmentForHeaders) {
            this.defaultAlignmentForHeaders = defaultAlignmentForHeaders;
            return this;
        }

        public FixedWidthWriteOptions.Builder skipEmptyLines(boolean skipEmptyLines) {
            this.skipEmptyLines = skipEmptyLines;
            return this;
        }

        public FixedWidthWriteOptions.Builder expandIncompleteRows(boolean expandIncompleteRows) {
            this.expandIncompleteRows = expandIncompleteRows;
            return this;
        }

        public FixedWidthWriteOptions.Builder autoConfigurationEnabled(boolean autoConfigurationEnabled) {
            this.autoConfigurationEnabled = autoConfigurationEnabled;
            return this;
        }

        public FixedWidthWriteOptions.Builder errorContentLength(int errorContentLength) {
            this.errorContentLength = errorContentLength;
            return this;
        }

        public FixedWidthWriteOptions.Builder writeLineSeparatorAfterRecord(boolean writeLineSeparatorAfterRecord) {
            this.writeLineSeparatorAfterRecord = writeLineSeparatorAfterRecord;
            return this;
        }

        public FixedWidthWriteOptions.Builder ignoreTrailingWhitespaces(boolean ignoreTrailingWhitespaces) {
            this.ignoreTrailingWhitespaces = ignoreTrailingWhitespaces;
            return this;
        }

        public FixedWidthWriteOptions.Builder ignoreLeadingWhitespaces(boolean ignoreLeadingWhitespaces) {
            this.ignoreLeadingWhitespaces = ignoreLeadingWhitespaces;
            return this;
        }

        public FixedWidthWriteOptions.Builder columnReorderingEnabled(boolean columnReorderingEnabled) {
            this.columnReorderingEnabled = columnReorderingEnabled;
            return this;
        }

        public FixedWidthWriteOptions.Builder emptyValue(String emptyValue) {
            this.emptyValue = emptyValue;
            return this;
        }

        public FixedWidthWriteOptions.Builder nullValue(String nullValue) {
            this.nullValue = nullValue;
            return this;
        }

        public FixedWidthWriteOptions.Builder comment(char comment) {
            this.comment = comment;
            return this;
        }

        public FixedWidthWriteOptions.Builder padding(char padding) {
            this.padding = padding;
            return this;
        }

        public FixedWidthWriteOptions.Builder lookupWildcard(char lookupWildcard) {
            this.lookupWildcard = lookupWildcard;
            return this;
        }

        public FixedWidthWriteOptions.Builder normalizedNewline(char normalizedNewline) {
            this.normalizedNewline = normalizedNewline;
            return this;
        }

        public FixedWidthWriteOptions build() {
            return new FixedWidthWriteOptions(this);
        }
    }
}
