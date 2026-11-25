package tech.tablesaw.io;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/** Guesses the character encoding of a file by examining a sample of its bytes. */
final class FileCharsetDetector {

  /** The maximum number of bytes read from the start of the file to guess its charset. */
  private static final int MAX_SAMPLE_BYTES = 9999;

  /**
   * The minimum confidence (on a 1 to 100 scale) needed to trust a detected charset. Below this,
   * the system default charset is used instead.
   */
  private static final int MIN_CONFIDENCE = 60;

  private FileCharsetDetector() {}

  /**
   * Returns the likely charset for the given file, if it can be determined. A confidence score is
   * calculated. If the score is less than {@link #MIN_CONFIDENCE} the system default charset is
   * returned instead.
   *
   * @param file The file to be evaluated
   * @return The likely charset, or the system default charset
   */
  static Charset detect(File file) {
    long bufferSize = Math.min(file.length(), MAX_SAMPLE_BYTES);
    byte[] buffer = new byte[(int) bufferSize];
    try (InputStream initialStream = new FileInputStream(file)) {
      int bytesRead = initialStream.read(buffer);
      if (bytesRead < bufferSize) {
        throw new IOException("Was not able to read expected number of bytes");
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return detect(buffer);
  }

  /**
   * Returns the likely charset for the given byte[], if it can be determined. A confidence score is
   * calculated. If the score is less than {@link #MIN_CONFIDENCE} the system default charset is
   * returned instead.
   *
   * @param buffer The byte array to evaluate
   * @return The likely charset, or the system default charset
   */
  private static Charset detect(byte[] buffer) {
    CharsetDetector detector = new CharsetDetector();
    detector.setText(buffer);
    CharsetMatch match = detector.detect();
    if (match == null || match.getConfidence() < MIN_CONFIDENCE) {
      return Charset.defaultCharset();
    }
    return Charset.forName(match.getName());
  }
}
