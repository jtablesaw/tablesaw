package com.deathrayresearch.outlier.app.ui;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class StyleSheet {

  private Map<String, String> styleMap = new HashMap<>();


  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Map.Entry<String, String> entry : styleMap.entrySet()) {
      builder
          .append(entry.getKey())
          .append(": ")
          .append(entry.getValue());
    }
    return builder.toString();
  }
}
