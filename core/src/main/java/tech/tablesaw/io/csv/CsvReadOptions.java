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
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CsvReadOptions {

    private final File file;
    private final Reader reader;
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

    private CsvReadOptions(CsvReadOptions.Builder builder) {
        file = builder.file;
        reader = builder.reader;
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

        if (builder.locale == null) {
            locale = Locale.getDefault();
        } else {
            locale = builder.locale;
        }
    }

    public static Builder builder(File file) {
        return new Builder().file(file).tableName(file.getName());
    }

    public static Builder builder(String fileName) {
        return builder(new File(fileName));
    }

    /**
     * This method buffers the entire InputStream. Use the method taking a File for large input
     */
    public static Builder builder(InputStream stream, String tableName) {
        return builder(new InputStreamReader(stream), tableName);
    }

    /**
     * This method buffers the entire InputStream. Use the method taking a File for large input
     */
    public static Builder builder(Reader reader, String tableName) {
        Builder builder = new Builder();
        return builder.reader(reader).tableName(tableName);
    }

    public File file() {
        return file;
    }

    public Reader reader() {
        return reader;
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

    public static class Builder {

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

        public Builder file(File file) {
            this.file = file;
            this.tableName = file.getName();
            return this;
        }

        public Builder reader(Reader reader) {
            this.reader = reader;
            return this;
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

        public CsvReadOptions build() {
            return new CsvReadOptions(this);
        }
    }
}
