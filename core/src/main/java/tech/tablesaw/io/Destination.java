package tech.tablesaw.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class Destination {

    protected final Writer writer;

    public Destination(File file) throws IOException {
	this.writer = new FileWriter(file);
    }

    public Destination(Writer writer) {
	this.writer = writer;
    }

    public Destination(OutputStream stream) {
	this.writer = new OutputStreamWriter(stream);
    }

    public Writer writer() {
	return writer;
    }

}
