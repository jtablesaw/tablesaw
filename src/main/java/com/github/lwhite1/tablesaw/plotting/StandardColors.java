package com.github.lwhite1.tablesaw.plotting;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import com.opencsv.CSVReader;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
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

    String fileName = "colors.txt";

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

    String fileName = "colors.txt";

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

  public static List<StandardColor> getFilteredColors(Hue hue, Range<Integer> chromaRange, Range<Integer> valueRange) {
    List<StandardColor> filtered = new ArrayList<>();
    ImmutableCollection<StandardColor> colors = colorMap.get(hue);
    for (StandardColor color : colors) {
      if (chromaRange.contains(color.chroma()) && valueRange.contains(color.value())) {
        filtered.add(color);
      }
    }
    return filtered;
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

  /**
   * Returns n hues, chosen to maximize the visual distance between them. We exclude grays from the list
   */
  static List<Hue> randomHues(int n) {
    List<Hue> resultList = new ArrayList<>(n);
    int huesAvailable = Hue.values().length - 1; // we're going to take out the "neutral hue" aka gray
    int maxDistance = huesAvailable / n;
    Random random = new Random();
    int starting = random.nextInt(huesAvailable);
    List<Hue> hues = new ArrayList<>(huesAvailable);
    for (int i = starting; i < huesAvailable; i++) {
      if (Hue.values()[i] != Hue.Neutral) {
        hues.add(Hue.values()[i]);
      }
    }
    for (int i = 0; i < starting; i++) {
      if (Hue.values()[i] != Hue.Neutral) {
        hues.add(Hue.values()[i]);
      }
    }

    for (int j = 0; j < huesAvailable && resultList.size() < n; j = j + maxDistance) {
      resultList.add(hues.get(j));
    }
    return resultList;
  }

  public static List<Color> randomColors(int n) {
    Random random = new Random();

    List<Hue> hues = randomHues(n);
    List<Color> colors = new ArrayList<>(n);

    Range<Integer> chromaRange = Range.closed(4, 12);
    Range<Integer> valueRange = Range.closed(4, 8);

    for (Hue hue : hues) {
      List<StandardColor> standardColors = getFilteredColors(hue, chromaRange, valueRange);
      StandardColor randomColor = standardColors.get(random.nextInt(standardColors.size()));
      colors.add(randomColor.asColor());
    }
    return colors;
  }

  public static Color[] standardColorArray() {
    List<Color> standardColors = standardColors();
    return standardColors().toArray(new Color[standardColors.size()]);
  }

  public static List<Color> standardColors() {

    List<Color> colors = new ArrayList<>();
    colors.add(color(Hue.Red_5, 14, 4));
    colors.add(color(Hue.Yellow_2_5, 12, 8));
    colors.add(color(Hue.Purple_7_5, 10, 4));
    colors.add(color(Hue.YellowRed_5, 12, 7));
    colors.add(color(Hue.PurpleBlue_2_5, 6, 8));

    colors.add(color(Hue.Yellow_5, 4, 7));
    colors.add(color(Hue.GreenYellow_5, 2, 5));
    colors.add(color(Hue.Green_2_5, 10, 6));
    colors.add(color(Hue.RedPurple_5, 10, 7));
    colors.add(color(Hue.PurpleBlue_2_5, 8, 4));

    colors.add(color(Hue.Red_7_5, 8, 7));
    colors.add(color(Hue.PurpleBlue_2_5, 10, 4));
    colors.add(color(Hue.YellowRed_7_5, 12, 7));
    colors.add(color(Hue.RedPurple_7_5, 12, 4));
    colors.add(color(Hue.Yellow_10, 10, 8));

    colors.add(color(Hue.YellowRed_2_5, 8, 3));
    colors.add(color(Hue.GreenYellow_5, 10, 7));
    colors.add(color(Hue.Red_10, 14, 5));
    colors.add(color(Hue.GreenYellow_5, 4, 2));
    colors.add(allGreys()[2]);

    return colors;
  }

  @Nullable
  static Color color(Hue hue, int chroma, int value) {
    Collection<StandardColor> options = colorMap.get(hue);
    for (StandardColor standardColor : options) {
      if (standardColor.chroma() == chroma &&
          standardColor.value() == value) {
        return standardColor.asColor();
      }
    }
    return null;
  }
}
