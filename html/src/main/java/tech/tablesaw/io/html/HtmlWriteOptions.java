package tech.tablesaw.io.html;

import org.jsoup.nodes.Element;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.WriteOptions;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class HtmlWriteOptions extends WriteOptions {

    private final ElementCreator elementCreator;
    private final boolean escapeText;

    protected HtmlWriteOptions(Builder builder) {
        super(builder);
        this.escapeText = builder.escapeText;
        this.elementCreator = builder.elementCreator;
    }

    public boolean escapeText() {
        return escapeText;
    }

    public ElementCreator elementCreator() {
        return elementCreator;
    }

    public static Builder builder(Destination dest) {
        return new Builder(dest);
    }

    public static Builder builder(OutputStream dest) {
        return new Builder(dest);
    }

    public static Builder builder(Writer dest) {
        return new Builder(dest);
    }

    public static Builder builder(File dest) throws IOException {
        return new Builder(dest);
    }

    public static Builder builder(String fileName) throws IOException {
        return builder(new File(fileName));
    }

    public static class Builder extends WriteOptions.Builder {
        private ElementCreator elementCreator
                = (elementName, column, row) -> new Element(elementName);

        private boolean escapeText = true;

        protected Builder(Destination dest) {
            super(dest);
        }

        protected Builder(File file) throws IOException {
            super(file);
        }

        public Builder escapeText(boolean escapeText) throws IOException {
            this.escapeText = escapeText;
            return this;
        }

        protected Builder(Writer writer) {
            super(writer);
        }

        protected Builder(OutputStream stream) {
            super(stream);
        }

        public Builder elementCreator(ElementCreator elementCreator) {
            this.elementCreator = elementCreator;
            return this;
        }

        public HtmlWriteOptions build() {
            return new HtmlWriteOptions(this);
        }
    }

    public static interface ElementCreator {
        /**
         * Called for each element created. Used as a hook to add classes or
         * other attributes to the element.
         * @param elementName element type to create. E.g. table, thead, tbody, tr, th, td
         * @param column the column this table cell corresponds to. null if not a td or th
         * @param row the row this table cell corresponds to. null if not a td or tr in table body
         * @return HTML element
         */
        Element create(String elementName, Column<?> column, Integer row);

        default Element create(String elementName) {
            return create(elementName, null, null);
        }
    }
}
