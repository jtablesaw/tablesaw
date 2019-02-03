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

package tech.tablesaw.io;

import com.google.common.base.Strings;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ReadOptions {

    // we always have one of these (file, reader, or inputStream)
    protected final File file;
    protected final Reader reader;
    protected final InputStream inputStream;

    protected final String tableName;
    protected final boolean sample;
    protected final String dateFormat;
    protected final String dateTimeFormat;
    protected final String timeFormat;
    protected final Locale locale;
    protected final String missingValueIndicator;

    protected ReadOptions(ReadOptions.Builder builder) {

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
        sample = builder.sample;
        dateFormat = builder.dateFormat;
        timeFormat = builder.timeFormat;
        dateTimeFormat = builder.dateTimeFormat;
        missingValueIndicator = builder.missingValueIndicator;

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

    public static class Builder {

        protected InputStream inputStream;
        protected File file;
        protected Reader reader;
        protected String tableName = "";
        protected boolean sample = true;
        protected String dateFormat;
        protected String timeFormat;
        protected String dateTimeFormat;
        protected Locale locale;
        protected String missingValueIndicator;

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

        public Builder missingValueIndicator(String missingValueIndicator) {
            this.missingValueIndicator = missingValueIndicator;
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

        public ReadOptions build() {
            return new ReadOptions(this);
        }
    }

}
