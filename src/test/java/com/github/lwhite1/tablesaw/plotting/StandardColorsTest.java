package com.github.lwhite1.tablesaw.plotting;

import org.junit.Test;

import java.util.List;
import java.awt.*;


/**
 *
 */
public class StandardColorsTest {
  @Test
  public void testStandardColors() {
    List<Color> colors  = StandardColors.standardColors();
    System.out.println(colors);
  }

}