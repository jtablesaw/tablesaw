package tech.tablesaw.io.xlsx;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;

import tech.tablesaw.io.ReadOptions;

public class XlsxReadOptions extends ReadOptions {

    protected XlsxReadOptions(Builder builder) {
        super(builder);
    }

    public static Builder builder(File file) {
        return new Builder(file);
    }

    public static Builder builder(String fileName) {
        return new Builder(new File(fileName));
    }
    
    public static class Builder extends ReadOptions.Builder {

        public Builder(File file) {
            super(file);
        }

        public Builder(InputStream stream) {
            super(stream);
        }

        public Builder(Reader reader) {
            super(reader);
        }

        @Override
        public XlsxReadOptions build() {
            return new XlsxReadOptions(this);
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
