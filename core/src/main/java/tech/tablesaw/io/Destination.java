package tech.tablesaw.io;

import java.io.*;

public class Destination {

  protected final OutputStream stream;
  protected final Writer writer;

  public Destination(File file) {
    try {
      this.stream = new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      throw new RuntimeIOException(e);
    }
    this.writer = null;
  }

  public Destination(Writer writer) {
    this.stream = null;
    this.writer = writer;
  }

  public Destination(OutputStream stream) {
    this.stream = stream;
    this.writer = null;
  }

  public OutputStream stream() {
    return stream;
  }

  public Writer writer() {
    return writer;
  }

  public Writer createWriter() {
    if (writer != null) {
      return writer;
    } else {
      assert stream != null;
      return new OutputStreamWriter(stream);
    }
  }
}
