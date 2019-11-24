package tech.tablesaw.io.orc;

import org.apache.orc.OrcFile;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriteOptions;

import java.io.File;
import java.io.IOException;

public class OrcWriteOptions extends WriteOptions {

    private OrcFile.WriterOptions writerOptions;
    private File outputPath;

    protected OrcWriteOptions(Builder builder) {
        super(builder);
        writerOptions = builder.writerOptions;
        outputPath = builder.outputFile;
    }

    public OrcFile.WriterOptions getWriterOptions() {
        return writerOptions;
    }

    public File getOutputPath() {
        return outputPath;
    }

    public static Builder builder(File file) throws IOException {
        return new Builder(file);
    }

    public static Builder builder(Destination destination) {
        return new Builder(destination);
    }

    static class Builder extends WriteOptions.Builder{
        private OrcFile.WriterOptions writerOptions;
        private File outputFile;
        protected Builder(Destination dest) {
            super(dest);
        }
        protected Builder(File file) throws IOException {
            super(file);
            outputFile = file;
        }
        public Builder ocrWriteOptions(OrcFile.WriterOptions writerOptions) {
            this.writerOptions = writerOptions;
            return this;
        }
        public OrcWriteOptions build() {
            return new OrcWriteOptions(this);
        }
    }
}
