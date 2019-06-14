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

package tech.tablesaw.io.csv;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.io.ReadOptions;
import tech.tablesaw.io.Source;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CsvReadOptions extends ReadOptions {

    private final ColumnType[] columnTypes;
    private final Character separator;
    private final Character quoteChar;
    private final String lineEnding;
    private final Integer maxNumberOfColumns;
    private final Character commentPrefix;
    private final boolean lineSeparatorDetectionEnabled;

    private CsvReadOptions(CsvReadOptions.Builder builder) {
        super(builder);

        columnTypes = builder.columnTypes;
        separator = builder.separator;
        quoteChar = builder.quoteChar;
        lineEnding = builder.lineEnding;
        maxNumberOfColumns = builder.maxNumberOfColumns;
        commentPrefix = builder.commentPrefix;
        lineSeparatorDetectionEnabled = builder.lineSeparatorDetectionEnabled;
    }

    public static Builder builder(Source source) {
        return new Builder(source);
    }

    public static Builder builder(File file) {
        return new Builder(file).tableName(file.getName());
    }

    public static Builder builder(String fileName) {
        return new Builder(new File(fileName));
    }

    public static Builder builder(URL url) throws IOException {
        return new Builder(url);
    }

    public static Builder builderFromFile(String fileName) {
        return new Builder(new File(fileName));
    }

    public static Builder builderFromString(String contents) {
        return new Builder(new StringReader(contents));
    }

    public static Builder builderFromUrl(String url) throws IOException {
        return new Builder(new URL(url));
    }

    /**
     * This method may cause tablesaw to buffer the entire InputStream.
     * <p>
     * If you have a large amount of data, you can do one of the following:
     * 1. Use the method taking a File instead of a stream, or
     * 2. Provide the array of column types as an option. If you provide the columnType array,
     * we skip type detection and can avoid reading the entire file
     */
    public static Builder builder(InputStream stream) {
        return new Builder(stream);
    }

    /**
     * This method may cause tablesaw to buffer the entire InputStream.
     *
     * <p>
     * If you have a large amount of data, you can do one of the following:
     * 1. Use the method taking a File instead of a reader, or
     * 2. Provide the array of column types as an option. If you provide the columnType array,
     * we skip type detection and can avoid reading the entire file
     */
    public static Builder builder(Reader reader) {
        return new Builder(reader);
    }

    public ColumnType[] columnTypes() {
        return columnTypes;
    }

    public Character separator() {
        return separator;
    }

    public Character quoteChar() {
        return quoteChar;
    }

    public String lineEnding() {
        return lineEnding;
    }

    public boolean lineSeparatorDetectionEnabled() {
        return lineSeparatorDetectionEnabled;
    }

    public Integer maxNumberOfColumns() {
        return maxNumberOfColumns;
    }

    public Character commentPrefix() {
        return commentPrefix;
    }

    public int maxCharsPerColumn() {
        return maxCharsPerColumn;
    }

    public static class Builder extends ReadOptions.Builder {

        private Character separator = ',';
        private Character quoteChar;
        private String lineEnding;
        private ColumnType[] columnTypes;
        private Integer maxNumberOfColumns = 10_000;
        private Character commentPrefix;
        private boolean lineSeparatorDetectionEnabled = true;

        protected Builder(Source source) {
            super(source);
        }

        protected Builder(URL url) throws IOException {
            super(url);
        }

        protected Builder(File file) {
            super(file);
        }

        protected Builder(Reader reader) {
            super(reader);
        }

        protected Builder(InputStream stream) {
            super(stream);
        }

        public Builder columnTypes(ColumnType[] columnTypes) {
            this.columnTypes = columnTypes;
            return this;
        }

        public Builder separator(Character separator) {
            this.separator = separator;
            return this;
        }

        public Builder quoteChar(Character quoteChar) {
            this.quoteChar = quoteChar;
            return this;
        }

        public Builder lineEnding(String lineEnding) {
            this.lineEnding = lineEnding;
            this.lineSeparatorDetectionEnabled = false;
            return this;
        }

        /**
         * Defines maximal value of columns in csv file.
         * @param maxNumberOfColumns - must be positive integer. Default is 512.
         */
        public Builder maxNumberOfColumns(Integer maxNumberOfColumns) {
            this.maxNumberOfColumns = maxNumberOfColumns;
            return this;
        }

        public Builder commentPrefix(Character commentPrefix) {
            this.commentPrefix = commentPrefix;
            return this;
        }

        public CsvReadOptions build() {
            return new CsvReadOptions(this);
        }
        
        // Override super-class setters to return an instance of this class

        @Override
        public Builder header(boolean header) {
            super.header(header);
            return this;
        }

        @Override
        public Builder tableName(String tableName) {
            super.tableName(tableName);
            return this;
        }

        @Override
        public Builder sample(boolean sample) {
            super.sample(sample);
            return this;
        }

        @Override
        @Deprecated
        public Builder dateFormat(String dateFormat) {
            super.dateFormat(dateFormat);
            return this;
        }

        @Override
        @Deprecated
        public Builder timeFormat(String timeFormat) {
            super.timeFormat(timeFormat);
            return this;
        }

        @Override
        @Deprecated
        public Builder dateTimeFormat(String dateTimeFormat) {
            super.dateTimeFormat(dateTimeFormat);
            return this;
        }

        @Override
        public Builder dateFormat(DateTimeFormatter dateFormat) {
            super.dateFormat(dateFormat);
            return this;
        }

        @Override
        public Builder timeFormat(DateTimeFormatter timeFormat) {
            super.timeFormat(timeFormat);
            return this;
        }

        @Override
        public Builder dateTimeFormat(DateTimeFormatter dateTimeFormat) {
            super.dateTimeFormat(dateTimeFormat);
            return this;
        }

        public Builder maxCharsPerColumn(int maxCharsPerColumn) {
            super.maxCharsPerColumn(maxCharsPerColumn);
            return this;
        }

        @Override
        public Builder locale(Locale locale) {
            super.locale(locale);
            return this;
        }

        @Override
        public Builder missingValueIndicator(String missingValueIndicator) {
            super.missingValueIndicator(missingValueIndicator);
            return this;
        }

        @Override
        public Builder minimizeColumnSizes() {
            super.minimizeColumnSizes();
            return this;
        }
    }
}
