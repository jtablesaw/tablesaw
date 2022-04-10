package tech.tablesaw.selection;

import static org.junit.jupiter.api.Assertions.*;

import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import org.junit.jupiter.api.Test;

class BitSetSelectionTest {

  @Test
  void with() {
    Selection selection = BitSetBackedSelection.with(42, 53, 111);
    assertTrue(selection.contains(42));
    assertTrue(selection.contains(53));
    assertTrue(selection.contains(111));
    assertFalse(selection.contains(43));
    assertFalse(selection.contains(0));
    assertFalse(selection.contains(122));
  }

  @Test
  void withoutRange() {
    Selection selection = BitSetBackedSelection.withoutRange(0, 130, 42, 53);
    assertFalse(selection.contains(42));
    assertFalse(selection.contains(43));
    assertFalse(selection.contains(52));
    assertTrue(selection.contains(53));
    assertTrue(selection.contains(111));
    assertTrue(selection.contains(0));
    assertTrue(selection.contains(122));
  }

  @Test
  void withRange() {
    Selection selection = BitSetBackedSelection.withRange(42, 53);
    assertTrue(selection.contains(42));
    assertTrue(selection.contains(43));
    assertTrue(selection.contains(52));
    assertFalse(selection.contains(53));
    assertFalse(selection.contains(111));
    assertFalse(selection.contains(0));
    assertFalse(selection.contains(122));
  }

  @Test
  void toArray() {
    Selection selection = BitSetBackedSelection.with(42, 53, 111);
    int[] expected = {42, 53, 111};
    assertArrayEquals(expected, selection.toArray());
  }

  @Test
  void add() {
    Selection selection = BitSetBackedSelection.with(42, 53, 111);
    assertTrue(selection.contains(42));
    assertFalse(selection.contains(43));
    assertTrue(selection.add(43).contains(43));
  }

  @Test
  void addRange() {
    Selection selection = BitSetBackedSelection.with(42, 53, 111);
    assertTrue(selection.contains(42));
    assertFalse(selection.contains(43));
    assertTrue(selection.addRange(70, 80).contains(73));
    assertTrue(selection.addRange(70, 80).contains(70));
    assertTrue(selection.addRange(70, 80).contains(79));
    assertFalse(selection.addRange(70, 80).contains(80));
  }

  @Test
  void size() {
    Selection selection = BitSetBackedSelection.with(42, 53, 111);
    assertEquals(3, selection.size());
  }

  @Test
  void and() {
    Selection selection = BitSetBackedSelection.with(42, 53, 111);
    Selection selection2 = BitSetBackedSelection.with(11, 133, 53, 112);
    Selection selection3 = selection.and(selection2);
    assertEquals(1, selection3.size());
    assertEquals(53, selection3.get(0));
  }

  /** Tests and where one selection uses BitSet and the other RoaringBitmap */
  @Test
  void roaringAnd() {
    Selection selection = BitmapBackedSelection.with(42, 53, 111);
    Selection selection2 = BitSetBackedSelection.with(11, 133, 53, 112);
    Selection selection3 = selection.and(selection2);
    assertEquals(1, selection3.size());
    assertEquals(53, selection3.get(0));
  }

  /** Tests and where one selection uses BitSet and the other RoaringBitmap */
  @Test
  void roaringAnd2() {
    Selection selection = BitSetBackedSelection.with(42, 53, 111);
    Selection selection2 = BitmapBackedSelection.with(11, 133, 53, 112);
    Selection selection3 = selection.and(selection2);
    assertEquals(1, selection3.size());
    assertEquals(53, selection3.get(0));
  }

  @Test
  void or() {
    Selection selection = BitSetBackedSelection.with(42, 53, 111);
    Selection selection2 = BitSetBackedSelection.with(11, 133, 53, 112);
    Selection selection3 = selection.or(selection2);
    assertEquals(6, selection3.size());
    assertEquals(11, selection3.get(0));
    assertEquals(42, selection3.get(1));
    assertTrue(selection3.contains(53));
  }

  /** Tests or where one selection uses BitSet and the other RoaringBitmap */
  @Test
  void roaringOr() {
    Selection selection = BitSetBackedSelection.with(42, 53, 111);
    Selection selection2 = BitmapBackedSelection.with(11, 133, 53, 112);
    Selection selection3 = selection.or(selection2);
    assertEquals(6, selection3.size());
    assertEquals(11, selection3.get(0));
    assertEquals(42, selection3.get(1));
    assertTrue(selection3.contains(53));
  }

  /** Tests andNot where one selection uses BitSet and the other RoaringBitmap */
  @Test
  void roaringAndNot() {
    Selection selection = BitSetBackedSelection.with(42, 53, 111);
    Selection selection2 = BitmapBackedSelection.with(11, 133, 53, 112);
    Selection selection3 = selection.andNot(selection2);
    assertEquals(2, selection3.size());
    assertEquals(111, selection3.get(1));
    assertEquals(42, selection3.get(0));
    assertFalse(selection3.contains(53));
  }

  @Test
  void andNot() {
    Selection selection = BitSetBackedSelection.with(42, 53, 111);
    Selection selection2 = BitSetBackedSelection.with(11, 133, 53, 112);
    Selection selection3 = selection.andNot(selection2);
    assertEquals(2, selection3.size());
    assertEquals(111, selection3.get(1));
    assertEquals(42, selection3.get(0));
    assertFalse(selection3.contains(53));
  }

  @Test
  void isEmpty() {
    Selection selection = BitSetBackedSelection.with();
    assertTrue(selection.isEmpty());

    Selection selection1 = BitSetBackedSelection.with(42, 53, 111);
    assertFalse(selection1.isEmpty());
  }

  @Test
  void clear() {
    Selection selection1 = BitSetBackedSelection.with(42, 53, 111);
    assertFalse(selection1.isEmpty());

    selection1.clear();
    assertTrue(selection1.isEmpty());
  }

  @Test
  void get() {
    Selection selection = BitSetBackedSelection.with(42, 53, 111);
    assertEquals(42, selection.get(0));
    assertEquals(53, selection.get(1));
  }

  @Test
  void remove() {
    Selection selection = BitSetBackedSelection.with(42, 53, 111);
    assertTrue(selection.contains(53));

    selection = selection.removeRange(50, 69);
    assertFalse(selection.contains(53));
    assertTrue(selection.contains(111));
  }

  @Test
  void flip() {
    Selection selection = BitSetBackedSelection.with(42, 53, 111);
    assertTrue(selection.contains(53));
    assertTrue(selection.contains(42));
    assertTrue(selection.contains(111));

    selection = selection.flip(0, 124);
    assertFalse(selection.contains(53));
    assertFalse(selection.contains(42));
    assertFalse(selection.contains(111));
    assertTrue(selection.contains(0));
    assertTrue(selection.contains(110));
    assertTrue(selection.contains(112));
  }

  @Test
  void testConstructor1() {
    BitSetBackedSelection selection = new BitSetBackedSelection(10);
    assertEquals(-1, selection.bitSet().nextSetBit(0));
  }

  @Test
  void testConstructor2() {
    int[] values = {4, 11, 19};
    BitSetBackedSelection selection = new BitSetBackedSelection(values);
    assertEquals(4, selection.bitSet().nextSetBit(0));
    assertEquals(11, selection.bitSet().nextSetBit(5));
    assertEquals(19, selection.bitSet().nextSetBit(12));
  }

  @Test
  void testConstructor3() {
    BitSet bitSet = new BitSet(3);
    bitSet.set(2);
    BitSetBackedSelection selection = new BitSetBackedSelection(bitSet);
    assertEquals(2, selection.bitSet().nextSetBit(0));
  }

  @Test
  void testToString() {
    int[] values = {4, 11, 19};
    BitSetBackedSelection selection = new BitSetBackedSelection(values);
    assertEquals("Selection of size: 3", selection.toString());
  }

  @Test
  void testEquals() {
    int[] values = {4, 11, 19};
    BitSetBackedSelection selection1 = new BitSetBackedSelection(values);
    BitSetBackedSelection selection2 = new BitSetBackedSelection(values.clone());
    assertEquals(selection1, selection2);
    assertEquals(selection1.hashCode(), selection2.hashCode());
  }

  @Test
  void selectNRandomRows() {
    BitSetBackedSelection random = BitSetBackedSelection.selectNRowsAtRandom(4, 40);
    assertEquals(4, random.size());
  }

  @Test
  void iterator() {
    BitSetBackedSelection random = BitSetBackedSelection.selectNRowsAtRandom(4, 40);
    List<Integer> elements = new ArrayList<>();
    IntIterator it = random.iterator();
    while (it.hasNext()) {
      elements.add(it.nextInt());
    }
    assertEquals(4, elements.size());
  }
}
