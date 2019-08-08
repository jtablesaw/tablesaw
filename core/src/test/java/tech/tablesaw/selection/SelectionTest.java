package tech.tablesaw.selection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SelectionTest {

  @Test
  void with() {
    Selection selection = Selection.with(42, 53, 111);
    assertTrue(selection.contains(42));
    assertTrue(selection.contains(53));
    assertTrue(selection.contains(111));
    assertFalse(selection.contains(43));
    assertFalse(selection.contains(0));
    assertFalse(selection.contains(122));
  }

  @Test
  void withoutRange() {
    Selection selection = Selection.withoutRange(0, 130, 42, 53);
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
    Selection selection = Selection.withRange(42, 53);
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
    Selection selection = Selection.with(42, 53, 111);
    int[] expected = {42, 53, 111};
    assertArrayEquals(expected, selection.toArray());
  }

  @Test
  void add() {
    Selection selection = Selection.with(42, 53, 111);
    assertTrue(selection.contains(42));
    assertFalse(selection.contains(43));
    assertTrue(selection.add(43).contains(43));
  }

  @Test
  void addRange() {
    Selection selection = Selection.with(42, 53, 111);
    assertTrue(selection.contains(42));
    assertFalse(selection.contains(43));
    assertTrue(selection.addRange(70, 80).contains(73));
    assertTrue(selection.addRange(70, 80).contains(70));
    assertTrue(selection.addRange(70, 80).contains(79));
    assertFalse(selection.addRange(70, 80).contains(80));
  }

  @Test
  void size() {
    Selection selection = Selection.with(42, 53, 111);
    assertEquals(3, selection.size());
  }

  @Test
  void and() {
    Selection selection = Selection.with(42, 53, 111);
    Selection selection2 = Selection.with(11, 133, 53, 112);
    Selection selection3 = selection.and(selection2);
    assertEquals(1, selection3.size());
    assertEquals(53, selection3.get(0));
  }

  @Test
  void or() {
    Selection selection = Selection.with(42, 53, 111);
    Selection selection2 = Selection.with(11, 133, 53, 112);
    Selection selection3 = selection.or(selection2);
    assertEquals(6, selection3.size());
    assertEquals(11, selection3.get(0));
    assertEquals(42, selection3.get(1));
    assertTrue(selection3.contains(53));
  }

  @Test
  void andNot() {
    Selection selection = Selection.with(42, 53, 111);
    Selection selection2 = Selection.with(11, 133, 53, 112);
    Selection selection3 = selection.andNot(selection2);
    assertEquals(2, selection3.size());
    assertEquals(111, selection3.get(1));
    assertEquals(42, selection3.get(0));
    assertFalse(selection3.contains(53));
  }

  @Test
  void isEmpty() {
    Selection selection = Selection.with();
    assertTrue(selection.isEmpty());

    Selection selection1 = Selection.with(42, 53, 111);
    assertFalse(selection1.isEmpty());
  }

  @Test
  void clear() {
    Selection selection1 = Selection.with(42, 53, 111);
    assertFalse(selection1.isEmpty());

    selection1.clear();
    assertTrue(selection1.isEmpty());
  }

  @Test
  void get() {
    Selection selection = Selection.with(42, 53, 111);
    assertEquals(42, selection.get(0));
    assertEquals(53, selection.get(1));
  }

  @Test
  void remove() {
    Selection selection = Selection.with(42, 53, 111);
    assertTrue(selection.contains(53));

    selection = selection.removeRange(50, 69);
    assertFalse(selection.contains(53));
    assertTrue(selection.contains(111));
  }

  @Test
  void flip() {
    Selection selection = Selection.with(42, 53, 111);
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
}
