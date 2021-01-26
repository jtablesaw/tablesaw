package tech.tablesaw.io;

import com.google.common.annotations.VisibleForTesting;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
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
import java.util.Scanner;

public class Source {

  // we always have one of these (file, reader, or inputStream)
  protected final File file;
  protected final Reader reader;
  protected final InputStream inputStream;
  protected final Charset charset;

  public Source(File file) {
    this(file, getCharSet(file));
  }

  public Source(File file, Charset charset) {
    this.file = file;
    this.reader = null;
    this.inputStream = null;
    this.charset = charset;
  }

  public Source(InputStreamReader reader) {
    this.file = null;
    this.reader = reader;
    this.inputStream = null;
    this.charset = Charset.forName(reader.getEncoding());
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

  public Charset getCharset() {
    return charset;
  }

  /**
   * If cachedBytes are not null, returns a Reader created from the cachedBytes. Otherwise, returns
   * a Reader from the underlying source.
   */
  public Reader createReader(byte[] cachedBytes) throws IOException {
    if (cachedBytes != null) {
      return charset != null
          ? new InputStreamReader(new ByteArrayInputStream(cachedBytes), charset)
          : new InputStreamReader(new ByteArrayInputStream(cachedBytes));
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
    try (Scanner scanner = new Scanner(new URL(url).openStream())) {
      scanner.useDelimiter("\\A"); // start of a string
      return scanner.hasNext() ? scanner.next() : "";
    }
  }

  /**
   * Returns the likely charset for the given file, if it can be determined. A confidence score is
   * calculated. If the score is less than 60 (on a 1 to 100 interval) the system default charset is
   * returned instead.
   *
   * @param file The file to be evaluated
   * @return The likely charset, or the system default charset
   */
  @VisibleForTesting
  static Charset getCharSet(File file) {
    long bufferSize = file.length() < 9999 ? file.length() : 9999;
    byte[] buffer = new byte[(int) bufferSize];
    try (InputStream initialStream = new FileInputStream(file)) {
      int bytesRead = initialStream.read(buffer);
      if (bytesRead < bufferSize) {
        throw new IOException("Was not able to read expected number of bytes");
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return getCharSet(buffer);
  }

  /**
   * Returns the likely charset for the given byte[], if it can be determined. A confidence score is
   * calculated. If the score is less than 60 (on a 1 to 100 interval) the system default charset is
   * returned instead.
   *
   * @param buffer The byte array to evaluate
   * @return The likely charset, or the system default charset
   */
  private static Charset getCharSet(byte[] buffer) {
    CharsetDetector detector = new CharsetDetector();
    detector.setText(buffer);
    CharsetMatch match = detector.detect();
    if (match == null || match.getConfidence() < 60) {
      return Charset.defaultCharset();
    }
    return Charset.forName(match.getName());
  }
}
