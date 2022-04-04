package tech.tablesaw.io.saw;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import tech.tablesaw.api.Table;

/** All metadata used in the storage of one table */
public class SawMetadata {

  // The name of the file that this data is written to
  static final String METADATA_FILE_NAME = "Metadata.json";

  // The version of the Saw Storage system used to write the file
  private static final int SAW_VERSION = 3;

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private TableMetadata tableMetadata;
  private int version;
  private CompressionType compressionType;
  private EncryptionType encryptionType;

  /**
   * Returns a SawMetadata instance derived from the json-formatted Metadata.json file in the
   * directory specified by sawPath
   *
   * @param sawPath The path to the folder containing the Saw metadata file and table data
   */
  static SawMetadata readMetadata(Path sawPath) {

    Path resolvePath = sawPath.resolve(METADATA_FILE_NAME);
    byte[] encoded;
    try {
      encoded = Files.readAllBytes(resolvePath);
    } catch (IOException e) {
      throw new UncheckedIOException(
          "Unable to read Saw Metadata file at " + resolvePath.toString(), e);
    }
    return SawMetadata.fromJson(new String(encoded, StandardCharsets.UTF_8));
  }

  public SawMetadata(Table table, SawWriteOptions options) {
    this.tableMetadata = new TableMetadata(table);
    this.version = SAW_VERSION;
    this.compressionType = options.getCompressionType();
    this.encryptionType = options.getEncryptionType();
  }

  /** Default constructor for Jackson json serialization */
  protected SawMetadata() {}

  /**
   * Returns an instance of TableMetadata constructed from the provided json string
   *
   * @param jsonString A json-formatted String consistent with those output by the toJson() method
   */
  static SawMetadata fromJson(String jsonString) {
    try {
      return objectMapper.readValue(jsonString, SawMetadata.class);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Returns a JSON string that represents this object
   *
   * @see static methdod fromJson() which constructs a SawMetadata object from this JSON output
   */
  String toJson() {
    try {
      return objectMapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  public TableMetadata getTableMetadata() {
    return tableMetadata;
  }

  /** Returns the saw file format version used to create this file */
  public int getVersion() {
    return version;
  }

  public CompressionType getCompressionType() {
    return compressionType;
  }

  public EncryptionType getEncryptionType() {
    return encryptionType;
  }

  @JsonIgnore
  public List<ColumnMetadata> getColumnMetadataList() {
    return tableMetadata.getColumnMetadataList();
  }

  @JsonIgnore
  public Table structure() {
    return tableMetadata.structure();
  }

  @JsonIgnore
  public String getTableName() {
    return tableMetadata.getName();
  }

  @JsonIgnore
  public List<String> columnNames() {
    return tableMetadata.columnNames();
  }

  @JsonIgnore
  public int getRowCount() {
    return tableMetadata.getRowCount();
  }

  public int columnCount() {
    return tableMetadata.columnCount();
  }

  public String shape() {
    return tableMetadata.shape();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SawMetadata that = (SawMetadata) o;
    return getVersion() == that.getVersion()
        && Objects.equal(getTableMetadata(), that.getTableMetadata())
        && getCompressionType() == that.getCompressionType();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getTableMetadata(), getVersion(), getCompressionType());
  }
}
