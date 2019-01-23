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

import com.google.common.base.Strings;
import tech.tablesaw.api.ColumnType;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CsvReadOptions {

    // we always have one of these (file, reader, or inputStream)
    private final File file;
    private final Reader reader;
    private final InputStream inputStream;

    private final String tableName;
    private final ColumnType[] columnTypes;
    private final boolean header;
    private final Character separator;
    private final String lineEnding;
    private final boolean sample;
    private final String dateFormat;
    private final String dateTimeFormat;
    private final String timeFormat;
    private final Locale locale;
    private final String missingValueIndicator;
    private final Integer maxNumberOfColumns;

    private CsvReadOptions(CsvReadOptions.Builder builder) {

        int sourceCount = 0;
        if (builder.file != null) sourceCount++;
        if (builder.reader != null) sourceCount++;
        if (builder.inputStream != null) sourceCount++;

        if (sourceCount == 0) {
            throw new IllegalArgumentException("CsvReadOptions Builder configured with no data source");
        } else if (sourceCount > 1) {
            throw new IllegalArgumentException("CsvReadOptions Builder configured with more than one data source");
        }

        file = builder.file;
        reader = builder.reader;
        inputStream = builder.inputStream;
        tableName = builder.tableName;
        columnTypes = builder.columnTypes;
        header = builder.header;
        separator = builder.separator;
        sample = builder.sample;
        dateFormat = builder.dateFormat;
        timeFormat = builder.timeFormat;
        dateTimeFormat = builder.dateTimeFormat;
        lineEnding = builder.lineEnding;
        missingValueIndicator = builder.missingValueIndicator;
        maxNumberOfColumns = builder.maxNumberOfColumns;

        if (builder.locale == null) {
            locale = Locale.getDefault();
        } else {
            locale = builder.locale;
        }
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

    public File file() {
        return file;
    }

    public Reader reader() {
        return reader;
    }

    public InputStream inputStream() {
        return inputStream;
    }

    public String tableName() {
        return tableName;
    }

    public ColumnType[] columnTypes() {
        return columnTypes;
    }

    public boolean header() {
        return header;
    }

    public Character separator() {
        return separator;
    }

    public String lineEnding() {
        return lineEnding;
    }

    public boolean sample() {
        return sample;
    }

    public String missingValueIndicator() {
        return missingValueIndicator;
    }

    public Locale locale() {
        return locale;
    }

    public DateTimeFormatter dateTimeFormatter() {
        if (Strings.isNullOrEmpty(dateTimeFormat)) {
            return null;
        }
        return DateTimeFormatter.ofPattern(dateTimeFormat, locale);
    }

    public DateTimeFormatter timeFormatter() {
        if (Strings.isNullOrEmpty(timeFormat)) {
            return null;
        }
        return DateTimeFormatter.ofPattern(timeFormat, locale);
    }

    public DateTimeFormatter dateFormatter() {
        if (Strings.isNullOrEmpty(dateFormat)) {
            return null;
        }
        return DateTimeFormatter.ofPattern(dateFormat, locale);
    }

    public Integer maxNumberOfColumns() {
        return maxNumberOfColumns;
    }

    public static class Builder {

        private InputStream inputStream;
        private File file;
        private Reader reader;
        private String tableName = "";
        private boolean header = true;
        private Character separator = ',';
        private String lineEnding;
        private boolean sample = true;
        private ColumnType[] columnTypes;
        private String dateFormat;
        private String timeFormat;
        private String dateTimeFormat;
        private Locale locale;
        private String missingValueIndicator;
        private Integer maxNumberOfColumns = 10_000;

        public Builder(File file) {
            this.file = file;
            this.tableName = file.getName();
        }

        /**
         * This method may cause tablesaw to buffer the entire InputStream.
         * <p>
         * If you have a large amount of data, you can do one of the following:
         * 1. Use the method taking a File instead of a reader, or
         * 2. Provide the array of column types as an option. If you provide the columnType array,
         * we skip type detection and can avoid reading the entire file
         */
        public Builder(Reader reader) {
            this.reader = reader;
        }

        /**
         * This method may cause tablesaw to buffer the entire InputStream.
         * <p>
         * If you have a large amount of data, you can do one of the following:
         * 1. Use the method taking a File instead of a stream, or
         * 2. Provide the array of column types as an option. If you provide the columnType array,
         * we skip type detection and can avoid reading the entire file
         */
        public Builder(InputStream stream) {
            this.inputStream = stream;
        }

        public Builder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder dateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
            return this;
        }

        public Builder timeFormat(String timeFormat) {
            this.timeFormat = timeFormat;
            return this;
        }

        public Builder dateTimeFormat(String dateTimeFormat) {
            this.dateTimeFormat = dateTimeFormat;
            return this;
        }

        public Builder header(boolean header) {
            this.header = header;
            return this;
        }

        public Builder missingValueIndicator(String missingValueIndicator) {
            this.missingValueIndicator = missingValueIndicator;
            return this;
        }

        public Builder separator(char separator) {
            this.separator = separator;
            return this;
        }

        public Builder lineEnding(String lineEnding) {
            this.lineEnding = lineEnding;
            return this;
        }

        public Builder sample(boolean sample) {
            this.sample = sample;
            return this;
        }

        public Builder locale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public Builder columnTypes(ColumnType[] columnTypes) {
            this.columnTypes = columnTypes;
            return this;
        }

        /**
         * Defines maximal value of columns in csv file.
         * @param maxNumberOfColumns - must be positive integer. Default is 512.         *
         */
        public Builder maxNumberOfColumns(Integer maxNumberOfColumns) {
            this.maxNumberOfColumns = maxNumberOfColumns;
            return this;
        }

        public CsvReadOptions build() {
            return new CsvReadOptions(this);
        }
    }

}
