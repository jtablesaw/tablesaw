package tech.tablesaw.io.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Paths;

public class CsvWriteOptions {

    private final Writer writer;
    private final boolean header;
    private final char separator;
    private final char quotechar;
    private final char escapechar;
    private final String lineEnd;

    private CsvWriteOptions(Builder builder) {
        this.writer = builder.writer;
        this.header = builder.header;
        this.separator = builder.separator;
        this.quotechar = builder.quoteChar;
        this.escapechar = builder.escapeChar;
        this.lineEnd = builder.lineEnd;
    }

    Writer writer() {
        return writer;
    }

    boolean header() {
        return header;
    }

    char separator() {
        return separator;
    }

    char escapeChar() {
        return escapechar;
    }

    char quoteChar() {
        return quotechar;
    }

    String lineEnd() {
        return lineEnd;
    }

    public static Builder builder(File file) {
        return new Builder(file);
    }

    public static Builder builder(String fileName) {
        return builder(new File(fileName));
    }

    public static class Builder {

        private Writer writer;
        private boolean header = true;
        private char separator = ',';
//        private String lineEnd = CSVWriter.DEFAULT_LINE_END;
//        private char escapeChar = CSVWriter.DEFAULT_ESCAPE_CHARACTER;
//        private char quoteChar = CSVWriter.NO_QUOTE_CHARACTER;
        private String lineEnd = "\n";
        private char escapeChar = '\\';
        private char quoteChar = '"';

        public Builder(String fileName) throws IOException {
            File file = Paths.get(fileName).toFile();
            this.writer = new FileWriter(file);
        }

        public Builder(File file) {
            try {
                this.writer = new FileWriter(file);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        public Builder(Writer writer) {
            this.writer = writer;
        }

        public Builder(OutputStream stream) {
            this.writer = new OutputStreamWriter(stream);
        }

        public CsvWriteOptions.Builder separator(char separator) {
            this.separator = separator;
            return this;
        }

        public CsvWriteOptions.Builder quoteChar(char quoteChar) {
            this.quoteChar = quoteChar;
            return this;
        }

        public CsvWriteOptions.Builder escapeChar(char escapeChar) {
            this.escapeChar = escapeChar;
            return this;
        }

        public CsvWriteOptions.Builder lineEnd(String lineEnd) {
            this.lineEnd = lineEnd;
            return this;
        }

        public CsvWriteOptions.Builder header(boolean header) {
            this.header = header;
            return this;
        }

        public CsvWriteOptions build() {
            return new CsvWriteOptions(this);
        }
    }
}
