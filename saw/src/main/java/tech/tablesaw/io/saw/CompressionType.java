package tech.tablesaw.io.saw;

/** What compression algorithm was applied, or should be applied in reading/writing a Saw File */
public enum CompressionType {
  SNAPPY, // Google's Snappy compression algorithm
  NONE
}
