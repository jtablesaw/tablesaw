package com.github.lwhite1.tablesaw.plotting;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public enum Hue {

  Red_2_5("2.5R"),
  Red_5("5R"),
  Red_7_5("7.5R"),
  Red_10("10R"),

  YellowRed_2_5("2.5YR"),
  YellowRed_5("5YR"),
  YellowRed_7_5("7.5YR"),
  YellowRed_10("10YR"),

  Yellow_2_5("2.5Y"),
  Yellow_5("5Y"),
  Yellow_7_5("7.5Y"),
  Yellow_10("10Y"),

  GreenYellow_2_5("2.5GY"),
  GreenYellow_5("5GY"),
  GreenYellow_7_5("7.5GY"),
  GreenYellow_10("10GY"),

  Green_2_5("2.5G"),
  Green_5("5G"),
  Green_7_5("7.5G"),
  Green_10("10G"),

  BlueGreen_2_5("2.5BG"),
  BlueGreen_5("5BG"),
  BlueGreen_7_5("7.5BG"),
  BlueGreen_10("10BG"),

  Blue_2_5("2.5B"),
  Blue_5("5B"),
  Blue_7_5("7.5B"),
  Blue_10("10B"),

  PurpleBlue_2_5("2.5PB"),
  PurpleBlue_5("5PB"),
  PurpleBlue_7_5("7.5PB"),
  PurpleBlue_10("10PB"),

  Purple_2_5("2.5P"),
  Purple_5("5P"),
  Purple_7_5("7.5P"),
  Purple_10("10P"),

  RedPurple_2_5("2.5RP"),
  RedPurple_5("5RP"),
  RedPurple_7_5("7.5RP"),
  RedPurple_10("10RP"),

  Neutral("N");

  private String colorString;

  Hue(String colorString) {
    this.colorString = colorString;
  }

  static Hue from(String hueString) {
    return STRING_HUE_HASH_MAP.get(hueString);
  }

  static final Map<String, Hue> STRING_HUE_HASH_MAP = new HashMap<>();

  static {
    for (Hue hue : Hue.values()) {
      STRING_HUE_HASH_MAP.put(hue.colorString, hue);
    }
  }
}
