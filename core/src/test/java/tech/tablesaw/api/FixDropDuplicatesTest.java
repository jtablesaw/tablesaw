package tech.tablesaw.api;

import java.io.File;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import tech.tablesaw.io.csv.CsvReadOptions;

class FixDropDuplicatesTest {

    private static final String SOURCE_FILE_NAME = "../data/missing_values.csv";
    private static Table testTable;
    
    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        testTable = Table.read().usingOptions(CsvReadOptions
            .builder(new File(SOURCE_FILE_NAME))
            .missingValueIndicator("-"));    }

    @Test
    void test() throws Exception {
        Method privateMethod = Table.class.getDeclaredMethod("isDuplicate", Row.class, Int2ObjectMap.class);
        privateMethod.setAccessible(true);
        Int2ObjectMap<IntArrayList> uniqueHashes = new Int2ObjectOpenHashMap<>();
        Row row0 = testTable.row(0);
        IntArrayList value = new IntArrayList(new int[] {1, 0});
        uniqueHashes.put(row0.rowHash(), value);
        boolean isDuplicate = (boolean) privateMethod.invoke(testTable, row0, uniqueHashes);
        assertTrue(isDuplicate, "Duplicate row not found");
    }

}
