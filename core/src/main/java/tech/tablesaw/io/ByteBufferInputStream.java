package tech.tablesaw.io;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferInputStream extends InputStream {

  private final ByteBuffer bb;

  public ByteBufferInputStream(ByteBuffer buffer) {
    this.bb = buffer;
  }

  @Override
  public int read() {
    if (bb.remaining() == 0) {
      return -1;
    }
    return bb.get();
  }

  @Override
  public int read(byte b[]) {
    return read(b, 0, b.length);
  }

  @Override
  public int read(byte b[], int off, int len) {
    int length = Math.min(bb.remaining(), len);
    if (length == 0) {
      return -1;
    }
    bb.get(b, off, length);
    return length;
  }

  public int available() {
    return bb.remaining();
  }
}
