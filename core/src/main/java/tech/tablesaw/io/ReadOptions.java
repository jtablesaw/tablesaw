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
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
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

    private static final List<ColumnType> DEFAULT_TYPES = Lists.newArrayList(
            LOCAL_DATE_TIME,
            LOCAL_TIME,
            LOCAL_DATE,
            BOOLEAN,
            INTEGER,
            LONG,
            DOUBLE,
            STRING);

    /**
     * An extended list of types that are used if minimizeColumnSizes is true. By including extra types like Short
     * the resulting table size is reduced at the cost of some additional complexity for the programmer if, for example,
     * they will subsequently modify the data in a way that exceeds the range of the type.
     */
    private static final List<ColumnType> EXTENDED_TYPES =
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

    protected final Source source;
    protected final String tableName;
    protected final List<ColumnType> columnTypesToDetect;
    protected final boolean sample;
    protected final String dateFormat;
    protected final String dateTimeFormat;
    protected final String timeFormat;
    protected final Locale locale;
    protected final String missingValueIndicator;
    protected final boolean minimizeColumnSizes;
    protected final int maxCharsPerColumn;

    protected final DateTimeFormatter dateFormatter;
    protected final DateTimeFormatter dateTimeFormatter;
    protected final DateTimeFormatter timeFormatter;

    protected final boolean header;

    protected ReadOptions(ReadOptions.Builder builder) {
        source = builder.source;
        tableName = builder.tableName;
        columnTypesToDetect = builder.columnTypesToDetect;
        sample = builder.sample;
        dateFormat = builder.dateFormat;
        timeFormat = builder.timeFormat;
        dateTimeFormat = builder.dateTimeFormat;
        missingValueIndicator = builder.missingValueIndicator;
        minimizeColumnSizes = builder.minimizeColumnSizes;
        header = builder.header;
        maxCharsPerColumn = builder.maxCharsPerColumn;

        dateFormatter = builder.dateFormatter;
        timeFormatter = builder.timeFormatter;
        dateTimeFormatter = builder.dateTimeFormatter;

        if (builder.locale == null) {
            locale = Locale.getDefault();
        } else {
            locale = builder.locale;
        }
    }

    public Source source() {
        return source;
    }

    public String tableName() {
        return tableName;
    }

    public List<ColumnType> columnTypesToDetect() {
        return columnTypesToDetect;
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
        if (dateTimeFormatter != null) {
            return dateTimeFormatter;
        }

        if (Strings.isNullOrEmpty(dateTimeFormat)) {
            return null;
        }
        return DateTimeFormatter.ofPattern(dateTimeFormat, locale);
    }

    public DateTimeFormatter timeFormatter() {
        if (timeFormatter != null) {
            return timeFormatter;
        }
        if (Strings.isNullOrEmpty(timeFormat)) {
            return null;
        }
        return DateTimeFormatter.ofPattern(timeFormat, locale);
    }

    public DateTimeFormatter dateFormatter() {
        if (dateFormatter != null) {
            return dateFormatter;
        }
        if (Strings.isNullOrEmpty(dateFormat)) {
            return null;
        }
        return DateTimeFormatter.ofPattern(dateFormat, locale);
    }

    protected static class Builder {

        protected final Source source;
        protected String tableName = "";
        protected List<ColumnType> columnTypesToDetect = DEFAULT_TYPES;
        protected boolean sample = true;
        protected String dateFormat;
        protected DateTimeFormatter dateFormatter;
        protected String timeFormat;
        protected DateTimeFormatter timeFormatter;
        protected String dateTimeFormat;
        protected DateTimeFormatter dateTimeFormatter;
        protected Locale locale;
        protected String missingValueIndicator;
        protected boolean minimizeColumnSizes = false;
        protected boolean header = true;
        protected int maxCharsPerColumn = 4096;

        protected Builder() {
            source = null;
        }
        
        protected Builder(Source source) {
            this.source = source;
        }

        protected Builder(File file) {
            this.source = new Source(file);
            this.tableName = file.getName();
        }

        protected Builder(URL url) throws IOException {
            this.source = new Source(url.openStream());
            this.tableName = url.toString();
        }

        protected Builder(InputStream stream) {
            this.source = new Source(stream);
        }

        protected Builder(Reader reader) {
            this.source = new Source(reader);
        }

        public Builder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder header(boolean hasHeader) {
            this.header = hasHeader;
            return this;
        }

        /**
         * Deprecated. Use dateFormat(DateTimeFormatter dateFormat) instead
         */
        @Deprecated
        public Builder dateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
            return this;
        }

        public Builder dateFormat(DateTimeFormatter dateFormat) {
            this.dateFormatter = dateFormat;
            return this;
        }

        /**
         * Deprecated. Use timeFormat(DateTimeFormatter dateFormat) instead
         */
        @Deprecated
        public Builder timeFormat(String timeFormat) {
            this.timeFormat = timeFormat;
            return this;
        }

        public Builder timeFormat(DateTimeFormatter dateFormat) {
            this.timeFormatter = dateFormat;
            return this;
        }

        /**
         * Deprecated. Use dateTimeFormat(DateTimeFormatter dateFormat) instead
         */
        @Deprecated
        public Builder dateTimeFormat(String dateTimeFormat) {
            this.dateTimeFormat = dateTimeFormat;
            return this;
        }

        public Builder dateTimeFormat(DateTimeFormatter dateFormat) {
            this.dateTimeFormatter = dateFormat;
            return this;
        }

        public Builder missingValueIndicator(String missingValueIndicator) {
            this.missingValueIndicator = missingValueIndicator;
            return this;
        }

        public Builder maxCharsPerColumn(int maxCharsPerColumn) {
            this.maxCharsPerColumn = maxCharsPerColumn;
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

        /**
         * @see ColumnTypeDetector
         */
        public Builder columnTypesToDetect(List<ColumnType> columnTypesToDetect) {
            this.columnTypesToDetect = columnTypesToDetect;
            return this;
        }

        /**
         * Allow the {@link ColumnTypeDetector} to choose shorter column types such as float
         * instead of double when the data will fit in a smaller type
         */
        public Builder minimizeColumnSizes() {
            this.columnTypesToDetect = EXTENDED_TYPES;
            return this;
        }
        
        public ReadOptions build() {
            return new ReadOptions(this);
        }
    }

}
