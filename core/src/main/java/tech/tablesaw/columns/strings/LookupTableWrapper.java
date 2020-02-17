package tech.tablesaw.columns.strings;

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

        for (Int2ObjectMap.Entry<String> entry : entries) {
          dos.writeInt(entry.getIntKey());
          dos.writeUTF(entry.getValue());
        }
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
      DataInputStream dis, String name, String dictionarySizeString, int columnSize) {

    StringColumn stringColumn;

    try {
      int stringCount = dis.readInt();

      if (dictionarySizeString.equals(Integer.class.getSimpleName())) {
        IntDictionaryMap dictionaryMap =
            (IntDictionaryMap) new ByteDictionaryMap().promoteYourself().promoteYourself();

        int j = 0;
        while (j < stringCount) {
          int key = dis.readInt();
          String value = dis.readUTF();
          dictionaryMap.updateMaps(key, value);
          j++;
        }
        // get the column entries
        int size = columnSize;
        for (int i = 0; i < size; i++) {
          dictionaryMap.addValue(dis.readInt());
        }
        stringColumn = StringColumn.createInternal(name, dictionaryMap);

      } else if (dictionarySizeString.equals(Short.class.getSimpleName())) {
        ShortDictionaryMap dictionaryMap =
            (ShortDictionaryMap) new ByteDictionaryMap().promoteYourself();
        int j = 0;
        while (j < stringCount) {
          short key = dis.readShort();
          String value = dis.readUTF();
          dictionaryMap.updateMaps(key, value);
          j++;
        }
        // get the column entries
        for (int i = 0; i < columnSize; i++) {
          dictionaryMap.addValue(dis.readShort());
        }
        stringColumn = StringColumn.createInternal(name, dictionaryMap);

      } else if (dictionarySizeString.equals(Byte.class.getSimpleName())) {
        ByteDictionaryMap dictionaryMap = new ByteDictionaryMap();
        int j = 0;
        while (j < stringCount) {
          byte key = dis.readByte();
          String value = dis.readUTF();
          dictionaryMap.updateMaps(key, value);
          j++;
        }
        // get the column entries
        for (int i = 0; i < columnSize; i++) {
          dictionaryMap.addValue(dis.readByte());
        }
        stringColumn = StringColumn.createInternal(name, dictionaryMap);
      } else {
        throw new IllegalArgumentException(
            "Unable to match the dictionary map type " + dictionarySizeString + " for StringColum");
      }

    } catch (IOException e) {
      throw new UncheckedIOException(
          "Failed reading " + name + " of type " + dictionarySizeString, e);
    }
    return stringColumn;
  }
}
