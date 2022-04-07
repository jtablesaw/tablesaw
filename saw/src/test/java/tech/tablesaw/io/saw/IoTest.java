package tech.tablesaw.io.saw;

import com.google.common.base.Stopwatch;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

public class IoTest {

  private static int count = 10485760; // 10 MB

  public static void main(String[] args) throws Exception {
    int[] ints = new int[count];
    for (int i = 0; i < count; i++) {
      ints[i] = i;
    }

    RandomAccessFile memoryMappedFile = new RandomAccessFile("largeFile", "rw");

    // Mapping a file into memory

    MappedByteBuffer out =
        memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, count * 4L);
    Stopwatch stopwatch = Stopwatch.createStarted();
    // Writing into Memory Mapped File
    for (int i = 0; i < count; i++) {
      out.putInt(ints[i]);
    }
    out.force();
    System.out.println(
        "Writing to Memory Mapped File is completed " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

    // reading from memory file in Java
    for (int i = 0; i < 16; i++) {
      System.out.println(out.getInt(i * 4));
    }

    System.out.println("Reading from Memory Mapped File is completed");
  }
}
