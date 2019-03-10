package tech.tablesaw.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Source {

    protected final File file;
    protected final Reader reader;
    protected final InputStream inputStream;

    public Source(File file) {
	this.file = file;
	this.reader = null;
	this.inputStream = null;
    }

    public Source(Reader reader) {
	this.file = null;
	this.reader = reader;
	this.inputStream = null;
    }

    public Source(InputStream inputStream) {
	this.file = null;
	this.reader = null;
	this.inputStream = inputStream;
    }

    public static Source fromString(String s) {
	return new Source(new StringReader(s));
    }

    public static Source fromFile(String file) {
	return new Source(new File(file));
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

    public Reader createReader(byte[] cachedBytes) throws IOException {
	if (cachedBytes != null) {
	    return new InputStreamReader(new ByteArrayInputStream(cachedBytes));
	}
        if (inputStream != null) {
            return new InputStreamReader(inputStream);
        }
        if (reader != null) {
            return reader;
        }
        return new FileReader(file);
    }

    private static String loadUrl(String url) throws IOException {
        try (Scanner scanner = new Scanner(new URL(url).openStream(), StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
	}
    }
}
