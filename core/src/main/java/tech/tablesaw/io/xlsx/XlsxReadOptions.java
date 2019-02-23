package tech.tablesaw.io.xlsx;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

import tech.tablesaw.io.ReadOptions;

public class XlsxReadOptions extends ReadOptions {

    protected XlsxReadOptions(final Builder builder) {
        super(builder);
    }

    public static Builder builder(File file) {
        Builder builder = new Builder(file);
		builder.tableName(file.getName());
		return builder;
    }

    public static Builder builder(String fileName) {
        return new Builder(new File(fileName));
    }
    
    public static class Builder extends ReadOptions.Builder {

        public Builder(final File file) {
            super(file);
        }

        public Builder(final InputStream stream) {
            super(stream);
        }

        public Builder(final Reader reader) {
            super(reader);
        }

        @Override
        public XlsxReadOptions build() {
            return new XlsxReadOptions(this);
        }
    }
}
