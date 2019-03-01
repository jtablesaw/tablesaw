package tech.tablesaw.io.xlsx;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

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
    }
}
