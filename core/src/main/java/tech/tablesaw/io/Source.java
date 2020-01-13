package tech.tablesaw.io;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteStreams;
import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;

public class Source {

  protected final ByteBuffer byteBuffer;
  protected Charset charset;

  public Source(File file, Charset charset) throws IOException {
    this(getMappedByteBuffer(file), charset);
  }

  public Source(File file) throws IOException {
    this(getMappedByteBuffer(file));
  }

  private static MappedByteBuffer getMappedByteBuffer(File file) throws IOException {
    try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
      return raf.getChannel().map(MapMode.READ_ONLY, 0, raf.length());
    }
  }

  public Source(InputStream inputStream, Charset charset) throws IOException {
    this(ByteStreams.toByteArray(inputStream), charset);
  }

  public Source(InputStream inputStream) throws IOException {
    this(ByteStreams.toByteArray(inputStream));
  }

  public Source(byte[] bytes, Charset charset) {
    this(ByteBuffer.wrap(bytes), charset);
  }

  public Source(byte[] bytes) {
    this(ByteBuffer.wrap(bytes));
  }

  public Source(ByteBuffer byteBuffer) {
    this.byteBuffer = byteBuffer;
    this.charset = getCharSet(byteBuffer);
  }

  public Source(ByteBuffer byteBuffer, Charset charset) {
    this.byteBuffer = byteBuffer;
    this.charset = charset;
  }

  public static Source fromString(String s, Charset charset) {
    return new Source(s.getBytes(charset), charset);
  }

  public static Source fromString(String s) {
    return new Source(s.getBytes());
  }

  public static Source fromUrl(String url) throws IOException {
    return fromUrl(new URL(url));
  }

  public static Source fromUrl(String url, Charset charset) throws IOException {
    return fromUrl(new URL(url), charset);
  }

  public static Source fromUrl(URL url) throws IOException {
    return new Source(loadUrl(url));
  }

  public static Source fromUrl(URL url, Charset charset) throws IOException {
    return new Source(loadUrl(url), charset);
  }

  /**
   * Creates a Reader from the underlying byte buffer. The reader is configured with either the
   * charset provided during initialization or with a charset automatically inferred from the data.
   */
  public Reader createReader() {
    return new InputStreamReader(createInputStream(), this.charset());
  }

  /** Creates a InputStream from the underlying byte buffer. */
  public InputStream createInputStream() {
    byteBuffer.rewind();
    return new ByteBufferInputStream(byteBuffer);
  }

  /** Downloads the URL to returns the content as a byte array. */
  private static byte[] loadUrl(URL url) throws IOException {
    try (InputStream is = url.openStream()) {
      return ByteStreams.toByteArray(is);
    } catch (IOException e) {
      throw new IOException("Failed to load URL: " + url, e);
    }
  }

  /**
   * Returns the charset of the the underlying byte buffer. If the charset was not provided during
   * source initialization, we try to infer it automatically.
   */
  public Charset charset() {
    if (this.charset == null) {
      this.charset = getCharSet(byteBuffer);
    }
    return this.charset;
  }

  /**
   * Returns the likely charset for the given byte buffer, if it can be determined. A confidence
   * score is calculated. If the score is less than 60 (on a 1 to 100 interval) the system default
   * charset is returned instead.
   *
   * @param byteBuffer The byteBuffer to be evaluated
   * @return The likely charset, or the system default charset
   */
  @VisibleForTesting
  static Charset getCharSet(ByteBuffer byteBuffer) {
    byteBuffer.rewind();
    long bufferSize = byteBuffer.limit() < 9999 ? byteBuffer.limit() : 9999;
    byte[] buffer = new byte[(int) bufferSize];
    byteBuffer.get(buffer, 0, buffer.length);
    byteBuffer.rewind();
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
