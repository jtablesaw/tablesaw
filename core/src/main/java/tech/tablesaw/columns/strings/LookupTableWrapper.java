package tech.tablesaw.columns.strings;

import com.google.common.base.Objects;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import tech.tablesaw.api.StringColumn;

/**
 * This class is strictly for Saw file IO. It wraps a dictionary map, exposing just a few methods,
 * in an attempt to make it less likely to be hacked directly
 */
public class LookupTableWrapper {

  private final DictionaryMap dictionaryMap;

  public LookupTableWrapper(DictionaryMap dictionaryMap) {
    this.dictionaryMap = dictionaryMap;
  }

  public LookupTableWrapper() {
    dictionaryMap = null;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(dictionaryMap);
  }

  /**
   * Writes the contents of the dictionaryMap to a stream in saw file format
   *
   * @param dos The stream to write on
   */
  public void writeToStream(DataOutputStream dos) {

    try {
      // write the number of unique strings
      dos.writeInt(dictionaryMap.countUnique());

      // write the strings
      if (dictionaryMap instanceof IntDictionaryMap) {
        IntDictionaryMap dictionary = (IntDictionaryMap) dictionaryMap;
        ObjectSet<Int2ObjectMap.Entry<String>> entries = dictionary.getKeyValueEntries();

        // write the unique strings and their keys, key first, then string, then next key, etc.
        for (Int2ObjectMap.Entry<String> entry : entries) {
          dos.writeInt(entry.getIntKey());
          dos.writeUTF(entry.getValue());
        }
        // write the individual keys. These represent the strings in their correct order
        for (int d : dictionary.values()) {
          dos.writeInt(d);
        }
      } else if (dictionaryMap instanceof ShortDictionaryMap) {
        ShortDictionaryMap dictionary = (ShortDictionaryMap) dictionaryMap;
        ObjectSet<Short2ObjectMap.Entry<String>> entries = dictionary.getKeyValueEntries();

        for (Short2ObjectMap.Entry<String> entry : entries) {
          dos.writeShort(entry.getShortKey());
          dos.writeUTF(entry.getValue());
        }
        for (short d : dictionary.values()) {
          dos.writeShort(d);
        }
      } else if (dictionaryMap instanceof ByteDictionaryMap) {
        ByteDictionaryMap dictionary = (ByteDictionaryMap) dictionaryMap;
        ObjectSet<Byte2ObjectMap.Entry<String>> entries = dictionary.getKeyValueEntries();

        for (Byte2ObjectMap.Entry<String> entry : entries) {
          dos.writeByte(entry.getByteKey());
          dos.writeUTF(entry.getValue());
        }
        for (byte d : dictionary.values()) {
          dos.writeByte(d);
        }
      }
      dos.flush();
    } catch (IOException exception) {
      throw new UncheckedIOException(exception);
    }
  }

  public Class<? extends DictionaryMap> dictionaryClass() {
    return dictionaryMap.getClass();
  }

  public StringColumn readFromStream(
      DataInputStream dis, String name, String keySize, int columnSize) {

    StringColumn stringColumn;

    try {
      // the first value in the stream holds the number of unique strings in the new column
      int uniqueStringCount = dis.readInt();

      if (keySize.equals(Integer.class.getSimpleName())) {
        stringColumn = createColumnUsingInts(dis, name, columnSize, uniqueStringCount);
      } else if (keySize.equals(Short.class.getSimpleName())) {
        stringColumn = createColumnUsingShorts(dis, name, columnSize, uniqueStringCount);
      } else if (keySize.equals(Byte.class.getSimpleName())) {
        stringColumn = createColumnUsingBytes(dis, name, columnSize, uniqueStringCount);
      } else {
        throw new IllegalArgumentException(
            "Invalid dictionary type " + keySize + " for StringColum");
      }
    } catch (IOException e) {
      throw new UncheckedIOException("Failed reading " + name + " of type " + keySize, e);
    }
    return stringColumn;
  }

  private StringColumn createColumnUsingInts(
      DataInputStream dis, String name, int columnSize, int uniqueStringCount) throws IOException {
    StringColumn stringColumn;
    IntDictionaryMap intDictionaryMap =
        (IntDictionaryMap) new ByteDictionaryMap().promoteYourself().promoteYourself();

    int j = 0;
    while (j < uniqueStringCount) {
      int key = dis.readInt();
      String value = dis.readUTF();
      intDictionaryMap.updateMapsFromSaw(key, value);
      j++;
    }
    // get the column entries
    for (int i = 0; i < columnSize; i++) {
      intDictionaryMap.addValueFromSaw(dis.readInt());
    }
    stringColumn = StringColumn.createInternal(name, intDictionaryMap);
    return stringColumn;
  }

  private StringColumn createColumnUsingBytes(
      DataInputStream dis, String name, int columnSize, int uniqueStringCount) throws IOException {
    StringColumn stringColumn;

    ByteDictionaryMap byteDictionaryMap = new ByteDictionaryMap();
    int j = 0;
    while (j < uniqueStringCount) {
      byte key = dis.readByte();
      String value = dis.readUTF();
      byteDictionaryMap.updateMapsFromSaw(key, value);
      j++;
    }
    // get the column entries
    for (int i = 0; i < columnSize; i++) {
      byteDictionaryMap.addValueFromSaw(dis.readByte());
    }
    stringColumn = StringColumn.createInternal(name, byteDictionaryMap);
    return stringColumn;
  }

  private StringColumn createColumnUsingShorts(
      DataInputStream dis, String name, int columnSize, int uniqueStringCount) throws IOException {
    StringColumn stringColumn;
    ShortDictionaryMap dictionaryMap =
        (ShortDictionaryMap) new ByteDictionaryMap().promoteYourself();

    int j = 0;
    while (j < uniqueStringCount) {
      short key = dis.readShort();
      String value = dis.readUTF();
      dictionaryMap.updateMapsFromSaw(key, value);
      j++;
    }
    // get the column entries
    for (int i = 0; i < columnSize; i++) {
      dictionaryMap.addValueFromSaw(dis.readShort());
    }
    stringColumn = StringColumn.createInternal(name, dictionaryMap);
    return stringColumn;
  }
}
