package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.mapper.StringIntMapper;
import com.deathrayresearch.outlier.mapper.StringStringMapper;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class TextColumnTest {

  @Test
  public void testMapper() {

    TextColumn column = TextColumn.create("text");
    column.add("   SKDLLKA DSFlkdsfsadFDDF  ");

    StringStringMapper mapper = input -> {
      String a = input.toLowerCase();
      return a.trim();
    };

    TextColumn result = column.collectIntoTextColumn("lowercase, trimmed", mapper);
    assertEquals("skdllka dsflkdsfsadfddf", result.first());
  }

  @Test
  public void testMapper2() {

    TextColumn column = TextColumn.create("text");
    column.add("   SKDLLKA DSFlkdsfsadFDDF  ");

    StringIntMapper trimmedLength = input -> {
      String a = input.trim();
      return a.length();
    };

    IntColumn result = column.collectIntoIntColumn("String length", trimmedLength);
    assertEquals(23, result.firstElement());
  }
}