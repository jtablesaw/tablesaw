/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.io.saw;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.strings.ByteDictionaryMap;
import tech.tablesaw.columns.strings.DictionaryMap;
import tech.tablesaw.columns.strings.IntDictionaryMap;
import tech.tablesaw.columns.strings.ShortDictionaryMap;

/** Data about a specific column used in it's persistence */
@Beta
public class ColumnMetadata {

  private String id;
  private String name;
  private String type;

  // number of unique values in column - not all columns will provide this as it can be expensive
  private int cardinality;

  // these attributes are specific to string columns
  private String stringColumnKeySize;
  private int nextStringKey;

  private int uncompressedByteSize;

  // these attributes are specific to boolean columns
  private int trueBytesLength;
  private int falseBytesLength;
  private int missingBytesLength;

  ColumnMetadata(Column<?> column) {
    this.id = SawUtils.makeName(column.name());
    this.name = column.name();
    this.type = column.type().name();
    if (column instanceof StringColumn) {
      StringColumn stringColumn = (StringColumn) column;
      cardinality = stringColumn.countUnique();
      DictionaryMap lookupTable = stringColumn.getDictionary();
      nextStringKey = lookupTable.nextKeyWithoutIncrementing();
      if (lookupTable.getClass().equals(IntDictionaryMap.class)) {
        stringColumnKeySize = Integer.class.getSimpleName();
      } else if (lookupTable.getClass().equals(ByteDictionaryMap.class)) {
        stringColumnKeySize = Byte.class.getSimpleName();
      } else if (lookupTable.getClass().equals(ShortDictionaryMap.class)) {
        stringColumnKeySize = Short.class.getSimpleName();
      } else {
        stringColumnKeySize = "";
      }
    } else {
      stringColumnKeySize = "";

      if (column instanceof BooleanColumn) {
        BooleanColumn bc = (BooleanColumn) column;
        trueBytesLength = bc.trueBytes().length;
        falseBytesLength = bc.falseBytes().length;
        missingBytesLength = bc.missingBytes().length;
        uncompressedByteSize = trueBytesLength + falseBytesLength + missingBytesLength;
      }
    }
  }

  /**
   * Constructs an instance of ColumnMetaData
   *
   * <p>NB: This constructor is used by Jackson JSON parsing code so it must be retained even though
   * it isn't explicitly called
   */
  protected ColumnMetadata() {}

  @Override
  public String toString() {
    return "ColumnMetadata{"
        + "id='"
        + id
        + '\''
        + ", name='"
        + name
        + '\''
        + ", type="
        + type
        + '}';
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public int getUncompressedByteSize() {
    return uncompressedByteSize;
  }

  public String getStringColumnKeySize() {
    return stringColumnKeySize;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ColumnMetadata that = (ColumnMetadata) o;
    return Objects.equal(getId(), that.getId())
        && Objects.equal(getName(), that.getName())
        && Objects.equal(getType(), that.getType())
        && Objects.equal(getStringColumnKeySize(), that.getStringColumnKeySize());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId(), getName(), getType(), getStringColumnKeySize());
  }

  public int getNextStringKey() {
    return nextStringKey;
  }

  public int getCardinality() {
    return cardinality;
  }

  /**
   * For BooleanColumn only Returns the length of the byte array used for true values before it is
   * compressed
   */
  public int getTrueBytesLength() {
    return trueBytesLength;
  }

  /**
   * For BooleanColumn only Returns the length of the byte array used for false values before it is
   * compressed
   */
  public int getFalseBytesLength() {
    return falseBytesLength;
  }

  /**
   * For BooleanColumn only Returns the length of the byte array used for missing values before it
   * is compressed
   */
  public int getMissingBytesLength() {
    return missingBytesLength;
  }

  public void setUncompressedByteSize(int uncompressedByteSize) {
    this.uncompressedByteSize = uncompressedByteSize;
  }
}
