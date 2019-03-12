package tech.tablesaw.io.html;

import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriteOptions;

public class HtmlWriteOptions extends WriteOptions {

    protected HtmlWriteOptions(Builder builder) {
        super(builder);
    }

    public static Builder build(Destination dest) {
        return new Builder(dest);
    }

    public static class Builder extends WriteOptions.Builder {
        protected Builder(Destination dest) {
            super(dest);
        }

        public HtmlWriteOptions build() {
          return new HtmlWriteOptions(this);
        }
    }

}
