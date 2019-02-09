package tech.tablesaw.io.json;

import java.io.IOException;

public class JsonWriteOptions {

    private final boolean asObjects;
    private final boolean header;

    private JsonWriteOptions(Builder builder) {
        this.asObjects = builder.asObjects;
        this.header = builder.header;
    }

    public boolean asObjects() {
        return asObjects;
    }

    public boolean header() {
        return header;
    }

    public static Builder builder() throws IOException {
        return new Builder();
    }

    public static class Builder {

        private boolean asObjects = true;
        private boolean header = false;

        /**
         * If true writes each row as an object. If false writes each row as an array.
         */
        public JsonWriteOptions.Builder asObjects(boolean asObjects) {
            this.asObjects = asObjects;
            return this;
        }

        /**
         * Whether to write a header row. Only used if asObjects is false.
         */
        public JsonWriteOptions.Builder header(boolean header) {
            this.header = header;
            return this;
        }

        public JsonWriteOptions build() {
            return new JsonWriteOptions(this);
        }
    }
}
