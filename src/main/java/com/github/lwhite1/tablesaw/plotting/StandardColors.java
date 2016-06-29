package com.github.lwhite1.tablesaw.plotting;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class StandardColors {

  static ImmutableList<StandardColor> neutralColors = ImmutableList.copyOf(loadNeutrals());

  static final ImmutableMultimap<Hue, StandardColor> colorMap = ImmutableMultimap.copyOf(loadColors());

  /**
   * Loads the standard colors from a file
   *
   * @return
   */
  static Multimap<Hue, StandardColor> loadColors() {

    Multimap<Hue, StandardColor> standards = LinkedListMultimap.create();

    String fileName = "colors";

    String[] nextLine;
    try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
      // Add the rows
      while ((nextLine = reader.readNext()) != null) {
        for (String colorData : nextLine) {
          String[] colorSplit = colorData.trim().split(" ");
          String vHue = colorSplit[0];
          String valueChroma = colorSplit[1];
          String[] valueAndChroma = valueChroma.split("/");
          int vValue = Integer.parseInt(valueAndChroma[0]);
          int vChroma = Integer.parseInt(valueAndChroma[1]);
          String vHex = colorSplit[2];

          StandardColor color = new StandardColor(vHue, vChroma, vValue, vHex);
          if (!color.hue().equals(Hue.Neutral)) {
            standards.put(color.hue(), color);
          }
        }
      }
    } catch (IOException ex) {
      throw new RuntimeException("Unable to read predefined colors file", ex);
    }
    return standards;
  }

  static List<StandardColor> loadNeutrals() {

    ArrayList<StandardColor> neutrals = new ArrayList<>();

    String fileName = "colors";

    String[] nextLine;
    try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
      // Add the rows
      while ((nextLine = reader.readNext()) != null) {
        for (String colorData : nextLine) {
          String[] colorSplit = colorData.trim().split(" ");
          String vHue = colorSplit[0];
          String valueChroma = colorSplit[1];
          String[] valueAndChroma = valueChroma.split("/");
          int vValue = Integer.parseInt(valueAndChroma[0]);
          int vChroma = Integer.parseInt(valueAndChroma[1]);
          String vHex = colorSplit[2];

          StandardColor color = new StandardColor(vHue, vChroma, vValue, vHex);
          if (color.hue().equals(Hue.Neutral)) {
            neutrals.add(color);
          }
        }
      }
    } catch (IOException ex) {
      throw new RuntimeException("Unable to read predefined colors file", ex);
    }
    return neutrals;
  }

  static ImmutableList<StandardColor> getNeutralColors() {
    return neutralColors;
  }

  static Set<Hue> getHues() {
    return colorMap.keySet();
  }

  static ImmutableCollection<StandardColor> getColors(Hue hue) {
    return colorMap.get(hue);
  }

  static Color[] allGreys() {
    List<Color> colors = new ArrayList<>(11);
    colors.add(Color.WHITE);
    for (StandardColor color : neutralColors) {
      colors.add(color.asColor());
    }
    colors.add(Color.BLACK);
    return colors.toArray(new Color[11]);
  }

  static Color[] ggPlotGreys() {
    List<Color> colors = new ArrayList<>(9);
    for (StandardColor color : neutralColors.subList(1, neutralColors.size())) {
      colors.add(color.asColor());
    }
    colors.add(Color.BLACK);
    return colors.toArray(new Color[9]);
  }

  static Color[] ggPlotGreys(int size) {
    size = Math.min(size, 9);
    List<Color> colors = new ArrayList<>(size);
    int start = (neutralColors.size() - size) + 1;
    for (StandardColor color : neutralColors.subList(start, neutralColors.size())) {
      colors.add(color.asColor());
    }
    colors.add(Color.BLACK);
    return colors.toArray(new Color[size]);
  }

  static Color[] ggPlotGreys6() {
    int size = 6;
    List<Color> colors = new ArrayList<>(size);
    colors.add(Color.WHITE);
    colors.add(neutralColors.get(2).asColor());
    colors.add(neutralColors.get(4).asColor());
    colors.add(neutralColors.get(6).asColor());
    colors.add(neutralColors.get(8).asColor());
    colors.add(Color.BLACK);
    return colors.toArray(new Color[size]);
  }
}
