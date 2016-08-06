package com.github.lwhite1.tablesaw.plotting;

import org.junit.Test;

import java.util.List;
import java.awt.*;

import static org.junit.Assert.assertFalse;


/**
 *
 */
public class StandardColorsTest {
  @Test
  public void testStandardColors() {
    List<Color> colors  = StandardColors.standardColors();
    assertFalse(colors.isEmpty());
    //System.out.println(colors);
  }

}