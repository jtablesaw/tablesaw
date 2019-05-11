package tech.tablesaw.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class WriteOptions {

    protected final Destination dest;

    protected WriteOptions(Builder builder) {
        this.dest = builder.dest;
    }

    public Destination destination() {
        return dest;
    }

    public static class Builder {

        protected Destination dest;

        protected Builder(Destination dest) {
            this.dest = dest;
        }

        protected Builder(OutputStream dest) {
            this.dest = new Destination(dest);
        }

        protected Builder(Writer dest) {
            this.dest = new Destination(dest);
        }

        protected Builder(File dest) throws IOException {
            this.dest = new Destination(dest);
        }
    }

}
