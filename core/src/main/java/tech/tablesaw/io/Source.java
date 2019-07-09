package tech.tablesaw.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Source {

    // we always have one of these (file, reader, or inputStream)
    protected final File file;
    protected final Reader reader;
    protected final InputStream inputStream;
    protected final Charset charset;

    public Source(File file) {
        this(file, Charset.defaultCharset());
    }

    public Source(File file, Charset charset) {
        this.file = file;
        this.reader = null;
        this.inputStream = null;
        this.charset = charset;
    }

    public Source(Reader reader) {
        this.file = null;
        this.reader = reader;
        this.inputStream = null;
        this.charset = null;
    }

    public Source(InputStream inputStream) {
        this(inputStream, Charset.defaultCharset());
    }

    public Source(InputStream inputStream, Charset charset) {
        this.file = null;
        this.reader = null;
        this.inputStream = inputStream;
        this.charset = charset;
    }

    public static Source fromString(String s) {
        return new Source(new StringReader(s));
    }

    public static Source fromUrl(String url) throws IOException {
        return new Source(new StringReader(loadUrl(url)));
    }

    public File file() {
        return file;
    }

    public Reader reader() {
        return reader;
    }

    public InputStream inputStream() {
        return inputStream;
    }

    /**
     * If cachedBytes are not null, returns a Reader created from the cachedBytes.
     * Otherwise, returns a Reader from the underlying source.
     */
    public Reader createReader(byte[] cachedBytes) throws IOException {
        if (cachedBytes != null) {
            return new InputStreamReader(new ByteArrayInputStream(cachedBytes));
        }
        if (inputStream != null) {
            return new InputStreamReader(inputStream, charset);
        }
        if (reader != null) {
            return reader;
        }
        return new InputStreamReader(new FileInputStream(file), charset);
    }

    private static String loadUrl(String url) throws IOException {
        try (Scanner scanner = new Scanner(new URL(url).openStream(), StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}
