package com.github.lwhite1.tablesaw.store.debug;

import com.github.lwhite1.tablesaw.store.ColumnMetadata;
import com.github.lwhite1.tablesaw.store.TableMetadata;
import com.github.lwhite1.tablesaw.util.GuavaCollectors;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.github.lwhite1.tablesaw.store.debug.SizeUnit.*;

public final class StoreViewer {
  public static StoreDetail read(String src) throws IOException {
    Path dir = Paths.get(src);

    byte[] bytes = Files.readAllBytes(dir.resolve(TableMetadata.fileName));
    String json = new String(bytes, StandardCharsets.UTF_8);
    TableMetadata metadata = TableMetadata.fromJson(json);

    List<ColumnMetadata> cMeta = metadata.getColumnMetadataList();
    ImmutableList<ColumnDetail> xs = cMeta.stream().map(p -> {
      long length = dir.resolve(p.getId()).toFile().length();
      return new ColumnDetail(p.getName(), p.getType(), length);
    }).sorted((a, b) -> -Long.compare(a.sizeInBytes(), b.sizeInBytes()))
        .collect(GuavaCollectors.immutableList());

    long totalSize = xs.stream().mapToLong(ColumnDetail::sizeInBytes).sum();

    return new StoreDetail(xs.size(), metadata.getRowCount(), xs, totalSize);
  }

  public static void display(String pathToMetadataDir) throws IOException {
    StoreDetail detail = read(pathToMetadataDir);
    System.out.printf("Number of columns: %d, rows: %d%n", detail.columnCount(), detail.rowCount());
    System.out.println();
    detail.columnDetails().forEach(c -> System.out.println(c.name() + "[ "+c.columnType()+" ] --> " + toUnit(c.sizeInBytes())));
    System.out.println();
    System.out.println(toUnit(detail.totalSizeInBytes()));
  }

  private static String toUnit(long n) {
    SizeUnit unit = chooseUnit(n);
    double value = (double) n / unit.factor;
    return String.format("%.4g %s", value, unit.symbol);
  }

  private static SizeUnit chooseUnit(long bytes) {
    if (bytes > GB.factor) return GB;
    if (bytes > MB.factor) return MB;
    if (bytes > KB.factor) return KB;

    return Bytes;
  }
}
