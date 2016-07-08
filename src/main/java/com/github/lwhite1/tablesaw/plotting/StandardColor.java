package com.github.lwhite1.tablesaw.plotting;

import java.awt.*;

/**
 * A color scheme based on Munsell's color charts
 */
public class StandardColor {

  private final Hue hue;
  private final int chroma;
  private final int value;
  private final String hexColor;

  public StandardColor(String hue, int chroma, int value, String hexColor) {
    this.hue = Hue.from(hue);
    this.chroma = chroma;
    this.value = value;
    this.hexColor = hexColor;
  }

  public String hexColor() {
    return hexColor;
  }

  public int value() {
    return value;
  }

  public int chroma() {
    return chroma;
  }

  public Hue hue() {
    return hue;
  }

  public Color asColor() {
    return Color.decode(hexColor);
  }
}
