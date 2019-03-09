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

import com.univocity.parsers.fixed.FixedWidthFields;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.io.ReadOptions;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;

public class FixedWidthReadOptions extends ReadOptions {

    // we always have one of these (file, reader, or inputStream)
    private final ColumnType[] columnTypes;
    private final FixedWidthFields columnSpecs;
    private final String lineEnding;
    private final char padding;
    private final char lookupWildcard;
    private final boolean skipTrailingCharsUntilNewline;
    private final boolean recordEndsOnNewline;
    private final boolean skipInvalidRows;
    private final Integer maxNumberOfColumns;

    private FixedWidthReadOptions(FixedWidthReadOptions.Builder builder) {
        super(builder);

        columnTypes = builder.columnTypes;
        columnSpecs = builder.columnSpecs;
        padding = builder.padding;
        lookupWildcard = builder.lookupWildcard;
        skipTrailingCharsUntilNewline = builder.skipTrailingCharsUntilNewline;
        recordEndsOnNewline = builder.recordEndsOnNewline;
        skipInvalidRows = builder.skipInvalidRows;
        lineEnding = builder.lineEnding;
        maxNumberOfColumns = builder.maxNumberOfColumns;
    }

    public static Builder builder(File file) {
        return new Builder(file).tableName(file.getName());
    }

    public static Builder builder(String fileName) {
        return new Builder(new File(fileName));
    }


    /**
     * This method may cause tablesaw to buffer the entire InputStream.
     * <p>
     * If you have a large amount of data, you can do one of the following:
     * 1. Use the method taking a File instead of a stream, or
     * 2. Provide the array of column types as an option. If you provide the columnType array,
     * we skip type detection and can avoid reading the entire file
     */
    public static Builder builder(InputStream stream, String tableName) {
        return new Builder(stream).tableName(tableName);
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
    public static Builder builder(Reader reader, String tableName) {
        Builder builder = new Builder(reader);
        return builder.tableName(tableName);
    }

    public ColumnType[] columnTypes() {
        return columnTypes;
    }

    public FixedWidthFields columnSpecs() {
        return columnSpecs;
    }

    public String lineEnding() {
        return lineEnding;
    }

    public char padding() {
        return padding;
    }

    public char lookupWildcard() {
        return lookupWildcard;
    }

    public boolean skipTrailingCharsUntilNewline() {
        return skipTrailingCharsUntilNewline;
    }

    public boolean recordEndsOnNewline() {
        return recordEndsOnNewline;
    }

    public boolean skipInvalidRows() {
        return skipInvalidRows;
    }

    public Integer maxNumberOfColumns() {
        return maxNumberOfColumns;
    }
    
    public static class Builder extends ReadOptions.Builder {

        protected FixedWidthFields columnSpecs;
        protected String lineEnding;
        protected char padding = ' ';
        protected char lookupWildcard = '?';
        protected boolean skipTrailingCharsUntilNewline = false;
        protected boolean recordEndsOnNewline = false;
        protected boolean skipInvalidRows = false;
        protected ColumnType[] columnTypes;
        protected Integer maxNumberOfColumns = 10_000;

        protected Builder(File file) {
            super(file);
        }

        protected Builder(Reader reader) {
            super(reader);
        }

        protected Builder(InputStream stream) {
            super(stream);
        }

        public Builder columnSpecs(FixedWidthFields columnSpecs) {
            this.columnSpecs = columnSpecs;
            return this;
        }


        public Builder lineEnding(String lineEnding) {
            this.lineEnding = lineEnding;
            return this;
        }

        public Builder padding(char padding) {
            this.padding = padding;
            return this;
        }

        public Builder lookupWildcard(char lookupWildcard) {
            this.lookupWildcard = lookupWildcard;
            return this;
        }

        public Builder skipTrailingCharsUntilNewline(boolean skipTrailingCharsUntilNewline) {
            this.skipTrailingCharsUntilNewline = skipTrailingCharsUntilNewline;
            return this;
        }

        public Builder recordEndsOnNewline(boolean recordEndsOnNewline) {
            this.recordEndsOnNewline = recordEndsOnNewline;
            return this;
        }

        public Builder skipInvalidRows(boolean skipInvalidRows) {
            this.skipInvalidRows = skipInvalidRows;
            return this;
        }

        public Builder columnTypes(ColumnType[] columnTypes) {
            this.columnTypes = columnTypes;
            return this;
        }

        /**
         * Defines maximal value of columns in fixed-width file.
         *
         * @param maxNumberOfColumns - must be positive integer. Default is 512.         *
         */
        public Builder maxNumberOfColumns(Integer maxNumberOfColumns) {
            this.maxNumberOfColumns = maxNumberOfColumns;
            return this;
        }

        public FixedWidthReadOptions build() {
            return new FixedWidthReadOptions(this);
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
        public Builder dateFormat(String dateFormat) {
            super.dateFormat(dateFormat);
            return this;
        }

        @Override
        public Builder timeFormat(String timeFormat) {
            super.timeFormat(timeFormat);
            return this;
        }

        @Override
        public Builder dateTimeFormat(String dateTimeFormat) {
            super.dateTimeFormat(dateTimeFormat);
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
        public Builder minimizeColumnSizes(boolean minimizeColumnSizes) {
            super.minimizeColumnSizes(minimizeColumnSizes);
            return this;
        }
    }
}
