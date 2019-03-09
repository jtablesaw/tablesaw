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
import com.google.common.collect.Lists;

import tech.tablesaw.api.ColumnType;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static tech.tablesaw.api.ColumnType.BOOLEAN;
import static tech.tablesaw.api.ColumnType.DOUBLE;
import static tech.tablesaw.api.ColumnType.FLOAT;
import static tech.tablesaw.api.ColumnType.INTEGER;
import static tech.tablesaw.api.ColumnType.LOCAL_DATE;
import static tech.tablesaw.api.ColumnType.LOCAL_DATE_TIME;
import static tech.tablesaw.api.ColumnType.LOCAL_TIME;
import static tech.tablesaw.api.ColumnType.LONG;
import static tech.tablesaw.api.ColumnType.SHORT;
import static tech.tablesaw.api.ColumnType.STRING;
import static tech.tablesaw.api.ColumnType.TEXT;

public class ReadOptions {

    /**
     * An extended list of types that are used if minimizeColumnSizes is true. By including extra types like Short
     * the resulting table size is reduced at the cost of some additional complexity for the programmer if, for example,
     * they will subsequently modify the data in a way that exceeds the range of the type.
     */
    public static final List<ColumnType> EXTENDED_TYPE_ARRAY =
            Lists.newArrayList(
                    LOCAL_DATE_TIME,
                    LOCAL_TIME,
                    LOCAL_DATE,
                    BOOLEAN,
                    SHORT,
                    INTEGER,
                    LONG,
                    FLOAT,
                    DOUBLE,
                    STRING,
                    TEXT);

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
    protected final boolean minimizeColumnSizes;
    protected final boolean header;

    protected ReadOptions(ReadOptions.Builder builder) {

        int sourceCount = 0;
        if (builder.file != null) sourceCount++;
        if (builder.reader != null) sourceCount++;
        if (builder.inputStream != null) sourceCount++;

        if (sourceCount == 0) {
            throw new IllegalArgumentException("ReadOptions Builder configured with no data source");
        } else if (sourceCount > 1) {
            throw new IllegalArgumentException("ReadOptions Builder configured with more than one data source");
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
        minimizeColumnSizes = builder.minimizeColumnSizes;
        header = builder.header;

        if (builder.locale == null) {
            locale = Locale.getDefault();
        } else {
            locale = builder.locale;
        }
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

    public boolean minimizeColumnSizes() {
        return minimizeColumnSizes;
    }

    public String missingValueIndicator() {
        return missingValueIndicator;
    }

    public Locale locale() {
        return locale;
    }

    public boolean header() {
        return header;
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

    protected static class Builder {

        protected final InputStream inputStream;
        protected final File file;
        protected final Reader reader;
        protected String tableName = "";
        protected boolean sample = true;
        protected String dateFormat;
        protected String timeFormat;
        protected String dateTimeFormat;
        protected Locale locale;
        protected String missingValueIndicator;
        protected boolean minimizeColumnSizes = false;
        protected boolean header = true;

        protected Builder(File file) {
      	    this.inputStream = null;
            this.file = file;
            this.reader = null;
            this.tableName = file.getName();
        }

        protected Builder(Reader reader) {
      	    this.inputStream = null;
            this.file = null;
            this.reader = reader;
        }

        protected Builder(InputStream stream) {
            this.inputStream = stream;
            this.file = null;
            this.reader = null;
        }

        protected Builder(Reader reader, String tableName) {
            this.inputStream = null;
            this.file = null;
            this.reader = reader;
            this.tableName = tableName;
        }

        protected Builder(InputStream stream, String tableName) {
            this.inputStream = stream;
            this.file = null;
            this.reader = null;
            this.tableName = tableName;
        }

        public Builder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder dateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
            return this;
        }

        public Builder header(boolean hasHeader) {
            this.header = hasHeader;
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

        public Builder minimizeColumnSizes(boolean minimize) {
            this.minimizeColumnSizes = minimize;
            return this;
        }
    }

}
